/*
Copyright (c) 2021 Jan Marjanovic

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

package mem_checker

import chisel3._
import chisel3.util._

class PatternGen(val data_w: Int) extends Module {
  val io = IO(new Bundle {
    val data_init = Input(Bool())
    val data_inc = Input(Bool())

    val out_walk1 = Output(UInt(data_w.W))
    val out_walk0 = Output(UInt(data_w.W))
    val out_alt = Output(UInt(data_w.W))
    val out_8bit_cntr = Output(UInt(data_w.W))
    val out_32bit_cntr = Output(UInt(data_w.W))
    val out_128bit_cntr = Output(UInt(data_w.W))
  })

  val data_reg = Reg(UInt(data_w.W))
  val data_reg_walk1 = Reg(UInt(data_w.W))
  val data_reg_walk0 = Reg(UInt(data_w.W))
  val data_reg_alt = Reg(UInt(data_w.W))
  val data_reg_8bit_cntr_vec = RegInit(VecInit(Seq.tabulate(data_w / 8)(_.U(8.W))))
  val data_reg_32bit_cntr_vec = RegInit(VecInit(Seq.tabulate(data_w / 32)(_.U(32.W))))
  val data_reg_128bit_cntr_vec = RegInit(VecInit(Seq.tabulate(data_w / 128)(_.U(128.W))))

  when(io.data_init) {
    data_reg_walk1 := 1.U
    data_reg_walk0 := -1.S(data_w.W).asUInt() ^ 1.U
    data_reg_alt := -1.S(data_w.W).asUInt()
    for (i <- 0 until data_w / 8) {
      data_reg_8bit_cntr_vec(i) := i.U
    }
    for (i <- 0 until data_w / 32) {
      data_reg_32bit_cntr_vec(i) := i.U
    }
    for (i <- 0 until data_w / 128) {
      data_reg_128bit_cntr_vec(i) := i.U
    }
  }.elsewhen(io.data_inc) {
    data_reg_walk1 := Cat(data_reg_walk1(data_w - 2, 0), data_reg_walk1(data_w - 1))
    data_reg_walk0 := Cat(data_reg_walk0(data_w - 2, 0), data_reg_walk0(data_w - 1))
    data_reg_alt := data_reg_alt ^ -1.S(data_w.W).asUInt()
    for (i <- 0 until data_w / 8) {
      data_reg_8bit_cntr_vec(i) := data_reg_8bit_cntr_vec(i) + (data_w / 8).U
    }
    for (i <- 0 until data_w / 32) {
      data_reg_32bit_cntr_vec(i) := data_reg_32bit_cntr_vec(i) + (data_w / 32).U
    }
    for (i <- 0 until data_w / 128) {
      data_reg_128bit_cntr_vec(i) := data_reg_128bit_cntr_vec(i) + (data_w / 128).U
    }
  }

  io.out_walk1 := data_reg_walk1
  io.out_walk0 := data_reg_walk0
  io.out_alt := data_reg_alt
  io.out_8bit_cntr := data_reg_8bit_cntr_vec.asUInt()
  io.out_32bit_cntr := data_reg_32bit_cntr_vec.asUInt()
  io.out_128bit_cntr := data_reg_128bit_cntr_vec.asUInt()

}
