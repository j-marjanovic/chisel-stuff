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

import chisel3._
import bfmtester._

import scala.collection.mutable.ListBuffer
import scala.util.Random

class AxiMasterCoreUserMonitor(
    val read_if: AxiMasterCoreReadIface,
    val rnd: Random,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val data: ListBuffer[Byte] = ListBuffer[Byte]()
  private val width: Int = read_if.data.getWidth
  private var addr: BigInt = 0
  private var len: BigInt = 0
  private var addr_valid: Boolean = false
  private var addr_valid_p: Boolean = false

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  private def data_push(x: BigInt): Unit = {
    for (i <- 0 until width / 8) {
      val b = (x >> (8 * i)).toByte
      data += b
    }
  }

  def data_get(): List[Byte] = {
    val tmp = data.toList
    data.clear()
    tmp
  }

  def start_read(addr: BigInt, len: BigInt): Unit = {
    this.addr = addr
    this.len = len
    addr_valid = true
    addr_valid_p = false
    printWithBg(f"      AxiMasterCoreUserDriver($ident): start read (addr=0x${addr}%x, len=${len})")
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    if (addr_valid_p) {
      poke(read_if.addr, 0)
      poke(read_if.len, 0)
      poke(read_if.start, 0)
      addr_valid_p = false
    }

    if (addr_valid) {
      poke(read_if.addr, this.addr)
      poke(read_if.len, this.len)
      poke(read_if.start, 1)
      addr_valid = false
      addr_valid_p = true
    }

    val valid: Boolean = peek(read_if.valid) > 0
    val ready_prev: Boolean = peek(read_if.ready) > 0
    val ready = if(rnd.nextBoolean()) 1 else 0
    poke(read_if.ready, ready)
    if (ready_prev & valid) {
      val data = peek(read_if.data)
      printWithBg(f"${t}%5d AxiMasterCoreUserDriver($ident): recv data=${data}%x")
      data_push(data)
    }
  }

  printWithBg(f"      AxiMasterCoreUserMonitor($ident): BFM initialized")

  // poke(read_if.ready, 1)
}
