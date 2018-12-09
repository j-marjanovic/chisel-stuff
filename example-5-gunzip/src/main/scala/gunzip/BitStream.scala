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

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class BitStream(val bytes: List[Byte]) {
  private def bytesToBits(bytes: List[Byte]): List[Int] = {
    val bits = ListBuffer[Int]()

    for (byt <- bytes) {
      for (i <- 0 to 7) {
        bits += (byt & (1 << i)) >> i
      }
    }

    bits.toList
  }

  val in_bits = bytesToBits(bytes)
  val xs: mutable.Queue[Int] = mutable.Queue[Int]()
  xs ++= in_bits

  def getBits(len: Int): Int = {
    var accum : Int = 0
    for (i <- 0 until len) {
      accum |= xs.dequeue() << i
    }
    accum
  }

}
