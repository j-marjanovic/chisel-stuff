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

class GunzipOutBuf extends Module {
  val io = IO(new Bundle {
    val data_in = Input(UInt(8.W))
    val valid_in = Input(Bool())

    val dist = Input(UInt(15.W))
    val len = Input(UInt(15.W))
    val enable = Input(Bool())
    val done = Output(Bool())

    val data_out_bits = Output(UInt(8.W))
    val data_out_valid = Output(Bool())
    val data_out_ready = Input(Bool())
  })

  val out_buf = SyncReadMem(32768, UInt(8.W)) // TODO: check size
  val out_buf_wr_pos = RegInit(0.U(15.W))
  val out_buf_rd_pos = RegInit(0.U(15.W))
  val rem_len = Reg(UInt(15.W))

  // store output data
  when (io.valid_in) {
    out_buf.write(out_buf_wr_pos, io.data_in)
    out_buf_wr_pos := out_buf_wr_pos + 1.U
  }

  val sIdle :: sLoad :: sRead :: sStore :: Nil = Enum(4)
  val state = RegInit(sIdle)

  switch (state) {
    is (sIdle) {
      when (io.enable) {
        state := sLoad
      }
    }
    is (sLoad) {
      state := sRead
    }
    is (sRead) {
      when (io.data_out_ready) {
        state := sStore
      }
    }
    is (sStore) {
      when (io.done) {
        state := sIdle
      } .otherwise {
        state := sRead
      }
    }
  }

  when (state === sIdle && io.enable) {
    out_buf_rd_pos := out_buf_wr_pos - io.dist
    rem_len := io.len
  } .elsewhen (state === sRead && io.data_out_ready) {
    out_buf_rd_pos := out_buf_rd_pos + 1.U
    rem_len := rem_len - 1.U
  }

  io.data_out_bits := out_buf.read(out_buf_rd_pos, true.B)
  io.data_out_valid := state === sRead

  io.done := state === sStore && rem_len === 0.U
}
