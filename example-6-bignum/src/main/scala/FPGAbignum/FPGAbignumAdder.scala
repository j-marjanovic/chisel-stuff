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
import bfmtester._

/**
  *
  * limitations:
  *   - data must be at least 2 elements long (i.e. TLAST should not be
  *     asserted in the first cycle)
  *   - both inputs shall be of equal lengths
  */

class FPGAbignumAdder(val data_width: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = new AxiStreamIf(data_width.W)
    val b = new AxiStreamIf(data_width.W)
    val q = Flipped(new AxiStreamIf(data_width.W))
  })

  val reg_a_vld = RegInit(Bool(), false.B)
  val reg_b_vld = RegInit(Bool(), false.B)
  val reg_a_data = Reg(UInt(data_width.W))
  val reg_b_data = Reg(UInt(data_width.W))
  val reg_a_last = RegInit(Bool(), false.B)
  val reg_b_last = RegInit(Bool(), false.B)
  val stage2_rdy = Wire(Bool())
  val axis_add_out = Wire(new AxiStreamIf(data_width.W))

  io.a.tready := !reg_a_vld || stage2_rdy
  io.b.tready := !reg_b_vld || stage2_rdy

  reg_a_vld := (!io.a.tvalid && !stage2_rdy && reg_a_vld) || io.a.tvalid
  reg_b_vld := (!io.b.tvalid && !stage2_rdy && reg_b_vld) || io.b.tvalid

  when (io.a.tvalid && io.a.tready) {
    reg_a_data := io.a.tdata
  }
  when (io.a.tvalid && io.a.tready) {
    reg_a_last := io.a.tlast
  }
  when (io.b.tvalid && io.b.tready) {
    reg_b_data := io.b.tdata
  }
  when (io.b.tvalid && io.b.tready) {
    reg_b_last := io.b.tlast
  }

  // stage 2
  val reg_ab_vld = WireInit(Bool(), reg_a_vld && reg_b_vld)
  val stage2_vld = RegInit(Bool(), false.B)
  val stage2_data = RegInit(UInt((data_width + 2).W), 0.U)
  val stage2_last = RegInit(Bool(), false.B)
  val carry = Wire(UInt(2.W))
  carry := Cat(0.S(1.W), stage2_data(data_width).asSInt())

  stage2_rdy := !stage2_vld || axis_add_out.tready
  stage2_vld := (!reg_ab_vld && !axis_add_out.tready && stage2_vld) || reg_ab_vld

  when (reg_ab_vld && stage2_rdy) {
    stage2_data := (reg_a_data.asUInt() +& reg_b_data.asUInt()) + carry
    stage2_last := reg_a_last && reg_b_last

    when (axis_add_out.tlast) {
      stage2_data := 0.U
      stage2_last := false.B
    }
  } .elsewhen (axis_add_out.tlast && axis_add_out.tready && axis_add_out.tvalid) {
    stage2_data := 0.U
    stage2_last := false.B
  }

  axis_add_out.tdata := stage2_data
  axis_add_out.tvalid := stage2_vld
  axis_add_out.tlast := stage2_last
  axis_add_out.tuser := 0.U

  val m_axis_reg = Module(new AxiStreamRegSlice(data_width))
  m_axis_reg.io.inp <> axis_add_out
  m_axis_reg.io.out <> io.q
}
