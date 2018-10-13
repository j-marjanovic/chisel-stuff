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

class PipelineWithAxiLite extends Module {
  val io = IO(new Bundle {
    val ctrl = new AxiLite(ADDR_W = 4)
    val in_data   = Input(UInt(16.W))
    val in_valid  = Input(Bool())
    val out_data  = Output(UInt(16.W))
    val out_valid = Output(Bool())
  })

  class DataAndValid extends Bundle {
    val data = UInt(16.W)
    val valid = Bool()

    def +&(x: UInt): DataAndValid = {
      val tmp = Wire(new DataAndValid)
      tmp.valid := this.valid
      tmp.data  := this.data +& x
      tmp
    }
  }

  // axi interface
  val axi_ctrl   = Module(new PipelineWithAxiLiteSlave)
  val coef       = Wire(UInt(16.W))
  val nr_samples = RegInit(UInt(32.W), 0.U)
  io.ctrl                   <> axi_ctrl.io.ctrl
  coef                      := axi_ctrl.io.coef
  axi_ctrl.io.stats_nr_samp := nr_samples

  when (io.out_valid) {
    nr_samples := nr_samples +& 1.U
  }

  val in = Wire(new DataAndValid())
  in.data  := io.in_data
  in.valid := io.in_valid

  val p0 = RegNext(in)
  val p1 = RegNext(p0 +& coef)

  io.out_data := p1.data
  io.out_valid := p1.valid

}
