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
import chisel3.experimental.ChiselEnum
import chisel3.util._
import bfmtester.AvalonMMIf

class AvalonAgent extends Module {
  val io = IO(new Bundle {
    val mem_cmd = Flipped(new Interfaces.MemoryCmd)
    val mem_resp = new Interfaces.MemoryResp
    val avmm = new AvalonMMIf(32, 32, 1)
  })

  val cmd_queue = Queue(io.mem_cmd, 4)

  val reg_address = Reg(UInt(32.W))
  val reg_byteenable = Reg(UInt(4.W))
  val reg_read = Reg(Bool())
  val reg_write = Reg(Bool())
  val reg_writedata = Reg(UInt(32.W))
  io.avmm.address := reg_address
  io.avmm.byteenable := reg_byteenable
  io.avmm.read := reg_read
  io.avmm.write := reg_write
  io.avmm.writedata := reg_writedata

  val resp_dw0 = Reg(UInt(32.W))
  val resp_dw1 = Reg(UInt(32.W))
  val resp_len = Reg(Bool())
  val resp_valid = RegInit(false.B)
  io.mem_resp.bits.dw0 := resp_dw0
  io.mem_resp.bits.dw1 := resp_dw1
  io.mem_resp.bits.len := resp_len
  io.mem_resp.valid := resp_valid

  val reg_pcie_req_id = Reg(UInt(16.W))
  val reg_pcie_tag = Reg(UInt(8.W))
  val reg_pcie_lo_addr = Reg(UInt(7.W))
  io.mem_resp.bits.pcie_req_id := reg_pcie_req_id
  io.mem_resp.bits.pcie_tag := reg_pcie_tag
  io.mem_resp.bits.pcie_lo_addr := reg_pcie_lo_addr

  object State extends ChiselEnum {
    val sIdle, sWrite, sRead, sRead2 = Value
  }

  val state = RegInit(State.sIdle)
  cmd_queue.ready := state === State.sIdle
  resp_valid := false.B

  switch(state) {
    is(State.sIdle) {
      when(cmd_queue.valid) {
        printf(p"AvalongAgent: received cmd = ${cmd_queue.bits}\n")
        reg_address := cmd_queue.bits.address
        reg_byteenable := cmd_queue.bits.byteenable
        reg_write := !cmd_queue.bits.read_write_b
        reg_read := cmd_queue.bits.read_write_b
        reg_writedata := cmd_queue.bits.writedata(31, 0)
        reg_pcie_req_id := cmd_queue.bits.pcie_req_id
        reg_pcie_tag := cmd_queue.bits.pcie_tag
        reg_pcie_lo_addr := cmd_queue.bits.address(6, 0)
        when(cmd_queue.bits.read_write_b) {
          state := State.sRead
        }.otherwise {
          state := State.sWrite
        }
      }
    }
    is(State.sWrite) {
      when(!io.avmm.waitrequest) {
        reg_write := false.B
        state := State.sIdle
      }
    }
    is(State.sRead) {
      when(io.avmm.readdatavalid) {
        // TODO, store, check length
        resp_len := 0.U
        resp_dw0 := io.avmm.readdata
        resp_valid := true.B

        reg_read := false.B
        state := State.sIdle
      }
    }
  }

  io.avmm.burstcount := 1.U // just to make the BFM happy

}
