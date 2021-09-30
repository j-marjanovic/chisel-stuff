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

import scala.collection.mutable.Map

class BusMasterRegs extends Module {
  val io = IO(new Bundle {
    val ctrl_cmd = Flipped(new Interfaces.MemoryCmd)
    val ctrl_resp = new Interfaces.MemoryResp

    val dma_desc = Valid(new Interfaces.DmaDesc)

    val fsm_busy = Input(Bool())

    val debug_trigger = Output(Bool())
  })

  io.ctrl_cmd.ready := true.B

  val reg_id = 0xd3a01a2.U(32.W)
  val reg_version = 0x00000605.U(32.W)
  val reg_scratch = Reg(UInt(32.W))

  val reg_status = Wire(UInt(32.W))
  reg_status := Cat(0.U(31.W), io.fsm_busy)

  val reg_dma_desc_valid = RegNext(false.B)
  io.dma_desc.valid := reg_dma_desc_valid

  val reg_dma_desc = Reg(Output(new Interfaces.DmaDesc))
  val reg_dma_desc_control = Reg(UInt(32.W))
  io.dma_desc.bits := reg_dma_desc
  io.dma_desc.bits.control := reg_dma_desc_control.asTypeOf(reg_dma_desc.control)

  val reg_test_irq_req = RegInit(false.B)
  io.debug_trigger := reg_test_irq_req

  val reg_test_irq_clr = RegInit(false.B)

  reg_test_irq_clr := false.B
  when(reg_test_irq_clr) {
    reg_test_irq_req := false.B
  }

  val reg_map = Map[Int, (UInt, Boolean)](
    0 -> (reg_id, false),
    4 -> (reg_version, false),
    8 -> (reg_scratch, true),
    0x10 -> (reg_status, false),
    0x14 -> (reg_dma_desc_valid, true),
    0x20 -> (reg_dma_desc.addr32_0, true),
    0x24 -> (reg_dma_desc.addr63_32, true),
    0x28 -> (reg_dma_desc.len_bytes, true),
    0x2c -> (reg_dma_desc_control, true),
    0x60 -> (reg_test_irq_req, true),
    0x64 -> (reg_test_irq_clr, true)
  )

  // we need this to handle
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
      printf(
        "Bus Master: read: address = %x\n",
        io.ctrl_cmd.bits.address(7, 0)
      )
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
      printf(
        "Bus Master: write: address = %x, writedata = %x\n",
        io.ctrl_cmd.bits.address(7, 0),
        io.ctrl_cmd.bits.writedata
      )
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
}
