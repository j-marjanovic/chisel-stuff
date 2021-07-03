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

    val diag_cntr_rd = Output(UInt(10.W))
  })

  val NR_BEATS: Int = 4

  // defaults
  io.m.R.ready := true.B // we are always ready!
  io.m.AR.bits.len := (NR_BEATS - 1).U(8.W)
  io.m.AR.bits.size := 4.U // 16 bytes
  io.m.AR.bits.burst := 1.U // INCR
  io.m.AR.bits.lock := 0.U
  io.m.AR.bits.cache := io.config_axi_cache
  io.m.AR.bits.prot := io.config_axi_prot
  io.m.AR.bits.qos := 0.U
  io.m.AR.bits.user := io.config_axi_user

  // output regs
  val raddr_reg = Reg(UInt(addr_w.W))
  val arid_reg = Reg(UInt(id_w.W))
  val arvalid_reg = RegInit(Bool(), false.B)
  io.m.AR.bits.addr := raddr_reg
  io.m.AR.bits.id := arid_reg
  io.m.AR.valid := arvalid_reg

  // tx in flight
  val tx_in_flight = UpDownCounter(0 until 3)

  when(io.m.R.valid && io.m.R.ready && io.m.R.bits.last) {
    tx_in_flight.dec()
  }

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
        arid_reg := 0.U
        arvalid_reg := true.B
        state_rd := StateRd.RdAddr
      }
    }
    is(StateRd.RdAddr) {
      when(io.m.AR.ready) {
        tx_in_flight.inc()
        raddr_reg := raddr_reg + (NR_BEATS * data_w / 8).U
        arid_reg := arid_reg + 1.U
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

  io.rd_cmd.ready := state_rd === StateRd.Idle
  io.rd_cmd.done := state_rd === StateRd.Done

  //==========================================================

  // TODO: process data

  // TODO
  io.diag_cntr_rd := 0.U

  // tie-offs
  io.m.AW := DontCare
  io.m.W := DontCare
  io.m.B := DontCare
}
