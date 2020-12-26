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
import chisel3._

import scala.collection.mutable.ListBuffer

class AxiMasterCoreUserDriver(
    val write_if: AxiMasterCoreWriteIface,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val data = ListBuffer[Byte]()
  private var addr: BigInt = 0
  private var len: BigInt = 0
  private var addr_valid: Boolean = false
  private var addr_valid_p: Boolean = false
  private var data_rem: BigInt = 0
  private var act: Boolean = true

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  def add_data(ls: Seq[Byte]): Unit = {
    data ++= ls
  }

  def start_write(addr: BigInt, len: BigInt): Unit = {
    this.addr = addr
    this.len = len
    data_rem = len
    addr_valid = true
    addr_valid_p = false
    printWithBg(
      f"      AxiMasterCoreUserDriver($ident): start write (addr=0x${addr}%x, len=${len})"
    )
  }

  private def get_data_w(): BigInt = {
    var tmp: BigInt = 0
    for (i <- (128 / 8 - 1) to 0 by -1) {
      tmp <<= 8
      tmp |= data.remove(i) & 0xff
    }
    tmp
  }

  def stats(): Unit = {
    printWithBg(f"      AxiMasterCoreUserDriver($ident): data len = ${data.size}")
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    if (addr_valid_p) {
      poke(write_if.addr, 0)
      poke(write_if.len, 0)
      poke(write_if.start, 0)
      addr_valid_p = false
    }

    if (addr_valid) {
      poke(write_if.addr, this.addr)
      poke(write_if.len, this.len)
      poke(write_if.start, 1)
      addr_valid = false
      addr_valid_p = true
    }

    val ready = peek(write_if.ready) > 0

    if (data_rem > 0) {
      if (ready) {
        val data = get_data_w()
        poke(write_if.data, data)
        poke(write_if.valid, 1)
        printWithBg(f"${t}%5d AxiMasterCoreUserDriver($ident): sent data=${data}%x")
        act = true
        data_rem -= 1
      }
    } else if (act) {
      if (ready) {
        act = false
        printWithBg(f"${t}%5d AxiMasterCoreUserDriver($ident): done")
      }
    } else {
      poke(write_if.data, 0)
      poke(write_if.valid, 0)
    }
  }

  printWithBg(f"      AxiMasterCoreUserDriver($ident): BFM initialized")

}
