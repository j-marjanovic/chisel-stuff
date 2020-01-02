/*
MIT License

Copyright (c) 2019 Jan Marjanovic

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

package fft

import breeze.math._
import breeze.numerics._
import breeze.numerics.constants._
import chisel3._
import chisel3.internal.firrtl.{BinaryPoint, Width}
import chisel3.util.ShiftRegister

class FftButterfly(val w: Width, val bp: BinaryPoint, val k: Int, val N: Int)
    extends chisel3.Module {
  val io = IO(new Bundle {
    val in0 = Input(new ComplexBundle(w, bp))
    val in1 = Input(new ComplexBundle(w, bp))
    val in_vld = Input(Bool())
    val out0 = Output(new ComplexBundle(w, bp))
    val out1 = Output(new ComplexBundle(w, bp))
    val out_vld = Output(Bool())
  })

  val W: Complex = exp(-1 * i * 2 * Pi * k / N)

  val C_W = Wire(new ComplexBundle(w, bp))
  C_W.re := W.real.F(bp)
  C_W.im := W.imag.F(bp)

  val in1_W = RegNext(io.in1 * C_W)
  val in0 = ShiftRegister(io.in0, 4)

  val out0 = RegNext(in0 + in1_W)
  val out1 = RegNext(in0 - in1_W)

  io.out0 := out0
  io.out1 := out1

  val out_vld = ShiftRegister(io.in_vld, 6)
  io.out_vld := out_vld
}
