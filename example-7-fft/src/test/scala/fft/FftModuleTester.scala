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
import bfmtester._
import breeze.math._
import breeze.numerics.pow
import chisel3.core.FixedPoint

import scala.collection.mutable.ListBuffer

class FftModuleTester(c: FftModule) extends BfmTester(c) {

  val m_mst: AxiStreamMaster = BfmFactory.create_axis_master(c.io.data_in)
  val m_slv: AxiStreamSlave = BfmFactory.create_axis_slave(c.io.data_out)
  m_slv.backpressure = 0

  //==============================================================

  val STIM: List[Double] = List(
    0.1, 0.2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
  )

  for (i <- 0 until 15) {
    m_mst.stimAppend(FixedPoint.toBigInt(STIM(i), 16), 0, 0)
  }

  try {
    step(100)
    expect(false, "timeout waiting for last")
  } catch {
    case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Got TLAST.")
  }

  def bigint_to_complex(x: BigInt): Complex = {
    val MASK: BigInt = (1L << 18) - 1
    val INT_TO_DBL_COEF: Double = 1.0 / pow(2, 16)

    val re_uint: Int = (x & MASK).toInt
    val im_uint: Int = ((x & (MASK << 32L)) >> 32L).toInt

    def uint18_to_int(x: Int): Int =
      if ((x & (1 << 17)) == 0) { x } else { x - (1 << 18) }

    val re_int = uint18_to_int(re_uint)
    val im_int = uint18_to_int(im_uint)

    re_int * INT_TO_DBL_COEF + i * im_int * INT_TO_DBL_COEF
  }

  val resp = ListBuffer[Complex]()

  m_slv.respGet().foreach { data_user =>
    {
      val data = data_user._1
      println(s"data = ${round(bigint_to_complex(data), 4)}")
    }
  }

}
