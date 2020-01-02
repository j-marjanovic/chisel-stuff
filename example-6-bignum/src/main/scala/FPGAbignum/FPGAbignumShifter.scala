/*
MIT License

Copyright (c) 2019 Jan Marjanovic

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

package FPGAbignum

import bfmtester._
import chisel3._
import chisel3.util._

class FPGAbignumShifter(val shift_by: Int, val data_width: Int = 8)
    extends Module {
  val io = IO(new Bundle {
    val d = new AxiStreamIf(data_width.W)
    val q = Flipped(new AxiStreamIf(data_width.W))
  })

  assert(shift_by <= data_width, "Shift needs to be smaller than data width")

  //val axis_out = Wire(new AxiStreamIf(data_width.W))

  val data_r1 = RegInit(UInt(data_width.W), 0.U)
  val data_r2 = RegInit(UInt(data_width.W), 0.U)
  val valid_r1 = RegInit(Bool(), false.B)
  val valid_r2 = RegInit(Bool(), false.B)
  val valid_r3 = RegInit(Bool(), false.B)
  val last_r1 = RegInit(Bool(), false.B)
  val last_r2 = RegInit(Bool(), false.B)
  val last_r3 = RegInit(Bool(), false.B)

  val state_last = RegInit(Bool(), false.B)

  io.d.tready := !valid_r1

  when(io.d.tvalid && !valid_r1) {
    valid_r1 := true.B
    data_r1 := io.d.tdata
    last_r1 := io.d.tlast
  }.elsewhen(valid_r1 && !valid_r2 && io.q.tready) {
    valid_r1 := false.B
  }

  when(valid_r1 && !valid_r2 && io.q.tready) {
    valid_r2 := true.B
    data_r2 := data_r1
    last_r2 := last_r1
  }.elsewhen(valid_r2 && io.q.tready) {
    valid_r2 := false.B
  }

  when(!state_last && last_r1 && valid_r1 && io.q.tready) {
    state_last := true.B
    printf("to state last\n")
  }.elsewhen(state_last && io.q.tready) {
    state_last := false.B
    data_r1 := 0.U
    data_r2 := 0.U
    valid_r1 := false.B
    valid_r2 := false.B
  }

  when(!state_last) {
    io.q.tvalid := valid_r1
    io.q.tdata := Cat(data_r1(data_width - 1 - shift_by, 0),
                      data_r2(data_width - 1, data_width - 1))
    io.q.tlast := false.B
  }.otherwise {
    io.q.tvalid := true.B
    io.q.tdata := Cat(data_r1(data_width - 1 - shift_by, 0),
                      data_r2(data_width - 1, data_width - 1))
    io.q.tlast := true.B
  }

  io.q.tuser := 0.U
}
