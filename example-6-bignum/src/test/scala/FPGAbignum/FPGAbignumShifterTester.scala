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

package FPGAbignum

import bfmtester._

class FPGAbignumShifterTester(c: FPGAbignumShifter) extends BfmTester(c) {

  val m_mst: AxiStreamMaster = BfmFactory.create_axis_master(c.io.d)
  val m_slv: AxiStreamSlave = BfmFactory.create_axis_slave(c.io.q)
  m_slv.backpressure = 0.8

  //==========================================================================
  // helper functions

  def testShift(a: BigInt, shift_by: Int, input_length: Int): Unit = {
    println(f"${t}%5d ======================================================")

    for (i <- 0 until input_length) {
      m_mst.stimAppend((a >> (8 * i)) & 0xFF,
                       if (i == input_length - 1) 1 else 0)
    }

    try {
      step(TIMEOUT_STEPS)
      expect(false, "Timeout while waiting for TLAST")
      return
    } catch {
      case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Got TLAST.")
    }

    val resp = m_slv.respGet()
    val resp_len = resp.length
    val resp_num_u: BigInt =
      resp.map(_._1).foldRight(BigInt(0))((a, b) => b * 256 + a)
    val msb_mask: BigInt = BigInt(0x80) << ((resp_len - 1) * 8)

    println(
      f"${t}%5d Received response (unsign) = ${resp_num_u} (0x${resp_num_u}%x), length = ${resp_len}")

    val resp_num: BigInt = if ((resp_num_u & msb_mask) != 0) {
      val mask_xor: BigInt = (msb_mask << 1) - 1
      -((resp_num_u ^ mask_xor) + 1)
    } else {
      resp_num_u
    }

    println(
      f"${t}%5d Received response = ${resp_num} (0x${resp_num}%x), length = ${resp_len}")

    expect(resp_num == (a << shift_by),
           s"${a} << ${shift_by} = exp ${a << shift_by}, recv ${resp_num}")
  }

  //==============================================================
  val TIMEOUT_STEPS = 100

  step(5)

  testShift(1, 1, 2)
  testShift(123, 1, 2)
  testShift(0, 1, 2)
  testShift(-2, 1, 2)
  testShift(0, 1, 3)

}
