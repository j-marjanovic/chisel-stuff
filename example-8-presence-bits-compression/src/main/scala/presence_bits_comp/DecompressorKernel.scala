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

package presence_bits_comp

import chisel3._
import chisel3.util._

class DecompressorKernel(val w: Int) extends Module {
  val io = IO(new Bundle {
    val in = new DecompressorKernelInputInterface(w)
    val out = new DecompressorKernelOutputInterface(w)
  })

  //==========================================================================
  // presence bits
  val pres_bs: UInt = Wire(UInt(w.W))
  pres_bs := io.in.data(0)

  //==========================================================================
  // advance input
  val adv_en_reg: Bool = RegNext(next = io.in.en && io.out.ready)
  io.in.adv_en := adv_en_reg
  io.out.vld := adv_en_reg

  io.in.adv := PopCount(pres_bs) + 1.U

  //==========================================================================
  // output selector
  val out_sel: Vec[UInt] = Wire(Vec(w, UInt(log2Ceil(w + 1).W)))

  for (i <- 0 until w) {
    out_sel(i) := PopCount(pres_bs(i, 0))
  }

  //==========================================================================
  // output
  val out_reg: Vec[UInt] = Reg(Vec(w, UInt(w.W)))

  when(io.in.en && io.out.ready) {
    for (i <- 0 until w) {
      when(pres_bs(i)) {
        out_reg(i) := io.in.data(out_sel(i))
      }.otherwise {
        out_reg(i) := 0.U
      }
    }
  }

  io.out.data := out_reg
}
