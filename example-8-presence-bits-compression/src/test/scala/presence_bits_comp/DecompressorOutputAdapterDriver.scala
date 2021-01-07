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

import scala.util.Random

class DecompressorOutputAdapterDriver(
    val iface: DecompressorKernelOutputInterface,
    val rnd: Random,
    val kernel_w: Integer,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private var trans_cntr: Int = 0
  private var in_drive: Boolean = false

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  var rdy_prev: Boolean = peek(iface.ready) > 0

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    /*
    val vld = rnd.nextBoolean()
    val rdy = peek(iface.ready) > 0
    poke(iface.vld, if (vld) 1 else 0)
    if ((!in_drive && vld) || (in_drive && vld && rdy && rdy_prev)) {
      in_drive = true
      poke(iface.vld, 1)
      var s : String = ""
      for (i <- 0 until kernel_w) {
        val data = i + 1 + (trans_cntr << 4)
        poke(iface.data(i), data)
        s += f"${data}%02x"
      }
      printWithBg(f"${t}%5d drive, ${s}, rdy = ${rdy}, rdy_prev = ${rdy_prev}")
      trans_cntr += 1
    }
    printWithBg(f"${t}%5d update, rdy = ${rdy}, rdy_prev = ${rdy_prev}")
    rdy_prev = rdy
     */

    val vld_nxt = rnd.nextBoolean()
    val rdy = peek(iface.ready) > 0
    if ((!in_drive && vld_nxt) || (in_drive && vld_nxt && rdy)) {
      in_drive = true
      poke(iface.vld, 1)
      var s : String = ""
      for (i <- 0 until kernel_w) {
        val data = i + 1 + (trans_cntr << 4)
        poke(iface.data(i), data)
        s += f"${data}%02x"
      }
      printWithBg(f"${t}%5d drive, ${s}, rdy_prev = ${rdy_prev}")
      trans_cntr += 1
    } else if (in_drive && !vld_nxt && rdy) {
      in_drive = false
      poke(iface.vld, 0)
    }
  }

}
