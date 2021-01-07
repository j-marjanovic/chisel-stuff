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

import bfmtester._
import chisel3._

import scala.collection.mutable.ListBuffer
import scala.util.Random

class DecompressorOutputAdapterMonitor(
    val iface: AxiMasterCoreWriteIface,
    val rnd: Random,
    val axi_w: Integer,
    val kernel_w: Integer,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private var recv_cntr: Int = 0
  private val data_buf = ListBuffer[Byte]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  def get_data(): List[Byte] = {
    val tmp = data_buf.toList
    data_buf.clear()
    tmp
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val vld: Boolean = peek(iface.valid) > 0
    val rdy = if (this.rnd.nextInt(100) > 20) 1 else 0
    val rdy_prev = peek(iface.ready) > 0
    poke(iface.ready, rdy)
    if (vld && rdy_prev) {
      val data = peek(iface.data)
      printWithBg(f"recv, data = ${data}%x")
      for (i <- 0 until axi_w / 8) {
        data_buf += (data >> (8 * i)).toByte
      }
      recv_cntr += 2
    }
  }
}
