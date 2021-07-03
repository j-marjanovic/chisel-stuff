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

    val diag_cntr_rd = Output(UInt(10.W))
  })

  val raddr_reg = Reg(UInt(addr_w.W))
  val rdata_reg = Reg(UInt(data_w.W))
  val rd_done_reg = RegInit(Bool(), false.B)

  object StateRd extends ChiselEnum {
    val Idle, RdAddr, RdData = Value
  }

  val state_rd = RegInit(StateRd.Idle)

  val cntr_read = Reg(UInt(3.W))

  rd_done_reg := false.B

  switch(state_rd) {
    is(StateRd.Idle) {
      cntr_read := 0.U
      when(io.rd_cmd.valid) {
        state_rd := StateRd.RdAddr
        raddr_reg := io.rd_cmd.addr
      }
    }
    is(StateRd.RdAddr) {
      when(io.m.AR.ready) {
        state_rd := StateRd.RdData
      }
    }
    is(StateRd.RdData) {
      when(io.m.R.valid) {
        cntr_read := cntr_read + 1.U

        when (cntr_read === 0.U) {
          rdata_reg := io.m.R.bits.data
        }

        when(io.m.R.bits.last) {
          rd_done_reg := true.B
          state_rd := StateRd.Idle
        }
      }
    }
  }

  // just to make Chisel happy
  io.m.AR.valid := false.B
  io.m.R.ready := false.B
  io.m.AR.bits.id := 0.U
  io.m.AR.bits.addr := 0.U
  io.m.AR.bits.len := 3.U
  io.m.AR.bits.size := 4.U
  io.m.AR.bits.burst := 1.U
  io.m.AR.bits.lock := 0.U
  io.m.AR.bits.cache := io.config_axi_cache
  io.m.AR.bits.prot := io.config_axi_prot
  io.m.AR.bits.qos := 0.U
  io.m.AR.bits.user := io.config_axi_user

  switch(state_rd) {
    is(StateRd.Idle) {
      io.m.AR.valid := false.B
      io.m.AR.bits.id := 0.U
      io.m.AR.bits.addr := 0.U
      io.m.AR.bits.len := 3.U
      io.m.AR.bits.size := 4.U
      io.m.AR.bits.burst := 1.U
      io.m.AR.bits.lock := 0.U
      io.m.AR.bits.cache := io.config_axi_cache
      io.m.AR.bits.prot := io.config_axi_prot
      io.m.AR.bits.qos := 0.U
      io.m.AR.bits.user := io.config_axi_user
      io.m.R.ready := false.B
    }
    is(StateRd.RdAddr) {
      io.m.AR.valid := true.B
      io.m.AR.bits.id := 0.U
      io.m.AR.bits.addr := raddr_reg
      io.m.AR.bits.len := 3.U
      io.m.AR.bits.size := 4.U
      io.m.AR.bits.burst := 1.U
      io.m.AR.bits.lock := 0.U
      io.m.AR.bits.cache := io.config_axi_cache
      io.m.AR.bits.prot := io.config_axi_prot
      io.m.AR.bits.qos := 0.U
      io.m.AR.bits.user := io.config_axi_user
      io.m.R.ready := false.B
    }
    is(StateRd.RdData) {
      io.m.AR.valid := false.B
      io.m.AR.bits.id := 0.U
      io.m.AR.bits.addr := 0.U
      io.m.AR.bits.len := 3.U
      io.m.AR.bits.size := 4.U
      io.m.AR.bits.burst := 1.U
      io.m.AR.bits.lock := 0.U
      io.m.AR.bits.cache := io.config_axi_cache
      io.m.AR.bits.prot := io.config_axi_prot
      io.m.AR.bits.qos := 0.U
      io.m.AR.bits.user := io.config_axi_user
      io.m.R.ready := true.B
    }
  }

  io.rd_cmd.ready := state_rd === StateRd.Idle
  io.rd_cmd.done := rd_done_reg

  //==========================================================

  /*
  val cntr_wr = Reg(UInt(10.W))
  val cntr_rd = Reg(UInt(10.W))

  val state_wr_addr_prev = RegNext(state_wr_addr)
  val state_rd_prev = RegNext(state_rd)

  when (state_rd_prev === StateRd.Idle && state_rd =/= StateRd.Idle) {
    cntr_rd := 1.U
  } .elsewhen (state_rd =/= StateRd.Idle) {
    cntr_rd := cntr_rd + 1.U
  }

  when (state_wr_addr_prev === StateWrAddr.Idle && state_wr_addr =/= StateWrAddr.Idle) {
    cntr_wr := 1.U
  } .elsewhen (state_wr_addr =/= StateWrAddr.Idle) {
    cntr_wr := cntr_wr + 1.U
  }

  io.diag_cntr_rd := cntr_rd
  io.diag_cntr_wr := cntr_wr

   */

  // TODO
  io.diag_cntr_rd := 0.U

  // tie-offs
  io.m.B.bits.resp := DontCare

  io.m.AW := DontCare
  io.m.W := DontCare

    /*.bits.user := DontCare
  io.m.W.bits.last
  io.m.W.bits.data
  io.m.W.bits.strb

     */

  io.m.B := DontCare
}
