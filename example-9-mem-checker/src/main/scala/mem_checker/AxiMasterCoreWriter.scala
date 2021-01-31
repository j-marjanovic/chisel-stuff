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

package mem_checker

import chisel3._
import chisel3.util._

class AxiMasterCoreWriter(val addr_w: Int, val data_w: Int, val id_w: Int) extends Module {
  val io = IO(new Bundle {
    val awaddr = Output(UInt(addr_w.W))
    val awlen = Output(UInt(8.W))
    val awvalid = Output(Bool())
    val awready = Input(Bool())

    val wdata = Output(UInt(data_w.W))
    val wstrb = Output(UInt((data_w / 8).W))
    val wlast = Output(Bool())
    val wvalid = Output(Bool())
    val wready = Input(Bool())

    val bresp = Input(UInt(2.W))
    val bvalid = Input(Bool())
    val bready = Output(Bool())

    val write_addr = Input(UInt(addr_w.W))
    val write_len = Input(UInt(32.W))
    val write_start = Input(Bool())
    val in_data = Input(UInt(data_w.W))
    val in_valid = Input(Bool())
    val in_ready = Output(Bool())

    val cntr_resp_okay = Output(UInt(32.W))
    val cntr_resp_exokay = Output(UInt(32.W))
    val cntr_resp_slverr = Output(UInt(32.W))
    val cntr_resp_decerr = Output(UInt(32.W))
  })

  val BURST_LEN: Int = 32

  // registers
  val write_addr = Reg(UInt(addr_w.W))
  val write_len_tot = Reg(UInt(32.W))
  val write_len_burst = Reg((UInt(8.W)))
  val awlen_reg = Reg(UInt(8.W))
  io.awaddr := write_addr
  io.awlen := awlen_reg

  // state machine
  val sIdle :: sDriveAddr :: sWaitWrite :: Nil = Enum(3)
  val state = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.write_start) {
        write_addr := io.write_addr
        write_len_tot := io.write_len
        when(io.write_len > BURST_LEN.U) {
          awlen_reg := BURST_LEN.U - 1.U
          write_len_burst := BURST_LEN.U
        }.otherwise {
          awlen_reg := io.write_len - 1.U
          write_len_burst := io.write_len
        }
        state := sDriveAddr
      }
    }
    is(sDriveAddr) {
      when(io.awready) {
        state := sWaitWrite
      }
    }
    is(sWaitWrite) {
      when(io.wready && io.wvalid) {
        write_len_burst := write_len_burst - 1.U
      }
      when((write_len_burst === 1.U) && io.wready && io.wvalid) {
        when(write_len_tot <= BURST_LEN.U) {
          state := sIdle
        }.otherwise {
          write_addr := write_addr + (BURST_LEN * data_w / 8).U
          write_len_tot := write_len_tot - BURST_LEN.U
          when(write_len_tot > BURST_LEN.U * 2.U) {
            awlen_reg := BURST_LEN.U - 1.U
            write_len_burst := BURST_LEN.U
          }.otherwise {
            awlen_reg := write_len_tot - BURST_LEN.U - 1.U
            write_len_burst := write_len_tot - BURST_LEN.U
          }
          state := sDriveAddr
        }
      }
    }
  }

  // outputs
  io.awvalid := state === sDriveAddr

  // write resp
  io.bready := true.B

  // resp counter
  val mod_resp_cntr = Module(new AxiMasterCoreRespCounter)
  mod_resp_cntr.io.resp := io.bresp
  mod_resp_cntr.io.enable := io.bready && io.bvalid
  io.cntr_resp_okay := mod_resp_cntr.io.cntr_resp_okay
  io.cntr_resp_exokay := mod_resp_cntr.io.cntr_resp_exokay
  io.cntr_resp_slverr := mod_resp_cntr.io.cntr_resp_slverr
  io.cntr_resp_decerr := mod_resp_cntr.io.cntr_resp_decerr

  // data
  io.wdata := io.in_data
  io.wstrb := 0x7fffffff.U
  io.wlast := write_len_burst === 1.U
  io.wvalid := Mux(state === sWaitWrite, io.in_valid, false.B)
  io.in_ready := Mux(state === sWaitWrite, io.wready, false.B)

}
