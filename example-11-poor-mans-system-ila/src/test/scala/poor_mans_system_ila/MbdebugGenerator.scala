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

package poor_mans_system_ila

import bfmtester._
import chisel3._

import scala.collection.mutable
import scala.util.Random

class MbdebugGenerator(val mbdebug: MbdebugBundle,
                       val peek: Bits => BigInt,
                       val poke: (Bits, BigInt) => Unit,
                       val println: String => Unit,
                       val rnd_gen: Random)
    extends Bfm {

  private var cntr: Int = 0
  private val tdi_queue = mutable.Queue[Int]()
  private var quiet: Boolean = false

  def quiet_set(_quiet: Boolean): Unit = quiet = _quiet

  def poke_with_quiet(signal: Bits, value: BigInt): Unit = {
    if (!quiet) {
      poke(signal, value)
    }
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    cntr += 1
    if (cntr == 4) {
      poke_with_quiet(mbdebug.CLK, 0)
    } else if (cntr >= 9) {
      cntr = 0
      poke_with_quiet(mbdebug.CLK, 1)
      val tdi = if (rnd_gen.nextBoolean()) { 1 } else { 0 }
      poke_with_quiet(mbdebug.TDI, tdi)
      tdi_queue.enqueue(tdi)
      if (tdi_queue.size > 5) {
        val tdo = tdi_queue.dequeue()
        poke_with_quiet(mbdebug.TDO, tdo)
      }
    }
  }
}
