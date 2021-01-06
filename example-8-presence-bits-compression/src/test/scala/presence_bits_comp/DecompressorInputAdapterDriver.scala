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

class DecompressorInputAdapterDriver(
    val iface: AxiMasterCoreReadIface,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val w: Int = iface.data.getWidth
  private val data: ListBuffer[Byte] = new ListBuffer[Byte]()

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  def add_data(ls: Seq[Byte]): Unit = {
    data ++= ls
  }

  private def get_data_w(): BigInt = {
    var tmp: BigInt = 0
    for (i <- (w / 8 - 1) to 0 by -1) {
      tmp <<= 8
      tmp |= data.remove(i) & 0xff
    }
    tmp
  }

  var in_drive: Boolean = false
  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val rdy = peek(iface.ready) > 0
    if (data.nonEmpty) {
      if (!in_drive || (in_drive && rdy)) {
        in_drive = true
        val d = get_data_w()
        poke(iface.data, d)
        poke(iface.valid, 1)
        printWithBg(f"${t}%5d DecompressorInputAdapterDriver($ident): data=${d}%x")
      }
    } else {
      in_drive = false
      poke(iface.data, 0)
      poke(iface.valid, 0)
    }
  }

  printWithBg(f"      DecompressorInputAdapterDriver($ident): BFM initialized")
}
