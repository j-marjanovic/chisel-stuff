package lamports_bakery_algorithm
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

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class DelayGenLfsr(val cntr_w: Int) extends Module {
  val io = IO(new Bundle {
    val load = Input(Bool())
    val d = Input(UInt(16.W))

    val start = Input(Bool())
    val done = Output(Bool())
  })

  // LFSR
  val TAPS = Set(16, 14, 13, 11)

  val q = Reg(UInt(16.W))
  val q_out = Reg(Vec(cntr_w, Bool()))
  val q_next = (0 until cntr_w).foldRight(q)((i: Int, q_i: UInt) => {
    val next_bit = TAPS.map(i => q_i(i - 1)).reduce(_ ^ _).asBool()
    q_out(i) := next_bit
    Cat(q_i(14, 0), next_bit)
  })

  val inc = WireInit(false.B)

  when(io.load) {
    q := io.d
  }.elsewhen(inc) {
    q := q_next
  }

  // counter
  val cntr_max = Reg(UInt(cntr_w.W))
  val cntr_cur = Reg(UInt(cntr_w.W))

  // FSM
  object State extends ChiselEnum {
    val Idle, Wait, Done = Value
  }

  val state = RegInit(State.Idle)

  switch(state) {
    is(State.Idle) {
      when(io.start) {
        cntr_cur := 0.U
        cntr_max := q_out.asUInt()
        state := State.Wait
      }
    }
    is(State.Wait) {
      cntr_cur := cntr_cur + 1.U
      when(cntr_cur >= cntr_max) {
        state := State.Done
      }
    }
    is(State.Done) {
      state := State.Idle
    }
  }

  inc := state === State.Done
  io.done := state === State.Done

}
