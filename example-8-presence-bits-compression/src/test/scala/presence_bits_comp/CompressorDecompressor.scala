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

import java.io.BufferedInputStream
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object CompressorDecompressor {
  val SIZE = 8

  def read_from_file(filename: java.net.URL): List[Byte] = {
    val in = filename.openStream()
    val bis = new BufferedInputStream(in)
    val in_bytes = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toList
    in_bytes
  }

  def compress(in_bytes: List[Byte]): List[Byte] = {
    val out_bytes = ListBuffer[Byte]()

    val chunks: Iterator[List[Byte]] = in_bytes.grouped(SIZE)
    for (chunk <- chunks) {
      out_bytes ++= compress_chunk(chunk)
    }

    out_bytes.toList
  }

  def decompress(in_bytes: List[Byte]): List[Byte] = {
    val in_buffer: ListBuffer[Byte] = in_bytes.to[ListBuffer]
    val out_bytes: ListBuffer[Byte] = ListBuffer[Byte]()

    while (in_buffer.nonEmpty) {
      val pres_bits = in_buffer.remove(0)
      for (i <- 0 until 8) {
        if (((pres_bits >> i) & 1) == 1) {
          out_bytes += in_buffer.remove(0)
        } else {
          out_bytes += 0
        }
      }
    }

    out_bytes.toList
  }

  private def compress_chunk(in_lst: Seq[Byte]): Seq[Byte] = {
    var pres_bits: Int = 0
    val out_lst: ListBuffer[Byte] = ListBuffer[Byte]()
    for ((el, i) <- in_lst.zipWithIndex) {
      if (el != 0) {
        pres_bits |= (1 << i)
        out_lst += el
      }
    }
    pres_bits.toByte :: out_lst.toList
  }
}
