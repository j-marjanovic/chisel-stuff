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

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

class DecompressorInputAdapterMonitor(
    val iface: DecompressorKernelInputInterface,
    val rnd: Random,
    val almost_end: Int,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val l = iface.data.length
  private val data: ListBuffer[Byte] = new ListBuffer[Byte]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  private def seq_to_bigint(xs: Seq[BigInt]): BigInt = {
    var tmp: BigInt = 0
    for (x <- xs.reverse) {
      tmp <<= 8
      tmp |= x
    }
    tmp
  }

  def get_data(): List[Byte] = {
    val tmp = data.toList
    data.clear()
    tmp
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val en = peek(iface.en) > 0
    if (en) {
      val adv_en = peek(iface.adv_en) > 0
      val vals_to_read_prev = peek(iface.adv).toInt
      // read slow at the end to check if the last word is handled correctly
      val vals_to_read = if (data.length >= almost_end) rnd.nextInt(2) + 1 else rnd.nextInt(l) + 1
      if (adv_en) {
        val d_lst: immutable.Seq[BigInt] = for (i <- 0 until vals_to_read_prev) yield peek(iface.data(i))
        val d = seq_to_bigint(d_lst)
        data ++= d_lst.map(_.toByte)
        printWithBg(
          f"${t}%5d DecompressorInputAdapterMonitor($ident): vs = ${vals_to_read_prev}, data = 0x${d}%x"
        )
      }
      poke(iface.adv, vals_to_read)
      poke(iface.adv_en, 1)
    } else {
      poke(iface.adv, 0)
      poke(iface.adv_en, 0)
    }
  }

  printWithBg(f"      DecompressorInputAdapterMonitor($ident): BFM initialized")
  printWithBg(f"      DecompressorInputAdapterMonitor($ident):   l = ${l}")
}
