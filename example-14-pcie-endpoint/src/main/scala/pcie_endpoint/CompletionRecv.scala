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
import chisel3.util._

class CompletionRecv extends Module {
  val io = IO(new Bundle {
    val data = Input(UInt(256.W))
    val valid = Input(Bool())
    val sop = Input(Bool())

    val mrd_in_flight_dec = Valid(UInt(10.W))

    val data_out = new Interfaces.AvalonStreamDataOut
  })

  val data_prev = RegEnable(io.data, io.valid)

  val reg_length_dw = RegInit(0.U(10.W))

  when(io.valid) {
    when(io.sop) {
      reg_length_dw := io.data(9, 0)
    }.otherwise {
      reg_length_dw := reg_length_dw - (256 / 32).U
    }
  }

  io.mrd_in_flight_dec.valid := io.valid && io.sop
  io.mrd_in_flight_dec.bits := io.data(9, 0)

  io.data_out.data := Cat(io.data(127, 0), data_prev(255, 128))
  io.data_out.valid := RegNext(io.valid)
  io.data_out.empty := Mux(reg_length_dw > (256 / 32).U, 0.U, (256 / 8).U - reg_length_dw * 4.U)
}
