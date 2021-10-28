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

class AvalonStreamDataOutBfm(
    val data_out: Interfaces.AvalonStreamDataOut,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit
) extends Bfm {

  private val data = ListBuffer[Byte]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  def get_data(): List[Byte] = {
    val tmp = data.toList
    data.clear()
    tmp
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val valid = peek(data_out.valid) > 0

    if (valid) {
      val data_beat = peek(data_out.data)
      // TODO: handle empty
      for (i <- 0 until data_out.data.getWidth / 8) {
        val b: Byte = ((data_beat >> (8 * i)) & 0xff).toByte
        data += b
      }
      printWithBg(f"${t}%5d AvalonStreamDataOutBfm: recv ${data_beat}%x")
    }
  }

  printWithBg(f"      AvalonStreamDataOutBfm: BFM initialized")
}
