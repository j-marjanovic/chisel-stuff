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
import bfmtester.Bfm

import scala.collection.mutable.ListBuffer
import scala.util.Random

class DecompressorKernelDriver(
    val iface: DecompressorKernelInputInterface,
    val rnd: Random,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val stim: ListBuffer[BigInt] = ListBuffer[BigInt]()
  private var verbose: Boolean = true
  private val w: Int = iface.data.length

  def stimAppend(data: BigInt): Unit = {
    stim += data
  }
  def stimAppend(ls: List[BigInt]): Unit = {
    stim ++= ls
  }

  def setVerbose(enable_verbose: Boolean) {
    verbose = enable_verbose
  }

  private def printWithBg(s: String): Unit = {
    // black on orange
    if (verbose) {
      println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
    }
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    update_adv(t, poke)
    update_drive(t, poke)
  }

  def update_drive(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val rnd_val: Boolean = rnd.nextBoolean()
    if (rnd_val && stim.nonEmpty) {
      poke(iface.en, 1)
      val w_ = w.min(stim.size)
      var dbg_str: String = ""
      for (i <- 0 until w_) {
        poke(iface.data(i), stim(i))
        dbg_str += f" ${stim(i)}%02x"
      }
      printWithBg(f"${t}%5d DecompKernelDriver($ident): data =${dbg_str}")
    } else {
      poke(iface.en, 0)
      for (i <- 0 until w) {
        poke(iface.data(i), 0)
      }
    }
  }

  def update_adv(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    // remove the words which were already processed
    val adv_en: BigInt = peek(iface.adv_en)
    val adv: BigInt = peek(iface.adv)

    if (adv_en != 0) {
      printWithBg(f"${t}%5d DecompKernelDriver($ident): adv = ${adv}")
      stim.remove(0, adv.toInt)
    }
  }

  printWithBg(f"      DecompKernelDriver($ident): BFM initialized")
}
