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

package mem_checker

import chisel3._
import chisel3.util._

class AvalonMMWriter(
    val addr_w: Int,
    val data_w: Int,
    val burst_w: Int,
    val BURST_LEN: Int
) extends Module {
  val io = IO(new Bundle {
    // Avalon interface
    val address = Output(UInt(addr_w.W))
    val byteenable = Output(UInt((data_w / 8).W))
    val response = Input(UInt(2.W))
    val write = Output(Bool())
    val writedata = Output(UInt(data_w.W))
    val waitrequest = Input(Bool())
    val writeresponsevalid = Input(Bool())
    val burstcount = Output(UInt(burst_w.W))

    // control interface
    val ctrl_addr = Input(UInt(addr_w.W))
    val ctrl_len_bytes = Input(UInt(32.W))
    val ctrl_start = Input(Bool())

    // data input
    val data_init = Output(Bool())
    val data_inc = Output(Bool())
    val data_data = Input(UInt(data_w.W))

    // statistics
    val stats_resp_cntr = Output(UInt(32.W))
    val stats_done = Output(Bool())
    val stats_duration = Output(UInt(32.W))
  })

  val BYTES_PER_CYC = data_w / 8
  var log2 = (x: Double) => math.log10(x) / math.log10(2.0)

  val cur_addr = Reg(io.ctrl_addr.cloneType)
  val rem_tot_len_bytes = Reg(io.ctrl_len_bytes.cloneType)
  val rem_cyc_len_words = Reg(UInt(burst_w.W))

  val sIdle :: sWrFirst :: sWrRest :: Nil = Enum(3)
  val state = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.ctrl_start) {
        cur_addr := io.ctrl_addr
        rem_tot_len_bytes := io.ctrl_len_bytes - BYTES_PER_CYC.U
        when(io.ctrl_len_bytes > (BURST_LEN * BYTES_PER_CYC).U) {
          rem_cyc_len_words := (BURST_LEN - 1).U
        }.otherwise {
          rem_cyc_len_words := (io.ctrl_len_bytes / BYTES_PER_CYC.U) - 1.U // div by a pow of 2
        }
        state := sWrFirst
      }
    }
    is(sWrFirst) {
      when(!io.waitrequest) {
        rem_cyc_len_words := rem_cyc_len_words - 1.U
        rem_tot_len_bytes := rem_tot_len_bytes - BYTES_PER_CYC.U
        when(rem_cyc_len_words === 0.U) {
          state := sIdle
        }.otherwise {
          state := sWrRest
        }
      }
    }
    is(sWrRest) {
      when(!io.waitrequest) {
        rem_cyc_len_words := rem_cyc_len_words - 1.U
        rem_tot_len_bytes := rem_tot_len_bytes - BYTES_PER_CYC.U
        when(rem_cyc_len_words === 0.U) {
          when(rem_tot_len_bytes === 0.U) {
            state := sIdle
          }.otherwise {
            state := sWrFirst
            cur_addr := cur_addr + (BYTES_PER_CYC * BURST_LEN).U
            when(rem_tot_len_bytes >= (BURST_LEN * BYTES_PER_CYC).U) {
              rem_cyc_len_words := (BURST_LEN - 1).U
            }.otherwise {
              rem_cyc_len_words := (rem_tot_len_bytes / BYTES_PER_CYC.U) - 1.U // div by a pow of 2
            }
          }
        }
      }
    }
  }

  io.byteenable := -1.S(io.byteenable.getWidth.W).asUInt()

  when(state === sWrFirst) {
    io.burstcount := rem_cyc_len_words + 1.U
  }.otherwise {
    io.burstcount := 0.U
  }

  when(state === sWrFirst) {
    io.address := cur_addr
  }.otherwise {
    io.address := 0.U
  }

  io.write := state =/= sIdle

  // data interface
  io.data_init := state === sIdle && io.ctrl_start
  io.data_inc := state =/= sIdle && !io.waitrequest
  io.writedata := io.data_data

  // response counter

  val resp_cntr_reg = Reg(UInt(32.W))
  io.stats_resp_cntr := resp_cntr_reg

  when(state === sIdle && io.ctrl_start) {
    resp_cntr_reg := 0.U
  }.elsewhen(state =/= sIdle) {
    when(io.writeresponsevalid && io.response === 0.U) {
      resp_cntr_reg := resp_cntr_reg + 1.U
    }
  }

  // statistics

  val state_prev = RegNext(state)
  io.stats_done := state_prev =/= sIdle && state === sIdle

  val wr_duration = RegInit(UInt(32.W), 0.U)
  val wr_duration_en = RegInit(Bool(), false.B)
  io.stats_duration := wr_duration

  when(state === sIdle && io.ctrl_start) {
    wr_duration_en := true.B
  }.elsewhen(io.stats_done) {
    wr_duration_en := false.B
  }

  when(wr_duration_en) {
    wr_duration := wr_duration + 1.U
  }.elsewhen(state === sIdle && io.ctrl_start) {
    wr_duration := 0.U
  }
}
