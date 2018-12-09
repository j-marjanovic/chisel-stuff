/*
MIT License

Copyright (c) 2018 Jan Marjanovic

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

package gunzip

import chisel3.Bits
import chisel3.util.DecoupledIO

import scala.collection.mutable.ListBuffer

/**
  * Monitor for data+valid interface - stores all received data in internal list
  */
class DecoupledMonitor(val iface: DecoupledIO[GunzipOutput],
                       val peek: Bits => BigInt,
                       val poke: (Bits, BigInt) => Unit,
                       val println: String => Unit) extends DecoupledBfm {
  private var resp = ListBuffer[BigInt]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  def respGet(): List[BigInt] = {
    val ls = resp.result()
    resp.clear()
    ls
  }

  def update(t: Long): Unit = {
    poke(iface.ready, 1)
    val vld = peek(iface.valid)
    if (vld != 0) {
      val d = peek(iface.bits.data)
      val last = peek(iface.bits.last)
      if (last > 0) {
        throw AllDone
      }
      resp += d
      printWithBg(f"${t}%5d Monitor: received ${d}")
    }
  }
}
