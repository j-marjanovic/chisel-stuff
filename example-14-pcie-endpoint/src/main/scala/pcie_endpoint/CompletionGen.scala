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
import chisel3.util._

class CompletionGen extends Module {
  val io = IO(new Bundle {
    val mem_resp = Flipped(new Interfaces.MemoryResp)
    val bm_resp = Flipped(new Interfaces.MemoryResp)

    val tx_st = new Interfaces.AvalonStreamTx

    val conf_internal = Input(new Interfaces.ConfigIntern)
  })

  // TODO: rename
  val cmd_queue = Queue(io.mem_resp, 4)
  val cmd2_queue = Queue(io.bm_resp, 4)

  val reg_data = Reg(UInt(256.W))
  val reg_sop = Reg(Bool())
  val reg_eop = Reg(Bool())
  val reg_valid = Reg(Bool())
  val reg_empty = Reg(UInt(2.W))
  val reg_err = Reg(Bool())
  io.tx_st.data := reg_data
  io.tx_st.sop := reg_sop
  io.tx_st.eop := reg_eop
  io.tx_st.valid := reg_valid
  io.tx_st.empty := reg_empty
  io.tx_st.err := reg_err

  cmd_queue.ready := true.B // TODO: check state
  cmd2_queue.ready := true.B // TODO: check state

  val cpld = Wire(new CplD)
  cpld.fmt := 2.U
  cpld.typ := BigInt("01010", 2).U
  cpld.r1 := 0.U
  cpld.tc := 0.U
  cpld.r2 := 0.U
  cpld.attr2 := 0.U
  cpld.r3 := 0.U
  cpld.th := 0.U
  cpld.td := 0.U
  cpld.ep := 0.U
  cpld.attr1_0 := 0.U
  cpld.at := 0.U
  cpld.bcm := 0.U
  cpld.r := 0.U
  cpld.dw0_unalign := DontCare
  cpld.dw0 := DontCare
  cpld.dw1 := DontCare

  cpld.length := 0.U
  cpld.compl_id := io.conf_internal.busdev << 8
  cpld.byte_count := 0.U
  cpld.compl_status := 0.U
  cpld.req_id := 0.U
  cpld.tag := 0.U
  cpld.lo_addr := 0.U

  reg_err := false.B

  when(cmd_queue.valid) {
    printf(p"Completion from cmd_queue: ${cmd_queue.bits}\n")
    cpld.length := cmd_queue.bits.len
    cpld.byte_count := cmd_queue.bits.len * 4.U

    when(cmd_queue.bits.pcie_lo_addr(2, 0) === 4.U) {
      cpld.dw0_unalign := cmd_queue.bits.dw0
      cpld.dw0 := cmd_queue.bits.dw1
      cpld.dw1 := 0.U
    }.otherwise {
      cpld.dw0_unalign := 0.U
      cpld.dw0 := cmd_queue.bits.dw0
      cpld.dw1 := cmd_queue.bits.dw1
    }

    reg_empty := 1.U
    when(cmd_queue.bits.pcie_lo_addr(2, 0) === 4.U && cmd_queue.bits.len === 1.U) {
      reg_empty := 2.U
    }

    cpld.tag := cmd_queue.bits.pcie_tag
    cpld.req_id := cmd_queue.bits.pcie_req_id
    cpld.compl_id := io.conf_internal.busdev << 8
    cpld.lo_addr := cmd_queue.bits.pcie_lo_addr
    reg_data := cpld.asUInt()
    reg_valid := true.B
    reg_sop := true.B
    reg_eop := true.B
  }.elsewhen(cmd2_queue.valid) {
      printf(p"Completion from cmd2_queue: ${cmd2_queue.bits}\n")
      cpld.length := cmd2_queue.bits.len
      cpld.byte_count := cmd2_queue.bits.len * 4.U

      when(cmd2_queue.bits.pcie_lo_addr(2, 0) === 4.U) {
        cpld.dw0_unalign := cmd2_queue.bits.dw0
        cpld.dw0 := cmd2_queue.bits.dw1
        cpld.dw1 := 0.U
      }.otherwise {
        cpld.dw0_unalign := 0.U
        cpld.dw0 := cmd2_queue.bits.dw0
        cpld.dw1 := cmd2_queue.bits.dw1
      }

      reg_empty := 1.U
      when(cmd2_queue.bits.pcie_lo_addr(2, 0) === 4.U && cmd2_queue.bits.len === 1.U) {
        reg_empty := 2.U
      }

      cpld.tag := cmd2_queue.bits.pcie_tag
      cpld.req_id := cmd2_queue.bits.pcie_req_id
      cpld.compl_id := io.conf_internal.busdev << 8
      cpld.lo_addr := cmd2_queue.bits.pcie_lo_addr

      reg_data := cpld.asUInt()
      reg_valid := true.B
      reg_sop := true.B
      reg_eop := true.B
    }
    .elsewhen(io.tx_st.ready) {
      reg_data := 0.U
      reg_valid := false.B
      reg_sop := false.B
      reg_eop := false.B
    }
}
