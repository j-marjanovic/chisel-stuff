package axiLiteExample

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

import chisel3.{Bits, Bool, UInt}

import scala.collection.mutable.ListBuffer

/**
  * Driver for data+valid interface - drives everything which gets appended
  * to stimulus on the interface
  */
class ValidIfDriver(val data: UInt,
                    val valid: Bool,
                    val peek: Bits => BigInt,
                    val poke: (Bits, BigInt) => Unit,
                    val println: String => Unit) extends ValidIfBfm{
  private var stim = ListBuffer[BigInt]()

  def stimAppend(x: BigInt): Unit = {
    stim += x
  }

  def stimAppend(ls: List[BigInt]): Unit = {
    stim ++= ls
  }

  def update(t: Long): Unit = {
    if (stim.nonEmpty) {
      poke(valid, 1)
      val d = stim.remove(0)
      poke(data, d)
      println(f"${t}%5d Driver: sent ${d}")
    } else {
      poke(valid, 0)
    }
  }
}
