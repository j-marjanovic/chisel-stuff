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

class CompletionGen(val if_width: Int) extends Module {
  val io = IO(new Bundle {
    val mem_resp = Flipped(new Interfaces.MemoryResp)
    val bm_resp = Flipped(new Interfaces.MemoryResp)

    val tx_st = new Interfaces.AvalonStreamTx(if_width)

    val conf_internal = Input(new Interfaces.ConfigIntern)
  })

  val cmd_mem_queue: DecoupledIO[Interfaces._MemoryResp] = Queue(io.mem_resp, 4)
  val cmd_bm_queue: DecoupledIO[Interfaces._MemoryResp] = Queue(io.bm_resp, 4)

  val reg_data = Reg(UInt(256.W))
  val reg_sop = Reg(Bool())
  val reg_eop = Reg(Bool())
  val reg_valid = RegInit(false.B)
  val reg_empty = Reg(UInt(2.W))
  io.tx_st.data := reg_data
  io.tx_st.sop := reg_sop
  io.tx_st.eop := reg_eop
  io.tx_st.valid := reg_valid
  io.tx_st.empty := reg_empty
  io.tx_st.err := false.B

  cmd_mem_queue.ready := true.B // TODO: check state
  cmd_bm_queue.ready := true.B // TODO: check state

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

  def write_cpld_from_queue(queue: DecoupledIO[Interfaces._MemoryResp]): Unit = {
    cpld.length := queue.bits.len
    cpld.byte_count := queue.bits.len * 4.U

    when(queue.bits.pcie_lo_addr(2, 0) === 4.U) {
      cpld.dw0_unalign := queue.bits.dw0
      cpld.dw0 := queue.bits.dw1
      cpld.dw1 := 0.U
    }.otherwise {
      cpld.dw0_unalign := 0.U
      cpld.dw0 := queue.bits.dw0
      cpld.dw1 := queue.bits.dw1
    }

    reg_empty := 1.U
    when(queue.bits.pcie_lo_addr(2, 0) === 4.U && queue.bits.len === 1.U) {
      reg_empty := 2.U
    }

    cpld.tag := queue.bits.pcie_tag
    cpld.req_id := queue.bits.pcie_req_id
    cpld.compl_id := io.conf_internal.busdev << 8
    cpld.lo_addr := queue.bits.pcie_lo_addr
  }

  object State extends ChiselEnum {
    val sIdle, sTx = Value
  }

  val state = RegInit(State.sIdle)
  val cntr = Reg(UInt(2.W))

  switch(state) {
    is(State.sIdle) {
      when(cmd_mem_queue.valid) {
        printf(p"Completion from cmd_mem_queue: ${cmd_mem_queue.bits}\n")
        write_cpld_from_queue(cmd_mem_queue)
      }.elsewhen(cmd_bm_queue.valid) {
        printf(p"Completion from cmd_bm_queue: ${cmd_bm_queue.bits}\n")
        write_cpld_from_queue(cmd_bm_queue)
      }

      when(cmd_mem_queue.valid || cmd_bm_queue.valid) {
        reg_data := cpld.asUInt()
        reg_valid := true.B
        reg_sop := true.B
        reg_eop := (if_width == 256).B

        if (if_width == 256) {
          cntr := 0.U
        } else if (if_width == 64) {
          when((cpld.lo_addr & 0x7.U) === 0.U) {
            cntr := 2.U
          }.otherwise {
            cntr := 1.U
          }
        } else {
          throw new Exception(s"Unsupported interface width ${if_width}")
        }

        state := State.sTx
      }
    }
    is(State.sTx) {
      when(io.tx_st.ready) {
        when(cntr > 0.U) {
          cntr := cntr - 1.U
          reg_data := reg_data >> 64.U
          reg_valid := true.B
          reg_sop := false.B
          reg_eop := cntr === 1.U
        }.otherwise {
          state := State.sIdle
          reg_data := 0.U
          reg_valid := false.B
          reg_sop := false.B
          reg_eop := false.B
        }
      }
    }
  }
}
