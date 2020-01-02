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

class AxiStreamRegSlice(val data_width: Int) extends Module {
  val io = IO(new Bundle {
    val inp = new AxiStreamIf(data_width.W)
    val out = Flipped(new AxiStreamIf(data_width.W))
  })

  val sReady :: sDrive :: Nil = Enum(2)
  val state = RegInit(sReady)

  val inp_r = RegEnable(io.inp, state === sReady)

  switch(state) {
    is(sReady) {
      when(io.inp.tvalid) {
        state := sDrive
      }
    }
    is(sDrive) {
      when(io.out.tready) {
        state := sReady
      }
    }
  }

  io.inp.tready := state === sReady

  io.out.tvalid := state === sDrive
  io.out.tdata := inp_r.tdata
  io.out.tlast := inp_r.tlast
  io.out.tuser := inp_r.tuser

}
