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

import bfmtester._

class DecompressorOutputAdapterTest(c: DecompressorOutputAdapter) extends BfmTester(c) {
  private def printWithBg_drive(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  private def printWithBg_monitor(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  val kernel_w: Int = c.w
  val axi_w: Int = c.data_w

  var recv_cntr: Int = 0
  var trans_cntr: Int = 0

  for (j <- 0 until 20) {
    // recv
    val vld: Boolean = peek(c.io.to_axi.valid) > 0
    val rdy_prev = peek(c.io.to_axi.ready) > 0
    val rdy = this.rnd.nextBoolean()
    poke(c.io.to_axi.ready, rdy)
    if (vld && rdy_prev) {
      val data = peek(c.io.to_axi.data)
      printWithBg_monitor(f"recv, data = ${data}%x")
      for (i <- 0 until axi_w / 8) {
        val recv_lo = (data >> (8 * i)) & 0xf
        val exp_lo = (i % kernel_w) + 1
        expect(recv_lo == exp_lo, "lower nibble should match")

        val recv_hi = (data >> (8 * i + 4)) & 0xf
        val exp_hi = recv_cntr + i / 8
        expect(recv_hi == exp_hi, "higher nibble should match")
      }
      recv_cntr += 2
    }

    // drive
    if (rdy_prev) {
      for (i <- 0 until kernel_w) {
        val data = i + 1 + (trans_cntr << 4)
        poke(c.io.from_kernel.data(i), data)
        printWithBg_drive(f"drive, idx = ${i}, data = ${data}%x")
        poke(c.io.from_kernel.vld, 1)
      }
      trans_cntr += 1
    }

    step(1)
  }
}
