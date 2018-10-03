/*
MIT License

Copyright (c) 2018 Jan Marjanovic

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

package axiLiteExample

import chisel3._
import chisel3.util._


class PipelineWithAxiLiteSlave extends Module {
  val ADDR_W = 4

  val io = IO(new Bundle {
    val ctrl          = new AxiLite(ADDR_W = ADDR_W)
    val coef          = Output(UInt(16.W))
    val stats_nr_samp = Input(UInt(32.W))
  })

  // word (32-bit) address
  val ADDR_ID      = 0.U
  val ADDR_VERSION = 1.U
  val ADDR_STATS   = 2.U
  val ADDR_COEF    = 3.U

  val REG_ID      = 0x71711123.U(32.W) // ~ PIPELINE
  val REG_VERSION = ((1 << 16) | (0 << 8) | (0)).U(32.W)
  val REG_COEF    = RegInit(UInt(16.W), 0.U)

  io.coef := REG_COEF

  //==========================================================================
  // write part

  val sWrIdle :: sWrHasRecvAddr :: sWrHasRecvData :: sWrResp :: Nil = Enum(4)
  val state_wr = RegInit(sWrIdle)

  val wr_en   = Reg(Bool())
  val wr_addr = Reg(UInt())
  val wr_data = Reg(UInt())
  val wr_strb = Reg(UInt())

  // default value (gets overridden by FSM when both data and addr are valid)
  // this is just to make the compiler happy
  wr_en := false.B

  switch (state_wr) {
    is (sWrIdle) {
      when (io.ctrl.AW.valid && io.ctrl.W.valid) {
        wr_en    := true.B
        wr_addr  := io.ctrl.AW.bits(ADDR_W-1, 2)
        wr_data  := io.ctrl.W.bits.wdata
        wr_strb  := io.ctrl.W.bits.wstrb
        state_wr := sWrResp
      } .elsewhen (io.ctrl.AW.valid) {
        wr_addr  := io.ctrl.AW.bits(ADDR_W-1, 2)
        state_wr := sWrHasRecvAddr
      } .elsewhen (io.ctrl.W.valid) {
        wr_data  := io.ctrl.W.bits.wdata
        wr_strb  := io.ctrl.W.bits.wstrb
        state_wr := sWrHasRecvData
      }
    }
    is (sWrHasRecvAddr) {
      when (io.ctrl.W.valid) {
        wr_en    := true.B
        wr_data  := io.ctrl.W.bits.wdata
        wr_strb  := io.ctrl.W.bits.wstrb
        state_wr := sWrResp
      }
    }
    is (sWrHasRecvData) {
      when (io.ctrl.AW.valid) {
        wr_en    := true.B
        wr_addr  := io.ctrl.AW.bits(ADDR_W-1, 2)
        state_wr := sWrResp
      }
    }
    is (sWrResp) {
      when (io.ctrl.B.ready) {
        state_wr := sWrIdle
      }
    }
  }

  // default values (gets overridden by FSM when both addr is valid)
  // this is just to make the compiler happy
  io.ctrl.AW.ready := false.B
  io.ctrl.W.ready  := false.B
  io.ctrl.B.valid  := false.B

  switch (state_wr) {
    is (sWrIdle) {
      io.ctrl.AW.ready := true.B
      io.ctrl.W.ready  := true.B
      io.ctrl.B.valid  := false.B
    }
    is (sWrHasRecvData) {
      io.ctrl.AW.ready := true.B
      io.ctrl.W.ready  := false.B
      io.ctrl.B.valid  := false.B
    }
    is (sWrHasRecvAddr) {
      io.ctrl.AW.ready := false.B
      io.ctrl.W.ready  := true.B
      io.ctrl.B.valid  := false.B
    }
    is (sWrResp) {
      io.ctrl.AW.ready := false.B
      io.ctrl.W.ready  := false.B
      io.ctrl.B.valid  := true.B
    }
  }

  // as in the Xilinx example, we always return OKAY as a response
  io.ctrl.B.bits := 0x0.U(2.W)
  io.ctrl.R.bits.rresp := 0x0.U(2.W)

  // write to regs
  def wrWithStrobe(data: UInt, prev: UInt, strobe: UInt) : UInt = {
    val BIT_W = 8
    val tmp = Wire(Vec(prev.getWidth/BIT_W, UInt(BIT_W.W)))

    for (i <- 0 until prev.getWidth/BIT_W) {
      when ((strobe & (1 << i).U) =/= 0.U) {
        tmp(i) := data((i+1)*BIT_W-1, i*BIT_W)
      } .otherwise {
        tmp(i) := prev((i+1)*BIT_W-1, i*BIT_W)
      }
    }

    tmp.asUInt()
  }

  when (wr_en) {
    switch (wr_addr) {
      is (ADDR_COEF) { REG_COEF := wrWithStrobe(wr_data, REG_COEF, wr_strb) }
    }
  }

  //==========================================================================
  // read part

  val sRdIdle :: sRdRead :: sRdResp :: Nil = Enum(3)
  val state_rd = RegInit(sRdIdle)

  val rd_en   = Reg(Bool())
  val rd_addr = Reg(UInt())
  val rd_data = Reg(UInt(io.ctrl.R.bits.getWidth.W))

  // default value (gets overridden by FSM when both data and addr are valid)
  rd_en := false.B

  switch (state_rd) {
    is (sRdIdle) {
      when (io.ctrl.AR.valid) {
        rd_en    := true.B
        rd_addr  := io.ctrl.AR.bits(io.ctrl.AR.bits.getWidth-1, 2)
        state_rd := sRdRead
      }
    }
    is (sRdRead) {
      state_rd   := sRdResp
    }
    is (sRdResp) {
      when (io.ctrl.R.ready) {
        state_rd := sWrIdle
      }
    }
  }

  io.ctrl.AR.ready     := false.B
  io.ctrl.R.valid      := false.B
  io.ctrl.R.bits.rdata := 0.U

  switch (state_rd) {
    is (sRdIdle) {
      io.ctrl.AR.ready     := true.B
      io.ctrl.R.valid      := false.B
      io.ctrl.R.bits.rdata := 0.U
    }
    is (sRdRead) {
      io.ctrl.AR.ready     := false.B
      io.ctrl.R.valid      := false.B
      io.ctrl.R.bits.rdata := 0.U
    }
    is (sRdResp) {
      io.ctrl.AR.ready     := false.B
      io.ctrl.R.valid      := true.B
      io.ctrl.R.bits.rdata := rd_data
    }
  }

  // read from regs
  when (rd_en) {
    switch (rd_addr) {
      is (ADDR_ID)      { rd_data := REG_ID           }
      is (ADDR_VERSION) { rd_data := REG_VERSION      }
      is (ADDR_STATS)   { rd_data := io.stats_nr_samp }
      is (ADDR_COEF)    { rd_data := REG_COEF         }
    }
  }

}
