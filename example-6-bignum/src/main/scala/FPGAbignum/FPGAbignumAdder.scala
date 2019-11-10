/*
MIT License

Copyright (c) 2018-2019 Jan Marjanovic

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

import chisel3._
import chisel3.util._

/**
  *
  * limitations:
  *   - data must be at least 2 elements long (i.e. TLAST should not be
  *     asserted in the first cycle)
  */

class FPGAbignumAdder(val data_width: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = new AxiStreamIf(data_width)
    val b = new AxiStreamIf(data_width)
    val q = Flipped(new AxiStreamIf(data_width))
  })

  val reg_a = Reg(SInt(data_width.W))
  val reg_b = Reg(SInt(data_width.W))
  val reg_a_vld = Reg(Bool())
  val reg_b_vld = Reg(Bool())
  val clear_input = Wire(Bool())

  val reg_a_last = Reg(Bool())
  val reg_b_last = Reg(Bool())
  val clear_last = Wire(Bool())

  when (reg_a_vld) {
    io.a.tready := false.B

    when (clear_input) {
      reg_a_vld := false.B
    }
  } .otherwise {
    io.a.tready := true.B

    when(io.a.tvalid) {
      reg_a_vld := true.B
      reg_a := io.a.tdata.asSInt()
    }
  }

  when (reg_b_vld) {
    io.b.tready := false.B

    when (clear_input) {
      reg_b_vld := false.B
    }
  } .otherwise {
    io.b.tready := true.B

    when(io.a.tvalid) {
      reg_b_vld := true.B
      reg_b := io.b.tdata.asSInt()
    }
  }

  clear_input := reg_a_vld && reg_b_vld && io.q.tready

  when (reg_a_vld) {
    when (clear_last) {
      reg_a_last := false.B
    }
  } .otherwise {
    when(io.a.tvalid && !reg_a_last) {
      reg_a_last := io.a.tlast
    }
  }

  when (reg_b_vld) {
    when (clear_last) {
      reg_b_last := false.B
    }
  } .otherwise {
    when(io.b.tvalid && !reg_b_last) {
      reg_b_last := io.b.tlast
    }
  }

  clear_last := clear_input && io.q.tlast

  val out = Reg(UInt((data_width + 2).W))
  val carry = Wire(UInt(2.W))
  val valid = RegInit(Bool(), false.B)
  val last = RegInit(Bool(), false.B)

  carry := Cat(0.S(1.W), out(data_width).asSInt())

  when (reg_a_vld && reg_b_vld) {
    printf("carry = %x\n", carry)
    out := (reg_a.asUInt() +& reg_b.asUInt()) + carry
    valid := true.B
  } .elsewhen (valid && io.q.tready) {
    valid := false.B
    when (io.q.tlast) {
      out := 0.U
    }
  }

  when (clear_last) {
    last := false.B
  } .otherwise {
    last := reg_a_last && reg_b_last
  }

  io.q.tdata := out(data_width-1, 0).asSInt().asUInt()
  io.q.tvalid := valid
  io.q.tlast := last
  io.q.tuser := 0.U

}
