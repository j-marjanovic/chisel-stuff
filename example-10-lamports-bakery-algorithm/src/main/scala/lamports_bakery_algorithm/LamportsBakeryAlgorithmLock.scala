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

class LamportsBakeryAlgorithmLock(val addr_w: Int = 49, val data_w: Int = 128) extends Module {
  val io = IO(new Bundle {
    val addr_choosing = Input(UInt(addr_w.W))
    val addr_number = Input(UInt(addr_w.W))
    val idx_inst = Input(UInt(4.W))
    val idx_max = Input(UInt(4.W)) //

    val lock = Input(Bool())
    val unlock = Input(Bool())
    val locked = Output(Bool())
    val unlocked = Output(Bool())

    val rd_cmd = Flipped(new Axi4LiteManagerRdCmd(addr_w, data_w))
    val wr_cmd = Flipped(new Axi4LiteManagerWrCmd(addr_w, data_w))
  })

  object State extends ChiselEnum {
    val Idle, LockWrEntering1, LockRdNumber, LockWrNumber, LockWrEntering0, LockLoopEntering,
        LockLoopNumber, Locked, Unlock = Value
  }

  def _comp_threads(nr_a: UInt, i_a: UInt, nr_b: UInt, i_b: UInt): Bool = {
    (nr_a < nr_b) || ((nr_a === nr_b) && (i_a < i_b))
  }

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

  // max
  val max_cur_pos = Reg(UInt(4.W))
  val max_cur_val = Reg(UInt(32.W))

  // loop
  val loop_idx = Reg(UInt(4.W))

  // state machine
  val state = RegInit(State.Idle)
  switch(state) {
    is(State.Idle) {
      when(io.lock) {
        state := State.LockWrEntering1
        wr_cmd_valid := true.B
        wr_cmd_addr := io.addr_choosing + io.idx_inst * 4.U
        wr_cmd_data := 1.U
      }
    }
    is(State.LockWrEntering1) {
      wr_cmd_valid := false.B
      wr_cmd_addr := 0.U
      wr_cmd_data := 0.U
      when(io.wr_cmd.done) {
        state := State.LockRdNumber
        max_cur_pos := 0.U
        max_cur_val := 1.U
        rd_cmd_valid := true.B
        rd_cmd_addr := io.addr_number
      }
    }
    is(State.LockRdNumber) {
      rd_cmd_valid := false.B
      rd_cmd_addr := 0.U
      when(io.rd_cmd.done) {
        // handle max
        when(io.rd_cmd.data > max_cur_val) {
          max_cur_val := io.rd_cmd.data + 1.U
        }
        // next or end
        when(max_cur_pos < io.idx_max) {
          rd_cmd_valid := true.B
          rd_cmd_addr := io.addr_number + (max_cur_pos + 1.U) * 4.U
          max_cur_pos := max_cur_pos + 1.U
        }.otherwise {
          state := State.LockWrNumber
          wr_cmd_valid := true.B
          wr_cmd_addr := io.addr_number + io.idx_inst * 4.U
          wr_cmd_data := Mux(io.rd_cmd.data > max_cur_val, io.rd_cmd.data + 1.U, max_cur_val)
        }
      }
    }
    is(State.LockWrNumber) {
      wr_cmd_valid := false.B
      wr_cmd_addr := 0.U
      wr_cmd_data := 0.U
      when(io.wr_cmd.done) {
        state := State.LockWrEntering0
        wr_cmd_valid := true.B
        wr_cmd_addr := io.addr_choosing + io.idx_inst * 4.U
        wr_cmd_data := 1.U
      }
    }
    is(State.LockWrEntering0) {
      wr_cmd_valid := false.B
      wr_cmd_addr := 0.U
      wr_cmd_data := 0.U
      when(io.wr_cmd.done) {
        state := State.LockLoopEntering
        loop_idx := 0.U
        rd_cmd_valid := true.B
        rd_cmd_addr := io.addr_choosing
      }
    }
    is(State.LockLoopEntering) {
      rd_cmd_valid := false.B
      rd_cmd_addr := 0.U
      when(io.rd_cmd.done) {
        when(io.rd_cmd.data =/= 0.U) {
          rd_cmd_valid := true.B
          rd_cmd_addr := io.addr_choosing + loop_idx * 4.U
        }.otherwise {
          state := State.LockLoopNumber
          rd_cmd_valid := true.B
          rd_cmd_addr := io.addr_number + loop_idx * 4.U
        }
      }
    }
    is(State.LockLoopNumber) {
      rd_cmd_valid := false.B
      rd_cmd_addr := 0.U
      when(io.rd_cmd.done) {
        when(
          io.rd_cmd.data =/= 0.U && _comp_threads(
            io.rd_cmd.data,
            loop_idx,
            max_cur_val,
            io.idx_inst
          )
        ) {
          rd_cmd_valid := true.B
          rd_cmd_addr := io.addr_number + loop_idx * 4.U
        }.otherwise {
          state := State.Locked
        }
      }
    }
    is(State.Locked) {
      when(io.unlock) {
        state := State.Unlock
        wr_cmd_valid := true.B
        wr_cmd_addr := io.addr_number + io.idx_inst * 4.U
        wr_cmd_data := 0.U
      }
    }
    is(State.Unlock) {
      wr_cmd_valid := false.B
      wr_cmd_addr := 0.U
      wr_cmd_data := 0.U
      when(io.wr_cmd.done) {
        state := State.Idle
      }
    }
  }

  io.locked := state === State.Locked
  io.unlocked := state === State.Idle
}
