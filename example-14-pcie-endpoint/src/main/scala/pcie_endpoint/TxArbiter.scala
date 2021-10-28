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

class TxArbiter(val if_width: Int) extends Module {
  val io = IO(new Bundle {
    val tx_st = new Interfaces.AvalonStreamTx(if_width)

    val cpld = Flipped(new Interfaces.AvalonStreamTx(if_width))
    val bm = Flipped(new Interfaces.AvalonStreamTx(if_width))
    val bm_hint = Input(Bool())
  })

  object State extends ChiselEnum {
    val sIdle, sCpld, sBm = Value
  }

  val state = RegInit(State.sIdle)

  switch(state) {
    is(State.sIdle) {
      when(io.cpld.valid) {
        state := State.sCpld
      }.elsewhen(io.bm.valid) {
        state := State.sBm
      }
    }
    is(State.sCpld) {
      when(io.cpld.valid && io.cpld.eop && io.tx_st.ready) {
        state := State.sIdle
      }
    }
    is(State.sBm) {
      when(io.bm.valid && io.bm.eop && io.tx_st.ready) {
        when(io.cpld.valid) {
          state := State.sCpld
        }.elsewhen(io.bm_hint) {
            state := State.sBm
          }
          .otherwise {
            state := State.sIdle
          }
      }
    }
  }

  io.tx_st := DontCare
  io.cpld.ready := false.B
  io.bm.ready := false.B
  io.tx_st.valid := false.B

  switch(state) {
    is(State.sCpld) {
      io.tx_st <> io.cpld
    }
    is(State.sBm) {
      io.tx_st <> io.bm
    }
  }
}
