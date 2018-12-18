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

class GunzipLenDistLut(val LUT_VALS: List[Int],
                       val EXTS: List[Int],
                       val OUT_W: Int) extends Module {
  val io = IO(new Bundle {
    val data_in_valid = Input(Bool())
    val data_in_ready = Output(Bool())
    val data_in_bits = Input(UInt(1.W))

    val enable = Input(Bool())
    val lut_idx = Input(UInt(9.W))

    val done = Output(Bool())
    val lut_out = Output(UInt(OUT_W.W))
  })

  def write_at(input: UInt, idx: UInt, prev: UInt): UInt = {
    val out = Wire(UInt())
    out := 0.U

    switch (idx) {
      is (0.U) { out := Cat(prev(prev.getWidth-1, 1), input)}
      is (1.U) { out := Cat(prev(prev.getWidth-1, 2), Cat(input, prev(0, 0)))}
      is (2.U) { out := Cat(prev(prev.getWidth-1, 3), Cat(input, prev(1, 0)))}
      is (3.U) { out := Cat(prev(prev.getWidth-1, 4), Cat(input, prev(2, 0)))}
      is (4.U) { out := Cat(prev(prev.getWidth-1, 5), Cat(input, prev(3, 0)))}
      is (5.U) { out := Cat(prev(prev.getWidth-1, 6), Cat(input, prev(4, 0)))}
      /*is (6.U) { out := Cat(prev(prev.getWidth-1, 7), Cat(input, prev(5, 0)))}
      is (7.U) { out := Cat(prev(prev.getWidth-1, 8), Cat(input, prev(6, 0)))} */
    }
    out
  }

  val vals: Vec[UInt] = VecInit.tabulate(LUT_VALS.length){ i => LUT_VALS(i).U(OUT_W.W)}
  val exts: Vec[UInt] = VecInit.tabulate(EXTS.length){ i => EXTS(i).U(OUT_W.W)}

  val sIdle :: sAccum :: sAccumOut :: Nil = Enum(3)
  val state = RegInit(sIdle)

  // output regs
  val done_reg = Reg(Bool())
  val out_reg = Reg(UInt(OUT_W.W))
  done_reg := false.B
  io.data_in_ready := false.B

  // accum regs
  val val_init  = Reg(UInt(9.W))
  val ext_idx  = Reg(UInt(5.W))
  val ext_lim  = Reg(UInt(5.W))
  val ext_val  = Reg(UInt(7.W))

  switch (state) {
    is (sIdle) {
      when (io.enable && !io.done) {
        when (exts(io.lut_idx) > 0.U) {
          state := sAccum
          val_init := vals(io.lut_idx)
          ext_idx := 0.U
          ext_lim := exts(io.lut_idx) - 1.U
          ext_val := 0.U
        } .otherwise {
          state := sIdle
          done_reg := true.B
          out_reg := vals(io.lut_idx)
        }
      }
    }
    is (sAccum) {
      when (io.data_in_valid) {
        io.data_in_ready := true.B
        ext_val := write_at(io.data_in_bits, ext_idx, ext_val)
        ext_idx := ext_idx + 1.U

        when(ext_idx === ext_lim) {
          state := sAccumOut
        }
      }
    }
    is (sAccumOut) {
      state := sIdle
      done_reg := true.B
      out_reg := val_init + ext_val
    }
  }

  io.lut_out := out_reg
  io.done := done_reg

  when (done_reg) {
    io.data_in_ready := false.B
  }
}
