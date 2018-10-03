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

package axiLiteExample

import chisel3.{Bits, Bool, UInt}

import scala.collection.mutable.ListBuffer

/**
  * Monitor for data+valid interface - stores all received data in internal list
  */
class ValidIfMonitor(val data: UInt,
                     val valid: Bool,
                     val peek: Bits => BigInt,
                     val poke: (Bits, BigInt) => Unit,
                     val println: String => Unit) extends ValidIfBfm {
  private var resp = ListBuffer[BigInt]()

  def respGet(): List[BigInt] = {
    val ls = resp.result()
    resp.clear()
    ls
  }

  def update(t: Long): Unit = {
    val vld = peek(valid)
    if (vld != 0) {
      val d = peek(data)
      resp += d
      println(f"${t}%5d Monitor: received ${d}")
    }
  }
}
