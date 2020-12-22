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

import bfmtester.Bfm
import chisel3._

import scala.collection.mutable.ListBuffer

class DecompressorKernelMonitor(
    val iface: DecompressorKernelOutputInterface,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val resp = ListBuffer[BigInt]()
  private var verbose: Boolean = true
  private val w: Int = iface.data.length

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    if (verbose) {
      println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
    }
  }

  def setVerbose(enable_verbose: Boolean) {
    verbose = enable_verbose
  }

  def respGet(): List[BigInt] = {
    val ls = resp.result()
    resp.clear()
    ls
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val vld = peek(iface.vld)
    var dbg_str: String = ""
    if (vld != 0) {
      for (i <- 0 until w) {
        val data = peek(iface.data(i))
        dbg_str += f" ${data}%02x"
        resp += data
      }

      printWithBg(f"${t}%5d DecompKernelMonitor($ident):  data = ${dbg_str}")
    }
  }

  printWithBg(f"      DecompKernelMonitor($ident): BFM initialized")
}
