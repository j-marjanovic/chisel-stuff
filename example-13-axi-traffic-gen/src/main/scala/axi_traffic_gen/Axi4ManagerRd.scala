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

class Axi4ManagerRd(addr_w: Int, data_w: Int, id_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)

    val config_axi_cache = Input(UInt(4.W))
    val config_axi_prot = Input(UInt(3.W))
    val config_axi_user = Input(UInt(2.W))

    val rd_cmd = new Axi4ManagerRdCmd(addr_w)
    val done_clear = Input(Bool())

    val diag_cntr_rd_cyc = Output(UInt(32.W))
    val diag_cntr_rd_ok = Output(UInt(32.W))
  })

  // defaults
  io.m.R.ready := true.B // we are always ready!
  io.m.AR.bits.len := (NR_BEATS_PER_BURST - 1).U(8.W)
  io.m.AR.bits.size := 4.U // 16 bytes
  io.m.AR.bits.burst := AxiIfBurstType.BURST
  io.m.AR.bits.lock := 0.U
  io.m.AR.bits.cache := io.config_axi_cache
  io.m.AR.bits.prot := io.config_axi_prot
  io.m.AR.bits.qos := 0.U
  io.m.AR.bits.user := io.config_axi_user

  // output regs
  val raddr_reg = Reg(UInt(addr_w.W))
  val arvalid_reg = RegInit(Bool(), false.B)
  io.m.AR.bits.addr := raddr_reg
  io.m.AR.bits.id := 0.U
  io.m.AR.valid := arvalid_reg

  // tx in flight
  val tx_in_flight_inc = Wire(Bool())
  val tx_in_flight_dec = Wire(Bool())
  val tx_in_flight = UpDownCounter(0 to NR_BURSTS_IN_FLIGHT, tx_in_flight_inc, tx_in_flight_dec)

  // state machine
  object StateRd extends ChiselEnum {
    val Idle, RdAddr, RespWait, Done = Value
  }

  val state_rd = RegInit(StateRd.Idle)
  val rem_addrs = Reg(UInt(32.W))

  switch(state_rd) {
    is(StateRd.Idle) {
      when(io.rd_cmd.valid) {
        rem_addrs := io.rd_cmd.len - 1.U
        raddr_reg := io.rd_cmd.addr
        arvalid_reg := true.B
        state_rd := StateRd.RdAddr
      }
    }
    is(StateRd.RdAddr) {
      when(io.m.AR.ready) {
        raddr_reg := raddr_reg + (NR_BEATS_PER_BURST * data_w / 8).U
        rem_addrs := rem_addrs - 1.U

        when(rem_addrs === 0.U) {
          state_rd := StateRd.Done
          arvalid_reg := false.B
        }.elsewhen(tx_in_flight.is_almost_full()) {
          arvalid_reg := false.B
          state_rd := StateRd.RespWait
        }
      }
    }
    is(StateRd.RespWait) {
      when(!tx_in_flight.is_full()) {
        arvalid_reg := true.B
        state_rd := StateRd.RdAddr
      }
    }
    is(StateRd.Done) {
      when(io.done_clear) {
        state_rd := StateRd.Idle
      }
    }
  }

  tx_in_flight_dec := io.m.R.valid && io.m.R.ready && io.m.R.bits.last
  tx_in_flight_inc := io.m.AR.ready && io.m.AR.valid

  io.rd_cmd.ready := state_rd === StateRd.Idle
  io.rd_cmd.done := state_rd === StateRd.Done

  //==========================================================

  val rem_beats = Reg(UInt(32.W))
  val ref_word = Reg(UInt(data_w.W))
  val rd_cntr_ok = Reg(UInt(32.W))

  when(io.rd_cmd.valid) {
    rem_beats := io.rd_cmd.len * NR_BEATS_PER_BURST.U - 1.U
    ref_word := 0.U
    rd_cntr_ok := 0.U
  }.elsewhen(io.m.R.valid) {
    rem_beats := rem_beats - 1.U
    ref_word := ref_word + 1.U
    when(io.m.R.bits.data === ref_word) {
      rd_cntr_ok := rd_cntr_ok + 1.U
    }
  }

  io.diag_cntr_rd_ok := rd_cntr_ok

  //==========================================================
  // statistics

  val rd_cyc_cntr_act = RegInit(false.B)
  val rd_cyc_cntr = Counter(rd_cyc_cntr_act, (Math.pow(2, 32) - 1).toInt)

  when(io.rd_cmd.valid) {
    rd_cyc_cntr_act := true.B
    rd_cyc_cntr._1 := 0.U
  }.elsewhen(state_rd === StateRd.Done && rem_beats === 0.U) {
    rd_cyc_cntr_act := false.B
  }

  io.diag_cntr_rd_cyc := rd_cyc_cntr._1

  //==========================================================
  // tie-offs
  io.m.AW := DontCare
  io.m.W := DontCare
  io.m.B := DontCare
}
