/*
Copyright (c) 2020-2021 Jan Marjanovic

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

package lamports_bakery_algorithm

import chisel3._
import chisel3.util._
import bfmtester._
import chisel3.experimental.ChiselEnum

class Axi4LiteManager(val addr_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = Flipped(new AxiLiteIf(addr_w.W, 32.W))

    val wr_cmd = new Axi4LiteManagerWrCmd(addr_w, 32)
    val rd_cmd = new Axi4LiteManagerRdCmd(addr_w, 32)
  })

  val waddr_reg = Reg(UInt(addr_w.W))
  val wdata_reg = Reg(UInt(32.W))
  val raddr_reg = Reg(UInt(addr_w.W))
  val rdata_reg = Reg(UInt(32.W))
  val wr_done_reg = RegInit(Bool(), false.B)
  val rd_done_reg = RegInit(Bool(), false.B)

  object StateWr extends ChiselEnum {
    val Idle, WrAddrData, WrAddr, WrData, WrRespWait = Value
  }

  object StateRd extends ChiselEnum {
    val Idle, RdAddr, RdData = Value
  }

  val state_wr = RegInit(StateWr.Idle)
  val state_rd = RegInit(StateRd.Idle)

  //==========================================================

  wr_done_reg := false.B

  switch(state_wr) {
    is(StateWr.Idle) {
      when(io.wr_cmd.valid) {
        waddr_reg := io.wr_cmd.addr
        wdata_reg := io.wr_cmd.data
        state_wr := StateWr.WrAddrData
      }
    }
    is(StateWr.WrAddrData) {
      when(io.m.AW.ready && io.m.W.ready) {
        state_wr := StateWr.WrRespWait
      }.elsewhen(io.m.AW.ready) {
          state_wr := StateWr.WrData
        }
        .elsewhen(io.m.W.ready) {
          state_wr := StateWr.WrAddr
        }
    }
    is(StateWr.WrData) {
      when(io.m.W.ready) {
        state_wr := StateWr.WrRespWait
      }
    }
    is(StateWr.WrAddr) {
      when(io.m.AW.ready) {
        state_wr := StateWr.WrRespWait
      }
    }
    is(StateWr.WrRespWait) {
      when(io.m.B.valid) {
        state_wr := StateWr.Idle
        wr_done_reg := true.B
      }
    }
  }

  // just to make Chisel happy
  io.m.AW.valid := false.B
  io.m.AW.bits.addr := 0.U
  io.m.AW.bits.prot := 0.U
  io.m.W.valid := false.B
  io.m.W.bits.wdata := 0.U
  io.m.W.bits.wstrb := 0.U
  io.m.B.ready := false.B

  switch(state_wr) {
    is(StateWr.Idle) {
      io.m.AW.valid := false.B
      io.m.AW.bits.addr := 0.U
      io.m.AW.bits.prot := 0.U
      io.m.W.valid := false.B
      io.m.W.bits.wdata := 0.U
      io.m.W.bits.wstrb := 0.U
      io.m.B.ready := false.B
    }
    is(StateWr.WrAddrData) {
      io.m.AW.valid := true.B
      io.m.AW.bits.addr := waddr_reg
      io.m.AW.bits.prot := 0.U
      io.m.W.valid := true.B
      io.m.W.bits.wdata := wdata_reg
      io.m.W.bits.wstrb := -1.S(io.m.W.bits.wstrb.getWidth.W).asUInt()
      io.m.B.ready := false.B
    }
    is(StateWr.WrData) {
      io.m.AW.valid := false.B
      io.m.AW.bits.addr := 0.U
      io.m.AW.bits.prot := 0.U
      io.m.W.valid := true.B
      io.m.W.bits.wdata := wdata_reg
      io.m.W.bits.wstrb := -1.S(io.m.W.bits.wstrb.getWidth.W).asUInt()
      io.m.B.ready := false.B
    }
    is(StateWr.WrAddr) {
      io.m.AW.valid := true.B
      io.m.AW.bits.addr := waddr_reg
      io.m.AW.bits.prot := 0.U
      io.m.W.valid := false.B
      io.m.W.bits.wdata := 0.U
      io.m.W.bits.wstrb := 0.U
      io.m.B.ready := false.B
    }
    is(StateWr.WrRespWait) {
      io.m.AW.valid := false.B
      io.m.AW.bits.addr := 0.U
      io.m.AW.bits.prot := 0.U
      io.m.W.valid := false.B
      io.m.W.bits.wdata := 0.U
      io.m.W.bits.wstrb := 0.U
      io.m.B.ready := true.B
    }
  }

  io.wr_cmd.ready := state_wr === StateWr.Idle
  io.wr_cmd.done := wr_done_reg

  //==========================================================

  rd_done_reg := false.B

  switch(state_rd) {
    is(StateRd.Idle) {
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
        rdata_reg := io.m.R.bits.rdata
        rd_done_reg := true.B
        state_rd := StateRd.Idle
      }
    }
  }

  // just to make Chisel happy
  io.m.AR.valid := false.B
  io.m.R.ready := false.B
  io.m.AR.bits.addr := 0.U
  io.m.AR.bits.prot := 0.U

  switch(state_rd) {
    is(StateRd.Idle) {
      io.m.AR.valid := false.B
      io.m.AR.bits.addr := 0.U
      io.m.AR.bits.prot := 0.U
      io.m.R.ready := false.B
    }
    is(StateRd.RdAddr) {
      io.m.AR.valid := true.B
      io.m.AR.bits.addr := raddr_reg
      io.m.AR.bits.prot := 0.U
      io.m.R.ready := false.B
    }
    is(StateRd.RdData) {
      io.m.AR.valid := false.B
      io.m.AR.bits.addr := 0.U
      io.m.AR.bits.prot := 0.U
      io.m.R.ready := true.B
    }
  }

  io.rd_cmd.data := rdata_reg
  io.rd_cmd.ready := state_rd === StateRd.Idle
  io.rd_cmd.done := rd_done_reg

}
