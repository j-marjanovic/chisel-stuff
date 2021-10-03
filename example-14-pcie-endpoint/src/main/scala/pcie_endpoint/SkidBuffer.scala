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

package pcie_endpoint

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class SkidBuffer[T <: Data](gen: T) extends MultiIOModule {
  val inp = IO(Flipped(Decoupled(gen)))
  val out = IO(Decoupled(gen))

  val reg_store_en = Wire(Bool())
  val reg_store = RegEnable(inp.bits, 0.U, reg_store_en)

  val sel_store = Wire(Bool())
  val mux_out = Mux(sel_store, reg_store, inp.bits)

  val reg_out_en = Wire(Bool())
  val reg_out = RegEnable(mux_out, 0.U, reg_out_en)
  out.bits := reg_out

  val enq = WireInit(inp.ready && inp.valid)
  val deq = WireInit(out.ready && out.valid)

  object State extends ChiselEnum {
    val sEmpty, sRun, sFull = Value
  }
  val state = RegInit(State.sEmpty)

  switch(state) {
    is(State.sEmpty) {
      when(enq) {
        state := State.sRun
      }
    }
    is(State.sRun) {
      when(enq && !deq) {
        state := State.sFull
      }.elsewhen(!enq && deq) {
        state := State.sEmpty
      }
    }
    is(State.sFull) {
      when(deq) {
        state := State.sRun
      }
    }
  }

  out.valid := DontCare
  inp.ready := DontCare

  switch(state) {
    is(State.sEmpty) {
      out.valid := false.B
      inp.ready := true.B
    }
    is(State.sRun) {
      out.valid := true.B
      inp.ready := true.B
    }
    is(State.sFull) {
      out.valid := true.B
      inp.ready := false.B
    }
  }

  reg_store_en := DontCare
  reg_out_en := DontCare

  switch(state) {
    is(State.sEmpty) {
      reg_store_en := false.B
      reg_out_en := enq
    }
    is(State.sRun) {
      reg_store_en := enq
      reg_out_en := enq && deq
    }
    is(State.sFull) {
      reg_store_en := false.B
      reg_out_en := deq
    }
  }

  sel_store := state === State.sFull

}
