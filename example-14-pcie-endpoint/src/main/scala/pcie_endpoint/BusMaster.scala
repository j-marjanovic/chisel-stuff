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

import scala.collection.mutable.Map

class BusMaster extends Module {
  val io = IO(new Bundle {
    val conf_internal = Input(new Interfaces.ConfigIntern)

    val ctrl_cmd = Flipped(new Interfaces.MemoryCmd)
    val ctrl_resp = new Interfaces.MemoryResp

    val tx_st = new Interfaces.AvalonStreamTx
  })

  io.ctrl_cmd.ready := true.B

  val reg_id = 0xd3a01a2.U(32.W)
  val reg_version = 0x00000300.U(32.W)
  val reg_scratch = Reg(UInt(32.W))

  val reg_start = Reg(Bool())

  val reg_mwr32 = Reg(new MWr32)

  reg_mwr32.fmt := 0x2.U
  reg_mwr32.typ := 0.U
  reg_mwr32.r1 := false.B
  reg_mwr32.tc := 0.U
  reg_mwr32.r2 := false.B
  reg_mwr32.attr2 := false.B
  reg_mwr32.r3 := false.B
  reg_mwr32.th := false.B
  reg_mwr32.td := false.B
  reg_mwr32.ep := false.B
  reg_mwr32.attr1_0 := 0.U
  reg_mwr32.at := 0.U
  reg_mwr32.first_be := 0xf.U
  reg_mwr32.last_be := 0.U
  reg_mwr32.tag := 0.U
  reg_mwr32.req_id := io.conf_internal.busdev << 8.U
  reg_mwr32.ph := 0.U

  reg_start := false.B

  val reg_map = Map[Int, (UInt, Boolean)](
    0 -> (reg_id, false),
    4 -> (reg_version, false),
    8 -> (reg_scratch, true),
    0x10 -> (reg_mwr32.addr, true),
    // rsvd for 64-bit
    0x18 -> (reg_mwr32.dw0_unalign, true),
    0x20 -> (reg_mwr32.dw0, true),
    0x24 -> (reg_mwr32.dw1, true),
    0x28 -> (reg_mwr32.first_be, true),
    0x2c -> (reg_mwr32.last_be, true),
    0x30 -> (reg_mwr32.length, true),
    0x34 -> (reg_start, true)
  )

  var reg_map_with_next = Map[Int, (UInt, UInt, Boolean, Boolean)]()

  for (el <- reg_map) {
    val reg_dummy = Reg(UInt(32.W))
    reg_dummy := 0xbadcaffeL.U
    val reg_next: (UInt, Boolean) = reg_map.getOrElse(el._1 + 4, Tuple2(reg_dummy, false))
    reg_map_with_next += el._1 -> Tuple4(el._2._1, reg_next._1, el._2._2, reg_next._2)
  }

  io.ctrl_resp.valid := false.B
  io.ctrl_resp.bits := DontCare

  val reg_ctrl_resp = Reg(Output(new Interfaces.MemoryResp))
  io.ctrl_resp.valid := reg_ctrl_resp.valid
  io.ctrl_resp.bits := reg_ctrl_resp.bits

  reg_ctrl_resp.valid := false.B

  when(io.ctrl_cmd.valid) {
    when(io.ctrl_cmd.bits.read_write_b) {
      reg_ctrl_resp.valid := true.B
      reg_ctrl_resp.bits.pcie_req_id := io.ctrl_cmd.bits.pcie_req_id
      reg_ctrl_resp.bits.pcie_tag := io.ctrl_cmd.bits.pcie_tag
      reg_ctrl_resp.bits.pcie_lo_addr := io.ctrl_cmd.bits.address(6, 0)

      reg_ctrl_resp.bits.dw0 := 0xbadcaffeL.U
      reg_ctrl_resp.bits.dw1 := 0xbadcaffeL.U
      reg_ctrl_resp.bits.len := io.ctrl_cmd.bits.len

      for (el <- reg_map) {
        when(io.ctrl_cmd.bits.address(7, 0) === el._1.U) {
          reg_ctrl_resp.bits.dw0 := el._2._1
        }

        when(io.ctrl_cmd.bits.address(7, 0) === el._1.U - 4.U) {
          reg_ctrl_resp.bits.dw1 := el._2._1
        }
      }
    }.otherwise {
      for (el <- reg_map_with_next) {
        if (el._2._3) {
          when(io.ctrl_cmd.bits.address(7, 0) === el._1.U) {
            el._2._1 := io.ctrl_cmd.bits.writedata(31, 0)
          }
        }

        if (el._2._4) {
          when(io.ctrl_cmd.bits.address(7, 0) === el._1.U - 4.U && io.ctrl_cmd.bits.len === 2.U) {
            el._2._2 := io.ctrl_cmd.bits.writedata(63, 32)
          }
        }
      }
    }
  }

  object State extends ChiselEnum {
    val sIdle, sTxHdr = Value
  }

  val state = RegInit(State.sIdle)

  switch(state) {
    is(State.sIdle) {
      when(reg_start) {
        state := State.sTxHdr
      }
    }
    is(State.sTxHdr) {
      when(io.tx_st.ready) {
        state := State.sIdle
      }
    }
  }

  io.tx_st := DontCare
  io.tx_st.err := 0.U

  when(state === State.sTxHdr) {
    io.tx_st.valid := true.B
    io.tx_st.sop := true.B
    io.tx_st.eop := true.B
    io.tx_st.empty := 1.U
    io.tx_st.data := reg_mwr32.asUInt()
  }.otherwise {
    io.tx_st.valid := false.B
  }

}
