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
import breeze.numerics._
import breeze.numerics.constants._

class FftButterflyTester(c: FftButterfly, val k: Int, val N: Int) extends BfmTester(c) {

  // =========================================================================
  //  helper funcs

  def butterfly(in: (Complex, Complex), k: Int, N: Int): (Complex, Complex) = {
    val W: Complex = exp(-1 * i * 2 * Pi * k / N)

    val in1_W: Complex = in._2 * W
    val out0: Complex = in._1 + in1_W
    val out1: Complex = in._1 - in1_W

    Tuple2(out0, out1)
  }

  // =========================================================================
  //  stim

  val STIM: List[(Complex, Complex)] = List(
    Tuple2(0.01 + 0.02 * i, 0.03 + 0.04 * i),
    Tuple2(0.0 + 0.0 * i, 0.0 + 0.0 * i),
    Tuple2(0.0 - 0.1 * i, 0.0 - 0.1 * i),
    Tuple2(0.5 + 0.5 * i, 0.5 + 0.5 * i),
    Tuple2(-0.1 + 0.0 * i, -0.5 - 0.2 * i),
    Tuple2(0.001 + 0.001 * i, 0.001 - 0.001 * i),
    Tuple2(0.001 - 0.002 * i, 0.003 - 0.004 * i),
    Tuple2(-0.01 + 0.01 * i, -0.02 + 0.04 * i),
  )

  val EXPECT: List[(Complex, Complex)] = STIM.map(butterfly(_, k, N))

  // =========================================================================
  //  modules
  val drv = new ButterflyDriver(c.io.in0, c.io.in1, c.io.in_vld, peek, poke, println)

  val mon = new ButterflyMonitor(c.io.out0, c.io.out1, c.io.out_vld, peek, poke, println)

  // =========================================================================
  //  main

  println("Butterfly test starting ...")
  println(s"  k = ${k}, N = ${N}")

  drv.stim ++= STIM
  step(20)

  val resp: List[(Complex, Complex)] = mon.resp.toList

  for (r_e <- resp.zip(EXPECT)) {
    println("")
    println(f"recv   (${round(r_e._1._1, 5)}, ${round(r_e._1._2, 5)})")
    println(f"expect (${round(r_e._2._1, 5)}, ${round(r_e._2._2, 5)})")

    val diff0 = r_e._1._1 - r_e._2._1
    val diff1 = r_e._1._2 - r_e._2._2
    println(f"diff = ${diff0}, ${diff1}")

    val tot_diff = abs(diff0 * diff0 + diff1 * diff1)
    println(f"tot diff = ${tot_diff}")

    expect(tot_diff < 1e-6, "error needs to be below 1 ppm")
  }
}
