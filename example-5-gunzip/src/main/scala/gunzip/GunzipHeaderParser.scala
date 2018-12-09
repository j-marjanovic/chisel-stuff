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

import java.io.BufferedInputStream

import scala.collection.mutable.{ListBuffer, MutableList}
import chisel3._


object GunzipHeaderParser {
  val GZIP_HEADER_LEN = 10

  def parse(file: java.net.URL): (List[Option[Int]], List[Option[Int]]) = {
    val in = file.openStream()
    val bis = new BufferedInputStream(in)
    val in_bytes = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toList

    val bs = new BitStream(in_bytes.slice(GZIP_HEADER_LEN, in_bytes.length))

    val bfinal = bs.getBits(1)
    val btype = bs.getBits(2)

    println(s"bfinal: ${bfinal}, btype: ${btype}")

    val HLIT = bs.getBits(5) + 257
    val HDIST = bs.getBits(5) + 1
    val HCLEN = bs.getBits(4) + 4

    val CL_ORDER : List[Int] = List(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

    val cl : MutableList[Int] = MutableList.fill(19)(0)
    for (i <- 0 until HCLEN) {
      cl(CL_ORDER(i)) = bs.getBits(3)
    }

    val codes_lens = gen_codes(cl.toList, 19)

    val ht_lens = new HuffmanTree(codes_lens)

    val lens_litlen = get_lengths(ht_lens, bs, HLIT)
    val codes_litlen = gen_codes(lens_litlen, HLIT)

    val lens_dist = get_lengths(ht_lens, bs, HDIST)
    val codes_dist = gen_codes(lens_dist, HDIST)

    val litTree: List[Option[Int]] = new HuffmanTree(codes_litlen).heap.toList
    val distTree: List[Option[Int]] = new HuffmanTree(codes_dist).heap.toList

    Tuple2(litTree, distTree)
  }

  def remove_hdr(file: java.net.URL) = {
    val in = file.openStream()
    val bis = new BufferedInputStream(in)
    val in_bytes = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toList

    val bs = new BitStream(in_bytes.slice(GZIP_HEADER_LEN, in_bytes.length))

    val bfinal = bs.getBits(1)
    val btype = bs.getBits(2)

    println(s"bfinal: ${bfinal}, btype: ${btype}")

    val HLIT = bs.getBits(5) + 257
    val HDIST = bs.getBits(5) + 1
    val HCLEN = bs.getBits(4) + 4

    val CL_ORDER : List[Int] = List(16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15)

    val cl : MutableList[Int] = MutableList.fill(19)(0)
    for (i <- 0 until HCLEN) {
      cl(CL_ORDER(i)) = bs.getBits(3)
    }

    val codes_lens = gen_codes(cl.toList, 19)

    val ht_lens = new HuffmanTree(codes_lens)

    val lens_litlen = get_lengths(ht_lens, bs, HLIT)
    val codes_litlen = gen_codes(lens_litlen, HLIT)

    val lens_dist = get_lengths(ht_lens, bs, HDIST)
    val codes_dist = gen_codes(lens_dist, HDIST)

    val litTree: List[Option[Int]] = new HuffmanTree(codes_litlen).heap.toList
    val distTree: List[Option[Int]] = new HuffmanTree(codes_dist).heap.toList

    bs
  }

  private def gen_codes(cl: List[Int], len: Int): List[HuffmanCode] = {
    val len_freq = MutableList.fill(len)(0)
    for (cli <- cl) {
      if (cli > 0) {
        len_freq(cli) += 1
      }
    }

    val init_code_per_len = MutableList.fill(len + 1)(0)
    init_code_per_len(0) = 0
    for (i <- 1 until len + 1) {
      init_code_per_len(i) = 2*(init_code_per_len(i-1) + len_freq(i-1))
    }

    val codes = MutableList.fill(len)(new HuffmanCode(0, 0))
    for (i <- 0 until len) {
      if (cl(i) != 0) {
        codes(i) = new HuffmanCode(init_code_per_len(cl(i)), cl(i))
        init_code_per_len(cl(i)) += 1
      }
    }

    codes.toList
  }

  private def get_lengths(ht: HuffmanTree, bs: BitStream, len: Int): List[Int] = {
    val lst = ListBuffer[Int]()

    while(lst.length < len) {
      val symbol = ht.get(bs)
      if (symbol < 16) {
        lst += symbol
      } else if (symbol == 16) {
        val size = bs.getBits(2) + 3
        for (_ <- 0 until size) {
          lst += lst.reverse.head
        }
      } else if (symbol == 17) {
        val size = bs.getBits(3) + 3
        for (_ <- 0 until size) {
          lst += 0
        }
      } else if (symbol == 18) {
        val size = bs.getBits(7) + 11
        for (_ <- 0 until size) {
          lst += 0
        }
      } else {
        throw new RuntimeException("unrecognized symbol for length")
      }
    }

    lst.toList
  }

  // generate Chisel vector
  def genVec(lst: List[Option[Int]]): Vec[GunzipHuffDecoderElem] = {
    val ht = Reg(Vec(lst.length, new GunzipHuffDecoderElem))

    for (i <- lst.indices) {
      ht(i).valid := lst(i).isDefined.B
      ht(i).data := lst(i).getOrElse(0).U
    }

    ht
  }

}
