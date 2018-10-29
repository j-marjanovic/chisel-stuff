/*
MIT License

Copyright (c) 2018 Jan Marjanovic

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

package ord_decoup_io

import chisel3._
import chisel3.util._

class OrdDecoupIo extends Module {
  val io = IO(new Bundle {
    val data_in = DeqIO(UInt(8.W))
    val data_out = EnqIO(UInt(8.W))
  })

  val sRst :: sIdle :: sTx0 :: sTx1 :: Nil = Enum(4)
  val state = RegInit(sRst)

  val in_s = RegEnable(io.data_in.bits, state === sIdle && io.data_in.valid)
  when (state === sIdle && io.data_in.valid) {
    printf(p"[OrdDecoupIo] received data: 0x${Hexadecimal(io.data_in.bits)}\n")
  }

  switch (state) {
    is (sRst) {
      state := sIdle
    }
    is (sIdle) {
      when (io.data_in.valid) { state := sTx0 }
    }
    is (sTx0) {
      when (io.data_out.ready) { state := sTx1 }
    }
    is (sTx1) {
      when (io.data_out.ready) { state := sIdle}
    }
  }

  io.data_in.ready := false.B
  io.data_out.valid := false.B
  io.data_out.bits := 0.U

  switch (state) {
    is (sIdle) {
      io.data_in.ready := true.B
      io.data_out.valid := false.B
      io.data_out.bits := 0.U
    }
    is (sTx0) {
      io.data_in.ready := false.B
      io.data_out.valid := true.B
      io.data_out.bits := in_s + 1.U
    }
    is (sTx1) {
      io.data_in.ready := false.B
      io.data_out.valid := true.B
      io.data_out.bits := in_s + 2.U
    }
  }

}
