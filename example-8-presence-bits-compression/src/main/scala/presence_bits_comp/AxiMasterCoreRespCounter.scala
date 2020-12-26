/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import chisel3._
import bfmtester._

class AxiMasterCoreRespCounter extends Module {
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val resp = Input(UInt(2.W))

    val cntr_resp_okay = Output(UInt(32.W))
    val cntr_resp_exokay = Output(UInt(32.W))
    val cntr_resp_slverr = Output(UInt(32.W))
    val cntr_resp_decerr = Output(UInt(32.W))
  })

  val cntr_resp_okay = RegInit(UInt(32.W), 0.U)
  val cntr_resp_exokay = RegInit(UInt(32.W), 0.U)
  val cntr_resp_slverr = RegInit(UInt(32.W), 0.U)
  val cntr_resp_decerr = RegInit(UInt(32.W), 0.U)

  when(io.enable) {
    when(io.resp === AxiIfRespToUInt(AxiIfResp.OKAY)) {
      cntr_resp_okay := cntr_resp_okay + 1.U
    }
    when(io.resp === AxiIfRespToUInt(AxiIfResp.EXOKAY)) {
      cntr_resp_exokay := cntr_resp_exokay + 1.U
    }
    when(io.resp === AxiIfRespToUInt(AxiIfResp.SLVERR)) {
      cntr_resp_slverr := cntr_resp_slverr + 1.U
    }
    when(io.resp === AxiIfRespToUInt(AxiIfResp.DECERR)) {
      cntr_resp_decerr := cntr_resp_decerr + 1.U
    }
  }

  io.cntr_resp_okay := cntr_resp_okay
  io.cntr_resp_exokay := cntr_resp_exokay
  io.cntr_resp_slverr := cntr_resp_slverr
  io.cntr_resp_decerr := cntr_resp_decerr

}
