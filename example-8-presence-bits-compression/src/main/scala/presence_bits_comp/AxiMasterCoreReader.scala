/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import chisel3._
import chisel3.util._

class AxiMasterCoreReader(val addr_w: Int, val data_w: Int, val id_w: Int) extends Module {
  val io = IO(new Bundle {
    val araddr = Output(UInt(addr_w.W))
    val arlen = Output(UInt(8.W))
    val arvalid = Output(Bool())
    val arready = Input(Bool())

    val rid = Input(UInt(id_w.W))
    val rdata = Input(UInt(data_w.W))
    val rresp = Input(UInt(3.W))
    val rlast = Input(Bool())
    val rvalid = Input(Bool())
    val rready = Output(Bool())

    val read_addr = Input(UInt(addr_w.W))
    val read_len = Input(UInt(32.W))
    val read_start = Input(Bool())
    val out_data = Output(UInt(data_w.W))
    val out_valid = Output(Bool())

    val cntr_resp_okay = Output(UInt(32.W))
    val cntr_resp_exokay = Output(UInt(32.W))
    val cntr_resp_slverr = Output(UInt(32.W))
    val cntr_resp_decerr = Output(UInt(32.W))
  })

  val BURST_LEN: Int = 32

  // registers
  val read_addr = Reg(UInt(addr_w.W))
  val read_len = Reg(UInt(32.W))
  val arlen_reg = Reg(UInt(8.W))
  io.araddr := read_addr
  io.arlen := arlen_reg

  // state machine
  val sIdle :: sDriveAddr :: sWaitRead :: Nil = Enum(3)
  val state = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.read_start) {
        read_addr := io.read_addr
        read_len := io.read_len
        when(io.read_len > BURST_LEN.U) {
          arlen_reg := BURST_LEN.U - 1.U
        }.otherwise {
          arlen_reg := io.read_len - 1.U
        }
        state := sDriveAddr
      }
    }
    is(sDriveAddr) {
      when(io.arready) {
        state := sWaitRead
      }
    }
    is(sWaitRead) {
      when(io.rready && io.rvalid && io.rlast) {
        when(read_len <= BURST_LEN.U) {
          state := sIdle
        }.otherwise {
          read_addr := read_addr + (BURST_LEN * data_w / 8).U
          read_len := read_len - BURST_LEN.U
          // arlen_reg := read_len-BURST_LEN.U
          when(read_len > BURST_LEN.U * 2.U) {
            arlen_reg := BURST_LEN.U - 1.U
          }.otherwise {
            arlen_reg := read_len - BURST_LEN.U - 1.U
          }
          state := sDriveAddr
        }
      }
    }
  }

  // outputs
  io.arvalid := false.B
  switch(state) {
    is(sIdle) {
      io.arvalid := false.B
    }
    is(sDriveAddr) {
      io.arvalid := true.B
    }
    is(sWaitRead) {
      io.arvalid := false.B
    }
  }

  io.rready := true.B
  io.out_data := io.rdata
  io.out_valid := io.rready && io.rvalid

  // resp counter
  val mod_resp_cntr = Module(new AxiMasterCoreRespCounter)
  mod_resp_cntr.io.resp := io.rresp
  mod_resp_cntr.io.enable := io.rready && io.rvalid && io.rlast
  io.cntr_resp_okay := mod_resp_cntr.io.cntr_resp_okay
  io.cntr_resp_exokay := mod_resp_cntr.io.cntr_resp_exokay
  io.cntr_resp_slverr := mod_resp_cntr.io.cntr_resp_slverr
  io.cntr_resp_decerr := mod_resp_cntr.io.cntr_resp_decerr
}
