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

class DataDriver(val data_w: Int) extends Module {
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
    val data_data = Output(UInt(data_w.W))
  })

  assert(data_w >= 128, "only the cases where data_w >= cntr width were tested")

  val mode_reg = RegNext(io.ctrl_mode)

  val mod_pattern_gen = Module(new PatternGen(data_w))
  mod_pattern_gen.io.data_init := io.data_init
  mod_pattern_gen.io.data_inc := io.data_inc

  io.data_data := 0.U // just to make Chisel (3.4) happy

  switch(mode_reg) {
    is(0.U) { io.data_data := 0.U }
    is(1.U) { io.data_data := -1.S(data_w.W).asUInt() }
    is(2.U) { io.data_data := mod_pattern_gen.io.out_walk1 }
    is(3.U) { io.data_data := mod_pattern_gen.io.out_walk0 }
    is(4.U) { io.data_data := mod_pattern_gen.io.out_alt }
    is(5.U) { io.data_data := mod_pattern_gen.io.out_8bit_cntr }
    is(6.U) { io.data_data := mod_pattern_gen.io.out_32bit_cntr }
    is(7.U) { io.data_data := mod_pattern_gen.io.out_128bit_cntr }
  }
}
