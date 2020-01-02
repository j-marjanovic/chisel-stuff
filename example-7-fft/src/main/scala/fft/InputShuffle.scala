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

import chisel3._
import chisel3.util._

class InputShuffle(val n: Int) extends Module {
  val io = IO(new Bundle {
    val inp = Input(Vec(n, new ComplexBundle(18.W, 16.BP)))
    val out = Output(Vec(n, new ComplexBundle(18.W, 16.BP)))
  })

  /// enable printing here
  val debug: Boolean = true

  def dbg(s: String) = {
    if (debug) {
      println("InputShuffle: " + s)
    }
  }

  val NR_BITS = log2Ceil(n)
  dbg(s"NR_BITS = ${NR_BITS}")

  def bit_reverse(x: Int): Int = {
    var tmp: Int = 0
    for (i <- 0 until NR_BITS) {
      tmp |= ((x >> i) & 1) << (NR_BITS - 1 - i)
    }

    dbg(f"${x}%2d -> ${tmp}%2d")

    tmp
  }

  for (i <- 0 until n) {
    io.out(bit_reverse(i)) := io.inp(i)
  }
}
