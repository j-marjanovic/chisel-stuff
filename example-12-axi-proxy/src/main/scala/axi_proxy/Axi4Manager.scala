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

package axi_proxy

import bfmtester._
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class Axi4Manager(addr_w: Int, data_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, 4.W, aruser_w = 2.W, awuser_w = 2.W)

    val config_axi_cache = Input(UInt(4.W))
    val config_axi_prot = Input(UInt(3.W))
    val config_axi_user = Input(UInt(2.W))

    val wr_cmd = new Axi4ManagerWrCmd(addr_w, data_w)
    val rd_cmd = new Axi4ManagerRdCmd(addr_w, data_w)

    val diag_cntr_rd = Output(UInt(10.W))
    val diag_cntr_wr = Output(UInt(10.W))
  })

  val waddr_reg = Reg(UInt(addr_w.W))
  val wdata_reg = Reg(UInt(data_w.W))
  val raddr_reg = Reg(UInt(addr_w.W))
  val rdata_reg = Reg(UInt(data_w.W))
  val rd_done_reg = RegInit(Bool(), false.B)

  object StateWrAddr extends ChiselEnum {
    val Idle, WrAddr, WrRespWait, Done = Value
  }

  object StateWrData extends ChiselEnum {
    val Idle, WrData, WrRespWait, Done = Value
  }

  object StateRd extends ChiselEnum {
    val Idle, RdAddr, RdData = Value
  }

  val state_wr_addr = RegInit(StateWrAddr.Idle)
  val state_wr_data = RegInit(StateWrData.Idle)
  val state_rd = RegInit(StateRd.Idle)

  //==========================================================

  val wr_done = WireInit(state_wr_addr === StateWrAddr.Done && state_wr_data === StateWrData.Done)
  val wr_done_reg = RegNext(wr_done)

  switch(state_wr_addr) {
    is(StateWrAddr.Idle) {
      when(io.wr_cmd.valid) {
        waddr_reg := io.wr_cmd.addr
        state_wr_addr := StateWrAddr.WrAddr
      }
    }
    is(StateWrAddr.WrAddr) {
      when(io.m.AW.ready) {
        state_wr_addr := StateWrAddr.WrRespWait
      }
    }
    is(StateWrAddr.WrRespWait) {
      when(io.m.B.valid) {
        state_wr_addr := StateWrAddr.Done
        // wr_done_reg := true.B
      }
    }
    is(StateWrAddr.Done) {
      when(wr_done) {
        state_wr_addr := StateWrAddr.Idle
      }
    }
  }

  // just to make Chisel happy
  io.m.AW.valid := false.B
  io.m.AW.bits.id := 0.U
  io.m.AW.bits.addr := 0.U
  io.m.AW.bits.len := 3.U  // 4 beats
  io.m.AW.bits.size := 4.U  // 16 bytes
  io.m.AW.bits.burst := 1.U  // INCR
  io.m.AW.bits.lock := 0.U
  io.m.AW.bits.cache := io.config_axi_cache
  io.m.AW.bits.prot := io.config_axi_prot
  io.m.AW.bits.qos := 0.U
  io.m.AW.bits.user := io.config_axi_user
  io.m.B.ready := false.B

  switch(state_wr_addr) {
    is(StateWrAddr.Idle) {
      io.m.AW.valid := false.B
      io.m.AW.bits.id := 0.U
      io.m.AW.bits.addr := 0.U
      io.m.AW.bits.len := 3.U  // 4 beats
      io.m.AW.bits.size := 4.U  // 16 bytes
      io.m.AW.bits.burst := 1.U  // INCR
      io.m.AW.bits.lock := 0.U
      io.m.AW.bits.cache := io.config_axi_cache
      io.m.AW.bits.prot := io.config_axi_prot
      io.m.AW.bits.qos := 0.U
      io.m.AW.bits.user := io.config_axi_user
      io.m.B.ready := false.B
    }
    is(StateWrAddr.WrAddr) {
      // data based on "7.7.1 Transfer size support" from Cortex-A53 TRM
      io.m.AW.valid := true.B
      io.m.AW.bits.id := 0.U
      io.m.AW.bits.addr := waddr_reg
      io.m.AW.bits.len := 3.U  // 4 beats
      io.m.AW.bits.size := 4.U  // 16 bytes
      io.m.AW.bits.burst := 1.U  // INCR
      io.m.AW.bits.lock := 0.U
      io.m.AW.bits.cache := io.config_axi_cache
      io.m.AW.bits.prot := io.config_axi_prot
      io.m.AW.bits.qos := 0.U
      io.m.AW.bits.user := io.config_axi_user
      io.m.B.ready := false.B
    }
    is(StateWrAddr.WrRespWait) {
      io.m.AW.valid := false.B
      io.m.AW.bits.id := 0.U
      io.m.AW.bits.addr := 0.U
      io.m.AW.bits.len := 3.U  // 4 beats
      io.m.AW.bits.size := 4.U  // 16 bytes
      io.m.AW.bits.burst := 1.U  // INCR
      io.m.AW.bits.lock := 0.U
      io.m.AW.bits.cache := io.config_axi_cache
      io.m.AW.bits.prot := io.config_axi_prot
      io.m.AW.bits.qos := 0.U
      io.m.AW.bits.user := io.config_axi_user
      io.m.B.ready := true.B
    }
  }

  io.wr_cmd.ready := state_wr_addr === StateWrAddr.Idle
  io.wr_cmd.done := wr_done_reg

  //==========================================================

  val cntr_write = Reg(UInt(3.W))

  switch(state_wr_data) {
    is(StateWrData.Idle) {
      cntr_write := 0.U
      when(io.wr_cmd.valid) {
        wdata_reg := io.wr_cmd.data
        state_wr_data := StateWrData.WrData
      }
    }
    is(StateWrData.WrData) {
      when(io.m.W.ready) {
        cntr_write := cntr_write + 1.U
        when (cntr_write >= 3.U) {
          state_wr_data := StateWrData.WrRespWait
        }
      }
    }
    is(StateWrData.WrRespWait) {
      when(io.m.B.valid) {
        state_wr_data := StateWrData.Done
      }
    }
    is(StateWrData.Done) {
      when(wr_done) {
        state_wr_data := StateWrData.Idle
      }
    }
  }

  // just to make Chisel happy
  io.m.W.valid := false.B
  io.m.W.bits.data := 0.U
  io.m.W.bits.strb := 0.U
  io.m.W.bits.last := 0.U
  io.m.W.bits.user := 0.U

  switch(state_wr_data) {
    is(StateWrData.Idle) {
      io.m.W.valid := false.B
      io.m.W.bits.data := 0.U
      io.m.W.bits.strb := 0.U
      io.m.W.bits.last := 0.U
    }
    is(StateWrData.WrData) {
      io.m.W.valid := true.B
      io.m.W.bits.strb :=  -1.S(io.m.W.bits.strb.getWidth.W).asUInt()
      io.m.W.bits.data := Mux(cntr_write === 0.U, wdata_reg, 0.U)
      io.m.W.bits.last := cntr_write === 3.U

    }
  }

  //==========================================================

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

  io.rd_cmd.data := rdata_reg
  io.rd_cmd.ready := state_rd === StateRd.Idle
  io.rd_cmd.done := rd_done_reg

  //==========================================================

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
}
