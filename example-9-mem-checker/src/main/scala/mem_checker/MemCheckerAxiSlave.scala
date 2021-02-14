/*
Copyright (c) 2019-2021 Jan Marjanovic

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

package mem_checker

import chisel3._
import chisel3.util._
import bfmtester._

class MemCheckerAxiSlave(val version: Int, val mem_data_w: Int, val addr_w: Int = 8) extends Module {

  val io = IO(new Bundle {
    val ctrl = new AxiLiteIf(addr_w = addr_w.W)

    val ctrl_dir = Output(Bool()) // 0 = read, 1 = write
    val ctrl_mode = Output(UInt(3.W))

    val read_addr = Output(UInt(64.W))
    val read_len = Output(UInt(32.W))
    val read_start = Output(Bool())
    val rd_stats_resp_cntr = Input(UInt(32.W))
    val rd_stats_done = Input(Bool())
    val rd_stats_duration = Input(UInt(32.W))

    val write_addr = Output(UInt(64.W))
    val write_len = Output(UInt(32.W))
    val write_start = Output(Bool())
    val wr_stats_resp_cntr = Input(UInt(32.W))
    val wr_stats_done = Input(Bool())
    val wr_stats_duration = Input(UInt(32.W))

    val check_tot = Input(UInt(32.W))
    val check_ok = Input(UInt(32.W))
  })

  //==========================================================================
  // word (32-bit) address
  val ADDR_ID = 0.U
  val ADDR_VERSION = 1.U
  val ADDR_CONF = 2.U
  val ADDR_CTRL = 4.U
  val ADDR_READ_STATUS = 8.U
  val ADDR_READ_CTRL = 9.U
  val ADDR_READ_ADDR_LO = 10.U
  val ADDR_READ_ADDR_HI = 11.U
  val ADDR_READ_LEN = 12.U
  val ADDR_READ_RESP_CNTR = 13.U
  val ADDR_READ_DURATION = 14.U
  val ADDR_WRITE_STATUS = 24.U
  val ADDR_WRITE_CTRL = 25.U
  val ADDR_WRITE_ADDR_LO = 26.U
  val ADDR_WRITE_ADDR_HI = 27.U
  val ADDR_WRITE_LEN = 28.U
  val ADDR_WRITE_RESP_CNTR = 29.U
  val ADDR_WRITE_DURATION = 30.U
  val ADDR_CHECK_TOT = 40.U
  val ADDR_CHECK_OK = 41.U

  val REG_ID = 0x3e3c8ec8.U(32.W) // ~ MEM CHECK
  val REG_VERSION = version.U(32.W)
  val reg_ctrl_dir = RegInit(Bool(), false.B)
  val reg_ctrl_mode = RegInit(UInt(3.W), 0.U)
  val reg_read_status = RegInit(UInt(32.W), 0.U)
  val reg_read_status_clear = Wire(UInt(32.W))
  val reg_read_start = RegInit(Bool(), false.B)
  val reg_read_addr = RegInit(UInt(64.W), 0.U)
  val reg_read_len = RegInit(UInt(32.W), 0.U)
  val reg_read_resp_cntr = RegNext(io.rd_stats_resp_cntr)
  val reg_read_duration = RegNext(io.rd_stats_duration)
  val reg_write_status = RegInit(UInt(32.W), 0.U)
  val reg_write_status_clear = Wire(UInt(32.W))
  val reg_write_start = RegInit(Bool(), false.B)
  val reg_write_addr = RegInit(UInt(64.W), 0.U)
  val reg_write_len = RegInit(UInt(32.W), 0.U)
  val reg_write_resp_cntr = RegNext(io.wr_stats_resp_cntr)
  val reg_write_duration = RegNext(io.wr_stats_duration)
  val reg_check_tot = RegNext(io.check_tot)
  val reg_check_ok = RegNext(io.check_ok)

  io.ctrl_dir := reg_ctrl_dir
  io.ctrl_mode := reg_ctrl_mode
  io.read_addr := reg_read_addr
  io.read_len := reg_read_len
  io.read_start := reg_read_start
  io.write_addr := reg_write_addr
  io.write_len := reg_write_len
  io.write_start := reg_write_start

  reg_read_status := (reg_read_status & (~reg_read_status_clear).asUInt()) | Cat(
    0.U(31.W),
    io.rd_stats_done
  )
  reg_write_status := (reg_write_status & (~reg_write_status_clear).asUInt()) | Cat(
    0.U(31.W),
    io.wr_stats_done
  )

  //==========================================================================
  // write part

  val sWrIdle :: sWrHasRecvAddr :: sWrHasRecvData :: sWrResp :: Nil = Enum(4)
  val state_wr = RegInit(sWrIdle)

  val wr_en = Reg(Bool())
  val wr_addr = Reg(UInt())
  val wr_data = Reg(UInt())
  val wr_strb = Reg(UInt())

  // default value (gets overridden by FSM when both data and addr are valid)
  // this is just to make the compiler happy
  wr_en := false.B

  switch(state_wr) {
    is(sWrIdle) {
      when(io.ctrl.AW.valid && io.ctrl.W.valid) {
        wr_en := true.B
        wr_addr := io.ctrl.AW.bits.addr(addr_w - 1, 2)
        wr_data := io.ctrl.W.bits.wdata
        wr_strb := io.ctrl.W.bits.wstrb
        state_wr := sWrResp
      }.elsewhen(io.ctrl.AW.valid) {
          wr_addr := io.ctrl.AW.bits.addr(addr_w - 1, 2)
          state_wr := sWrHasRecvAddr
        }
        .elsewhen(io.ctrl.W.valid) {
          wr_data := io.ctrl.W.bits.wdata
          wr_strb := io.ctrl.W.bits.wstrb
          state_wr := sWrHasRecvData
        }
    }
    is(sWrHasRecvAddr) {
      when(io.ctrl.W.valid) {
        wr_en := true.B
        wr_data := io.ctrl.W.bits.wdata
        wr_strb := io.ctrl.W.bits.wstrb
        state_wr := sWrResp
      }
    }
    is(sWrHasRecvData) {
      when(io.ctrl.AW.valid) {
        wr_en := true.B
        wr_addr := io.ctrl.AW.bits.addr(addr_w - 1, 2)
        state_wr := sWrResp
      }
    }
    is(sWrResp) {
      when(io.ctrl.B.ready) {
        state_wr := sWrIdle
      }
    }
  }

  // default values (gets overridden by FSM when both addr is valid)
  // this is just to make the compiler happy
  io.ctrl.AW.ready := false.B
  io.ctrl.W.ready := false.B
  io.ctrl.B.valid := false.B

  switch(state_wr) {
    is(sWrIdle) {
      io.ctrl.AW.ready := true.B
      io.ctrl.W.ready := true.B
      io.ctrl.B.valid := false.B
    }
    is(sWrHasRecvData) {
      io.ctrl.AW.ready := true.B
      io.ctrl.W.ready := false.B
      io.ctrl.B.valid := false.B
    }
    is(sWrHasRecvAddr) {
      io.ctrl.AW.ready := false.B
      io.ctrl.W.ready := true.B
      io.ctrl.B.valid := false.B
    }
    is(sWrResp) {
      io.ctrl.AW.ready := false.B
      io.ctrl.W.ready := false.B
      io.ctrl.B.valid := true.B
    }
  }

  // as in the Xilinx example, we always return OKAY as a response
  io.ctrl.B.bits := 0x0.U(2.W)
  io.ctrl.R.bits.rresp := 0x0.U(2.W)

  // write to regs
  def wrWithStrobe(data: UInt, prev: UInt, strobe: UInt): UInt = {
    val BIT_W = 8
    val tmp = Wire(Vec(prev.getWidth / BIT_W, UInt(BIT_W.W)))

    for (i <- 0 until prev.getWidth / BIT_W) {
      when((strobe & (1 << i).U) =/= 0.U) {
        tmp(i) := data((i + 1) * BIT_W - 1, i * BIT_W)
      }.otherwise {
        tmp(i) := prev((i + 1) * BIT_W - 1, i * BIT_W)
      }
    }

    tmp.asUInt()
  }

  reg_read_start := false.B
  reg_write_start := false.B
  reg_read_status_clear := 0.U
  reg_write_status_clear := 0.U

  when(wr_en) {
    switch(wr_addr) {
      is(ADDR_CTRL) {
        reg_ctrl_dir := wr_data(0)
        reg_ctrl_mode := wr_data(10, 8)
      }
      is(ADDR_READ_STATUS) { reg_read_status_clear := wr_data }
      is(ADDR_READ_CTRL) { reg_read_start := wr_data(0) }
      is(ADDR_READ_ADDR_LO) { reg_read_addr := Cat(reg_read_addr(63, 32), wr_data) }
      is(ADDR_READ_ADDR_HI) { reg_read_addr := Cat(wr_data, reg_read_addr(31, 0)) }
      is(ADDR_READ_LEN) { reg_read_len := wr_data }
      is(ADDR_WRITE_STATUS) { reg_write_status_clear := wr_data }
      is(ADDR_WRITE_CTRL) { reg_write_start := wr_data(0) }
      is(ADDR_WRITE_ADDR_LO) { reg_write_addr := Cat(reg_write_addr(63, 32), wr_data) }
      is(ADDR_WRITE_ADDR_HI) { reg_write_addr := Cat(wr_data, reg_write_addr(31, 0)) }
      is(ADDR_WRITE_LEN) { reg_write_len := wr_data }
    }
  }

  //==========================================================================
  // read part

  val sRdIdle :: sRdRead :: sRdResp :: Nil = Enum(3)
  val state_rd = RegInit(sRdIdle)

  val rd_en = Reg(Bool())
  val rd_addr = Reg(UInt())
  val rd_data = Reg(UInt(io.ctrl.R.bits.getWidth.W))

  // default value (gets overridden by FSM when both data and addr are valid)
  rd_en := false.B

  switch(state_rd) {
    is(sRdIdle) {
      when(io.ctrl.AR.valid) {
        rd_en := true.B
        rd_addr := io.ctrl.AR.bits.addr(addr_w - 1, 2)
        state_rd := sRdRead
      }
    }
    is(sRdRead) {
      state_rd := sRdResp
    }
    is(sRdResp) {
      when(io.ctrl.R.ready) {
        state_rd := sWrIdle
      }
    }
  }

  io.ctrl.AR.ready := false.B
  io.ctrl.R.valid := false.B
  io.ctrl.R.bits.rdata := 0.U

  switch(state_rd) {
    is(sRdIdle) {
      io.ctrl.AR.ready := true.B
      io.ctrl.R.valid := false.B
      io.ctrl.R.bits.rdata := 0.U
    }
    is(sRdRead) {
      io.ctrl.AR.ready := false.B
      io.ctrl.R.valid := false.B
      io.ctrl.R.bits.rdata := 0.U
    }
    is(sRdResp) {
      io.ctrl.AR.ready := false.B
      io.ctrl.R.valid := true.B
      io.ctrl.R.bits.rdata := rd_data
    }
  }

  // read from regs
  when(rd_en) {
    switch(rd_addr) {
      is(ADDR_ID) { rd_data := REG_ID }
      is(ADDR_VERSION) { rd_data := REG_VERSION }
      is(ADDR_CONF) { rd_data := (mem_data_w/8).U }
      is(ADDR_CTRL) { rd_data := Cat(reg_ctrl_mode, 0.U(7.W), reg_ctrl_dir) }
      is(ADDR_READ_STATUS) { rd_data := reg_read_status }
      is(ADDR_READ_CTRL) { rd_data := 0.U }
      is(ADDR_READ_ADDR_LO) { rd_data := reg_read_addr(31, 0) }
      is(ADDR_READ_ADDR_HI) { rd_data := reg_read_addr(63, 32) }
      is(ADDR_READ_LEN) { rd_data := reg_read_len }
      is(ADDR_READ_RESP_CNTR) { rd_data := reg_read_resp_cntr }
      is(ADDR_READ_DURATION) { rd_data := reg_read_duration }
      is(ADDR_WRITE_STATUS) { rd_data := reg_write_status }
      is(ADDR_WRITE_CTRL) { rd_data := 0.U }
      is(ADDR_WRITE_ADDR_LO) { rd_data := reg_write_addr(31, 0) }
      is(ADDR_WRITE_ADDR_HI) { rd_data := reg_write_addr(63, 32) }
      is(ADDR_WRITE_LEN) { rd_data := reg_write_len }
      is(ADDR_WRITE_RESP_CNTR) { rd_data := reg_write_resp_cntr }
      is(ADDR_WRITE_DURATION) { rd_data := reg_write_duration }
      is(ADDR_CHECK_TOT) { rd_data := reg_check_tot }
      is(ADDR_CHECK_OK) { rd_data := reg_check_ok }
    }
  }
}
