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

package gunzip

import chisel3._
import chisel3.util._

class ByteStreamToBitStream extends Module {
  val io = IO(new Bundle {
    val data_in = DeqIO(UInt(8.W))
    val data_out = EnqIO(UInt(8.W))
  })

  val sel = RegInit(0.U(4.W))

  io.data_in.ready := sel === 0.U
  io.data_out.valid := sel =/= 0.U

  when (sel =/= 0.U && io.data_out.ready) {
    when (sel === 8.U) {
      sel := 0.U
    } .otherwise {
      sel := sel + 1.U
    }
  } .elsewhen (sel === 0.U && io.data_in.valid) {
    sel := 1.U
  }

  val data_reg = Reg(UInt())

  when (sel === 0.U && io.data_in.valid) {
    data_reg := io.data_in.bits
  }

  io.data_out.bits := data_reg(sel - 1.U)

}
