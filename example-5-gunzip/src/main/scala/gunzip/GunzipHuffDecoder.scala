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

class GunzipHuffDecoder(val HUFF_TREE: List[Option[Int]]) extends Module {
  val io = IO(new Bundle {
    val data_in_valid = Input(Bool())
    val data_in_ready = Output(Bool())
    val data_in_bits = Input(UInt(1.W))

    val enable = Input(Bool())

    val data = Output(UInt(9.W))
    val valid = Output(Bool())
  })

  val lit_idx = RegInit(UInt(9.W), 0.U)
  val huff_tree_vec = GunzipHeaderParser.genVec(HUFF_TREE)
  val data_reg = Reg(UInt(9.W))
  val valid_reg = RegNext(io.valid)

  io.data_in_ready := false.B

  io.data := data_reg
  io.valid := false.B

  when (io.data_in_valid) {
    when (lit_idx >= huff_tree_vec.length.U) {
      // decoder error
      printf("DEC ERR\n")
    } .elsewhen (io.enable) {
      io.data_in_ready := true.B
      // adv idx
      lit_idx := lit_idx*2.U + 1.U + io.data_in_bits

      when (huff_tree_vec(lit_idx).valid) {
        lit_idx := 0.U
        io.data_in_ready := false.B
        io.data := huff_tree_vec(lit_idx).data
        data_reg := huff_tree_vec(lit_idx).data
        io.valid := true.B
      }
    } .otherwise {
      lit_idx := 0.U
    }
  }
}
