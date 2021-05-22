/*
Copyright (c) 2020-2021 Jan Marjanovic

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

package lamports_bakery_algorithm

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class LamportsBakeryAlgorithmSequencer extends Module {
  val io = IO(new Bundle {
    val nr_cycles = Input(UInt(32.W))
    val start = Input(Bool())
    val done = Output(Bool())
    val clear = Input(Bool())
    val last_cntr = Output(UInt(32.W))

    val lock_lock = Output(Bool())
    val lock_unlock = Output(Bool())
    val lock_locked = Input(Bool())
    val lock_unlocked = Input(Bool())

    val incr_start = Output(Bool())
    val incr_done = Input(Bool())

    val mux_sel = Output(Bool())
  })

  // output connections
  val lock_lock_reg = RegInit(false.B)
  val lock_unlock_reg = RegInit(false.B)
  io.lock_lock := lock_lock_reg
  io.lock_unlock := lock_unlock_reg

  val incr_start_reg = RegInit(false.B)
  io.incr_start := incr_start_reg

  val mux_sel_reg = RegInit(false.B)
  io.mux_sel := mux_sel_reg

  // counter
  val cntr = Reg(UInt(32.W))

  // FSM
  object State extends ChiselEnum {
    val Idle, Lock, Incr, Unlock, Done = Value
  }
  val state = RegInit(State.Idle)

  switch(state) {
    is(State.Idle) {
      when(io.start) {
        state := State.Lock
        lock_lock_reg := true.B
        cntr := 0.U
        mux_sel_reg := false.B
      }
    }
    is(State.Lock) {
      lock_lock_reg := false.B
      when(io.lock_locked) {
        state := State.Incr
        incr_start_reg := true.B
        mux_sel_reg := true.B
      }
    }
    is(State.Incr) {
      incr_start_reg := false.B
      when(io.incr_done) {
        state := State.Unlock
        lock_unlock_reg := true.B
        mux_sel_reg := false.B
      }
    }
    is(State.Unlock) {
      lock_unlock_reg := false.B
      when(io.lock_unlocked) {
        when(cntr < io.nr_cycles) {
          state := State.Lock
          lock_lock_reg := true.B
          cntr := cntr + 1.U
        }.otherwise {
          state := State.Done
        }
      }
    }
    is(State.Done) {
      when(io.clear) {
        state := State.Idle
      }
    }
  }

  io.last_cntr := cntr
  io.done := state === State.Done

}
