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

import chisel3._
import chisel3.util._


class GunzipEngine(val filename: java.net.URL) extends Module {
  val io = IO(new Bundle {
    val data_in = DeqIO(UInt(8.W))
    val data_out = EnqIO(new GunzipOutput)
  })

  val huff_lit_dist_trees_bit_skip = GunzipHeaderParser.parse(filename)
  val huff_lit_tree: List[Option[Int]] = huff_lit_dist_trees_bit_skip._1
  val huff_dist_tree: List[Option[Int]] = huff_lit_dist_trees_bit_skip._2
  val initial_bit_skip: Int = huff_lit_dist_trees_bit_skip._3
  val huff_dist_vec: Vec[GunzipHuffDecoderElem] = GunzipHeaderParser.genVec(huff_dist_tree)

  //==========================================================================
  // initial bit skipper
  val bit_skipper = Module(new GunzipBitSkipper(initial_bit_skip))
  bit_skipper.io.data_in_valid := io.data_in.valid

  //==========================================================================
  // lit decoder
  val dec_lit_en = Wire(Bool())

  val dec_lit = Module(new GunzipHuffDecoder(huff_lit_tree))
  dec_lit.io.data_in_valid := io.data_in.valid
  dec_lit.io.data_in_bits := io.data_in.bits(0)
  dec_lit.io.enable := dec_lit_en

  //==========================================================================
  // len handling
  val C_LENS = List(3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
    35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258)
  val C_LEXT = List(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
    3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0)

  val lut_len_en = Wire(Bool())

  val lut_len = Module(new GunzipLenDistLut(C_LENS, C_LEXT, 15))
  lut_len.io.data_in_bits := io.data_in.bits(0)
  lut_len.io.data_in_valid := io.data_in.valid
  lut_len.io.enable := lut_len_en
  lut_len.io.lut_idx := dec_lit.io.data - 257.U

  //==========================================================================
  // dist decoder
  val dec_dist_en = Wire(Bool())

  val dec_dist = Module(new GunzipHuffDecoder(huff_dist_tree))
  dec_dist.io.data_in_valid := io.data_in.valid
  dec_dist.io.data_in_bits := io.data_in.bits(0)
  dec_dist.io.enable := dec_dist_en

  //==========================================================================
  // dist handling
  val C_DISTS = List(1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193,
    257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145,
    8193, 12289, 16385, 24577)
  val C_DEXT = List(0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6,
    7, 7, 8, 8, 9, 9, 10, 10, 11, 11,
    12, 12, 13, 13)

  val lut_dist_en = Wire(Bool())
  val lut_dist = Module(new GunzipLenDistLut(C_DISTS, C_DEXT, 15))
  lut_dist.io.data_in_bits := io.data_in.bits(0)
  lut_dist.io.data_in_valid := io.data_in.valid
  lut_dist.io.enable := lut_dist_en
  lut_dist.io.lut_idx := dec_dist.io.data

  //==========================================================================
  // output buffer
  val out_buf = Module(new GunzipOutBuf())
  out_buf.io.data_in := io.data_out.bits.data
  out_buf.io.valid_in := io.data_out.valid && io.data_out.ready

  out_buf.io.dist := lut_dist.io.lut_out
  out_buf.io.len  := lut_len.io.lut_out
  out_buf.io.enable := false.B

  out_buf.io.data_out_ready := io.data_out.ready

  //==========================================================================
  // fsm
  val sInit :: sDecode :: sLen :: sDistDec :: sDist :: sCopy :: Nil = Enum(6)
  val state = RegInit(sInit)

  val out_data = Reg(UInt())
  val out_valid = Reg(Bool())
  val out_last = Reg(Bool())
  out_data  := 0.U
  out_valid := false.B
  out_last := false.B

  dec_lit_en := false.B
  lut_len_en := false.B
  dec_dist_en := false.B
  lut_dist_en := false.B

  switch (state) {
    is (sInit) {
      when (bit_skipper.io.done) {
        state := sDecode
        printf(p"Bit skip finished\n")
      }
    }
    is (sDecode) {
      dec_lit_en := true.B
      when (dec_lit.io.valid) {
        when (dec_lit.io.data < 256.U) {
          out_data := dec_lit.io.data(7, 0)
          out_valid := true.B
        } .elsewhen (dec_lit.io.data === 256.U) {
          out_last := true.B
          out_valid := true.B
          out_data := 0.U
        } .otherwise {
          state := sLen
        }
      }
    }
    is (sLen) {
      lut_len_en := true.B
      when (lut_len.io.done) {
        state := sDistDec
        printf(p"  len: ${lut_len.io.lut_out}\n")
      }
    }
    is (sDistDec) {
      dec_dist_en := true.B
      when (dec_dist.io.valid) {
        state := sDist
      }
    }
    is (sDist) {
      lut_dist_en := true.B
      when (lut_dist.io.done) {
        state := sCopy
        printf(p"  dist: ${lut_dist.io.lut_out}\n")
      }
    }
    is (sCopy) {
      out_buf.io.enable := true.B
      out_valid := out_buf.io.data_out_valid
      out_data  := out_buf.io.data_out_bits

      when (out_buf.io.done) {
        state := sDecode
      }
    }
  }

  io.data_out.valid := out_valid
  io.data_out.bits.data := out_data
  io.data_out.bits.last := out_last

  io.data_in.ready := false.B
  when (state === sInit) {
    io.data_in.ready := bit_skipper.io.data_in_ready
  }
  when (state === sDecode) {
    io.data_in.ready := dec_lit.io.data_in_ready
  }
  when (state === sLen) {
    io.data_in.ready := lut_len.io.data_in_ready
  }
  when (state === sDistDec) {
    io.data_in.ready := dec_dist.io.data_in_ready
  }
  when (state === sDist) {
    io.data_in.ready := lut_dist.io.data_in_ready
  }

}
