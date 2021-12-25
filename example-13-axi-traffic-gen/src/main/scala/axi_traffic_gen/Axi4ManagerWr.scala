/*
Copyright (c) 2021 Jan Marjanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package axi_traffic_gen

import axi_traffic_gen.AxiTGConfig.{NR_BEATS_PER_BURST, NR_BURSTS_IN_FLIGHT}
import bfmtester._
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class Axi4ManagerWr(addr_w: Int, data_w: Int, id_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)

    val config_axi_cache = Input(UInt(4.W))
    val config_axi_prot = Input(UInt(3.W))
    val config_axi_user = Input(UInt(2.W))

    val wr_cmd = new Axi4ManagerWrCmd(addr_w)
    val done_clear = Input(Bool())

    val diag_cntr_wr_cyc = Output(UInt(32.W))
  })

  // defaults
  io.m.AW.bits.len := (NR_BEATS_PER_BURST - 1).U(8.W)
  io.m.AW.bits.size := 4.U // 16 bytes
  io.m.AW.bits.burst := AxiIfBurstType.BURST
  io.m.AW.bits.lock := 0.U
  io.m.AW.bits.cache := io.config_axi_cache
  io.m.AW.bits.prot := io.config_axi_prot
  io.m.AW.bits.qos := 0.U
  io.m.AW.bits.user := io.config_axi_user
  io.m.B.ready := true.B

  // output regs
  val awaddr_reg = Reg(UInt(addr_w.W))
  val awvalid_reg = RegInit(Bool(), false.B)
  io.m.AW.bits.addr := awaddr_reg
  io.m.AW.bits.id := 0.U
  io.m.AW.valid := awvalid_reg

  // tx in flight
  val tx_in_flight_inc = Wire(Bool())
  val tx_in_flight_dec = Wire(Bool())
  val tx_in_flight = UpDownCounter(0 to NR_BURSTS_IN_FLIGHT, tx_in_flight_inc, tx_in_flight_dec)

  // state machine
  object StateWrAddr extends ChiselEnum {
    val Idle, WrAddr, WrRespWait, Done = Value
  }

  val state_wr_addr = RegInit(StateWrAddr.Idle)
  val rem_addrs = Reg(UInt(32.W))

  switch(state_wr_addr) {
    is(StateWrAddr.Idle) {
      when(io.wr_cmd.valid) {
        rem_addrs := io.wr_cmd.len - 1.U
        awaddr_reg := io.wr_cmd.addr
        awvalid_reg := true.B
        state_wr_addr := StateWrAddr.WrAddr
      }
    }
    is(StateWrAddr.WrAddr) {
      when(io.m.AW.ready) {
        awaddr_reg := awaddr_reg + (NR_BEATS_PER_BURST * data_w / 8).U
        rem_addrs := rem_addrs - 1.U

        when(rem_addrs === 0.U) {
          state_wr_addr := StateWrAddr.Done
          awvalid_reg := false.B
        }.elsewhen(tx_in_flight.is_almost_full()) {
          awvalid_reg := false.B
          state_wr_addr := StateWrAddr.WrRespWait
        }

      }
    }
    is(StateWrAddr.WrRespWait) {
      when(!tx_in_flight.is_full()) {
        awvalid_reg := true.B
        state_wr_addr := StateWrAddr.WrAddr
      }
    }
    is(StateWrAddr.Done) {
      when(io.done_clear) {
        state_wr_addr := StateWrAddr.Idle
      }
    }
  }

  tx_in_flight_inc := io.m.AW.ready && io.m.AW.valid
  tx_in_flight_dec := io.m.B.valid && io.m.B.ready

  //==========================================================

  io.wr_cmd.ready := state_wr_addr === StateWrAddr.Idle
  io.wr_cmd.done := state_wr_addr === StateWrAddr.Done

  //==========================================================

  // constant outptus
  io.m.W.bits.strb := -1.S(io.m.W.bits.strb.getWidth.W).asUInt()
  io.m.W.bits.user := 0.U

  // output regs
  val wdata_reg = Reg(UInt(data_w.W))
  val wlast_reg = RegInit(Bool(), false.B)
  io.m.W.bits.data := wdata_reg
  io.m.W.bits.last := wlast_reg

  // state machine
  object StateWrData extends ChiselEnum {
    val Idle, WrData, Done = Value
  }

  val state_wr_data = RegInit(StateWrData.Idle)
  val wr_beat_cntr = Reg(UInt(2.W))
  val wr_total_cntr = Reg(UInt(32.W))

  switch(state_wr_data) {
    is(StateWrData.Idle) {
      when(io.wr_cmd.valid) {
        wr_total_cntr := io.wr_cmd.len * NR_BEATS_PER_BURST.U - 1.U
        wr_beat_cntr := 0.U
        wdata_reg := 0.U
        state_wr_data := StateWrData.WrData
      }
    }
    is(StateWrData.WrData) {
      when(io.m.W.ready) {
        wr_total_cntr := wr_total_cntr - 1.U
        wdata_reg := wdata_reg + 1.U
        wr_beat_cntr := wr_beat_cntr + 1.U
        when(wr_total_cntr === 0.U) {
          state_wr_data := StateWrData.Done
        }
        when(wr_beat_cntr === 2.U) {
          wlast_reg := true.B
          //state_wr_data := StateWrData.WrRespWait
        }.otherwise {
          wlast_reg := false.B
        }
      }
    }
    is(StateWrData.Done) {
      when(io.done_clear) {
        state_wr_data := StateWrData.Idle
      }
    }
  }

  io.m.W.valid := state_wr_data === StateWrData.WrData

  //==========================================================
  // statistics

  val wr_cyc_cntr_act = RegInit(false.B)
  val wr_cyc_cntr = Counter(wr_cyc_cntr_act, (Math.pow(2, 32) - 1).toInt)

  when(io.wr_cmd.valid) {
    wr_cyc_cntr_act := true.B
    wr_cyc_cntr._1 := 0.U
  }.elsewhen(
    state_wr_addr === StateWrAddr.Done && state_wr_data === StateWrData.Done && tx_in_flight
      .is_empty()
  ) {
    wr_cyc_cntr_act := false.B
  }

  io.diag_cntr_wr_cyc := wr_cyc_cntr._1

  // tie-offs
  io.m.AR := DontCare
  io.m.R := DontCare
}
