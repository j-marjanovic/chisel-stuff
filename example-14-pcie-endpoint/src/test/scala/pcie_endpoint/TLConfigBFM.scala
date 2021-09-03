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

import scala.util.Random

class TLConfigBFM(
    iface: Interfaces.TLConfig,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val rnd_gen: Random
) extends Bfm {

  var cntr: Int = 0
  var addr: Int = 0

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {

    poke(iface.add, addr)
    if (cntr == 0 || cntr == 1 || cntr == 6 || cntr == 7) {
      poke(iface.ctl, rnd_gen.nextLong())
    } else {
      if (addr == 0xf) {
        poke(iface.ctl, 4)
      } else {
        // TODO: add other regs
        poke(iface.ctl, 0)
      }
    }

    cntr += 1
    if (cntr > 7) {
      cntr = 0
      addr = (addr + 1) % 16
    }
  }
}
