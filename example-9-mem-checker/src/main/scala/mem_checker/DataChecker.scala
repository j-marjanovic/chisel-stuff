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

class DataChecker(val data_w: Int) extends Module {
  val io = IO(new Bundle {
    // control interface
    //   mode:
    //     0 - all 0s
    //     1 - all 1s
    //     2 - walking 1
    //     3 - walking 0
    //     4 - alternate
    //     5 - 8-bit counter
    //     6 - 32-bit counter
    //     7 - 128-bit counter
    val ctrl_mode = Input(UInt(4.W))

    // data input
    val data_init = Input(Bool())
    val data_inc = Input(Bool())
    val data_data = Input(UInt(data_w.W))

    val check_tot = Output(UInt(32.W))
    val check_ok = Output(UInt(32.W))
  })

  assert(data_w >= 128, "only the cases where data_w >= cntr width were tested")

  val mode_reg = RegNext(io.ctrl_mode)

  val data_init: Int => BigInt = (cntr_w: Int) =>
    ((data_w / cntr_w) - 1 to 0 by -1).foldLeft(BigInt(0)) { (acc, i) => (acc << cntr_w) | i }
  val data_inc: Int => BigInt = (cntr_w: Int) =>
    ((data_w / 8) - 1 to 0 by -1).foldLeft(BigInt(0)) { (acc, i) =>
      (acc << cntr_w) | (data_w / cntr_w)
    }

  val data_reg = Reg(UInt(data_w.W))

  when(io.data_init) {
    switch(mode_reg) {
      is(0.U) { data_reg := 0.U }
      is(1.U) { data_reg := -1.S(data_w.W).asUInt() }
      is(2.U) { data_reg := 1.U }
      is(3.U) { data_reg := -1.S(data_w.W).asUInt() ^ 1.U }
      is(4.U) { data_reg := -1.S(data_w.W).asUInt() }
      is(5.U) { data_reg := data_init(8).U }
      is(6.U) { data_reg := data_init(32).U }
      is(7.U) { data_reg := data_init(128).U }
    }
  }.elsewhen(io.data_inc) {
    switch(mode_reg) {
      is(0.U) {}
      is(1.U) {}
      is(2.U) { data_reg := Cat(data_reg(data_w-2, 0), data_reg(data_w-1))  }
      is(3.U) { data_reg := Cat(data_reg(data_w-2, 0), data_reg(data_w-1))  }
      is(4.U) { data_reg := data_reg ^ -1.S(data_w.W).asUInt()}
      is(5.U) { data_reg := data_reg + data_inc(8).U }
      is(6.U) { data_reg := data_reg + data_inc(32).U }
      is(7.U) { data_reg := data_reg + data_inc(128).U }
    }
  }

  // stats counter
  val check_tot_reg = Reg(UInt(32.W))
  val check_ok_reg = Reg(UInt(32.W))
  io.check_tot := check_tot_reg
  io.check_ok := check_ok_reg

  when (io.data_init) {
    check_tot_reg := 0.U
    check_ok_reg := 0.U
  } .elsewhen (io.data_inc) {
    check_tot_reg := check_tot_reg + 1.U
    when (data_reg === io.data_data) {
      check_ok_reg := check_ok_reg + 1.U
    }
  }
}
