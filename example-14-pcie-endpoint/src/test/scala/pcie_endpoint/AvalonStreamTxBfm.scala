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

import bfmtester.Bfm
import chisel3.Bits

import scala.collection.mutable.ListBuffer

class AvalonStreamTxBfm(
    val av_st_tx: Interfaces.AvalonStreamTx,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit
) extends Bfm {

  class RecvData(val data: BigInt, val empty: BigInt) {}

  val recv_buffer = ListBuffer[RecvData]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    poke(av_st_tx.ready, 1)
    val valid = peek(av_st_tx.valid)
    if (valid > 0) {
      val data = peek(av_st_tx.data)
      val empty = peek(av_st_tx.empty)
      recv_buffer += new RecvData(data, empty)
      printWithBg(f"${t}%5d AvalonStreamTxBfm: data = ${data}%x, empty = ${empty}")
    }
  }

  printWithBg(f"      AvalonStreamTxBfm: BFM initialized")

}
