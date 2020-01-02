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

import bfmtester._
import breeze.math._
import chisel3._
import chisel3.core.FixedPoint

import scala.collection.mutable.ListBuffer

class ButterflyMonitor(val out0: ComplexBundle,
                       val out1: ComplexBundle,
                       val out_vld: Bool,
                       val peek: Bits => BigInt,
                       val poke: (Bits, BigInt) => Unit,
                       val println: String => Unit)
    extends Bfm {

  val resp: ListBuffer[(Complex, Complex)] = ListBuffer()

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val vld = peek(out_vld)
    if (vld != 0) {
      println(f"${t}%5d Monitor: received data")
      val out0_re = FixedPoint.toDouble(peek(out0.re), 16)
      val out0_im = FixedPoint.toDouble(peek(out0.im), 16)
      val out1_re = FixedPoint.toDouble(peek(out1.re), 16)
      val out1_im = FixedPoint.toDouble(peek(out1.im), 16)
      resp += Tuple2(out0_re + out0_im * i, out1_re + out1_im * i)
    }
  }
}
