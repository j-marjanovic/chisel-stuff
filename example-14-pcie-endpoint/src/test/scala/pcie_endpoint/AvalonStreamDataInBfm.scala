/*
Copyright (c) 2021 Jan Marjanovic

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

package pcie_endpoint

import bfmtester._
import chisel3._

import scala.collection.mutable.ListBuffer

class AvalonStreamDataInBfm(
    val data_in: Interfaces.AvalonStreamDataIn,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit
) extends Bfm {

  private val data = ListBuffer[BigInt]()

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  def transmit_data(word: BigInt): Unit = {
    data += word
  }

  private var iface_ready_prev: Boolean = false
  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val data_avail = data.nonEmpty
    val iface_ready = peek(data_in.ready) > 0
    val iface_valid_prev = peek(data_in.valid) > 0

    if ((!iface_valid_prev && data_avail) || (iface_ready_prev && data_avail)) {
      val data_beat = data.remove(0)
      poke(data_in.valid, 1)
      poke(data_in.data, data_beat)
      printWithBg(f"${t}%5d AvalonStreamRxBfm: drive ${data_beat}%x")
    } else if (iface_valid_prev && iface_ready_prev && !data_avail) {
      poke(data_in.valid, 0)
      poke(data_in.data, 0)
    }

    iface_ready_prev = iface_ready
  }

  printWithBg(f"      AvalonStreamRxBfm: BFM initialized")
}
