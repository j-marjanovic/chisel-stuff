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
import breeze.math.Complex
import chisel3._
import chisel3.core.FixedPoint

import scala.collection.mutable.ListBuffer

class ButterflyDriver(val in0: ComplexBundle,
                      val in1: ComplexBundle,
                      val in_vld: Bool,
                      val peek: Bits => BigInt,
                      val poke: (Bits, BigInt) => Unit,
                      val println: String => Unit)
    extends Bfm {

  val stim: ListBuffer[(Complex, Complex)] = ListBuffer()

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    if (stim.nonEmpty) {
      println(f"${t}%5d Driver: send data")
      val el = stim.remove(0)

      poke(in0.re, FixedPoint.toBigInt(el._1.real, 16))
      poke(in0.im, FixedPoint.toBigInt(el._1.imag, 16))
      poke(in1.re, FixedPoint.toBigInt(el._2.real, 16))
      poke(in1.im, FixedPoint.toBigInt(el._2.imag, 16))
      poke(in_vld, 1)
    } else {
      poke(in0.re, 0)
      poke(in0.im, 0)
      poke(in1.re, 0)
      poke(in1.im, 0)
      poke(in_vld, 0)
    }
  }
}
