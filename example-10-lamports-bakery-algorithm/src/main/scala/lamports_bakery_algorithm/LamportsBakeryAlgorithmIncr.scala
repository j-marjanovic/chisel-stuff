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
import chisel3.experimental._
import chisel3.util._

class LamportsBakeryAlgorithmIncr(val addr_w: Int = 49, val data_w: Int = 128) extends Module {
  val io = IO(new Bundle {
    val addr_cntr = Input(UInt(addr_w.W))

    val start = Input(Bool())
    val done = Output(Bool())

    val rd_cmd = Flipped(new Axi4LiteManagerRdCmd(addr_w, data_w))
    val wr_cmd = Flipped(new Axi4LiteManagerWrCmd(addr_w, data_w))

    val dbg_last_data = Output(UInt(32.W))
  })

  // wr command
  val wr_cmd_addr = Reg(UInt(addr_w.W))
  val wr_cmd_data = Reg(UInt(data_w.W))
  val wr_cmd_valid = Reg(Bool())
  io.wr_cmd.addr := wr_cmd_addr
  io.wr_cmd.data := wr_cmd_data
  io.wr_cmd.valid := wr_cmd_valid

  // rd command
  val rd_cmd_addr = Reg(UInt(addr_w.W))
  val rd_cmd_valid = Reg(Bool())
  io.rd_cmd.addr := rd_cmd_addr
  io.rd_cmd.valid := rd_cmd_valid

  // data
  val last_data = Reg(UInt(32.W))
  io.dbg_last_data := last_data

  // FSM
  object State extends ChiselEnum {
    val Idle, Read, Write = Value
  }

  val state = RegInit(State.Idle)

  // done
  val done_reg = Reg(Bool())
  io.done := done_reg
  done_reg := false.B

  switch(state) {
    is(State.Idle) {
      when(io.start) {
        state := State.Read
        rd_cmd_valid := true.B
        rd_cmd_addr := io.addr_cntr
      }
    }
    is(State.Read) {
      rd_cmd_valid := false.B
      when(io.rd_cmd.done) {
        state := State.Write
        wr_cmd_addr := io.addr_cntr
        wr_cmd_data := io.rd_cmd.data + 1.U
        last_data := io.rd_cmd.data + 1.U
        wr_cmd_valid := true.B
      }
    }
    is(State.Write) {
      wr_cmd_valid := false.B
      when(io.wr_cmd.done) {
        done_reg := true.B
        state := State.Idle
      }
    }
  }

}
