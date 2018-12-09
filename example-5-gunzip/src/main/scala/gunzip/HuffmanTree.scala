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

import scala.collection.mutable.MutableList

class HuffmanTree(val codes: List[HuffmanCode]) {
  val max_code_len = codes.maxBy(_.length).length
  val max_nr_els = geometric_series_sum(max_code_len) + 1
  var heap: MutableList[Option[Int]] = MutableList.fill(max_nr_els)(None)

  for (i <- codes.indices) {
    if (codes(i).length > 0) {
      insert_code(codes(i), i)
    }
  }


  def get(bs: BitStream): Int = {
    var idx = 0
    while (idx < heap.size) {
      if (heap(idx).isDefined) {
        return heap(idx).get
      }
      val jmp = bs.getBits(1)
      idx = idx*2 + 1 + jmp
    }

    throw new RuntimeException("heap index out of bounds")
  }


  private def insert_code(code: HuffmanCode, symbol: Int): Unit = {
    var idx = 0
    for (i <- code.length-1 to 0 by -1) {
      val mask = 1 << i
      val jmp = if ((code.code & mask) != 0) { 1 } else { 0 }
      idx = idx*2 + 1 + jmp
    }

    heap(idx) = Some(symbol)
  }

  private def geometric_series_sum(n: Int): Int = {
    val a0 = 2
    var pow_2_n = 1

    for (i <- 0 until n) {
      pow_2_n *= 2
    }

    return a0 * (1 - pow_2_n) / (1 - 2)
  }
}
