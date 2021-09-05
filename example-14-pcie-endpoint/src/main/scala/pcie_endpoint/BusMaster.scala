package pcie_endpoint

import chisel3._
import chisel3.util._
import scala.collection.mutable.Map

class BusMaster extends Module {
  val io = IO(new Bundle {
    val ctrl_cmd = Flipped(new Interfaces.MemoryCmd)
    val ctrl_resp = new Interfaces.MemoryResp

    val tx_st = new Interfaces.AvalonStreamTx
  })

  io.ctrl_cmd.ready := true.B

  val reg_id = 0xd3a01a2.U(32.W)
  val reg_version = 0x00000200.U(32.W)
  val reg_scratch = Reg(UInt(32.W))
  val reg_a = Reg(UInt(32.W))
  val reg_b = Reg(UInt(32.W))
  val reg_c = Reg(UInt(32.W))
  val reg_q1 = Reg(UInt(32.W))
  val reg_q2 = Reg(UInt(32.W))

  val reg_map = Map[Int, (UInt, Boolean)](
    0 -> (reg_id, false),
    4 -> (reg_version, false),
    8 -> (reg_scratch, true),
    0x10 -> (reg_a, true),
    0x14 -> (reg_b, true),
    0x18 -> (reg_c, true),
    0x20 -> (reg_q1, false),
    0x24 -> (reg_q2, false)
  )

  val reg_dummy = Reg(UInt(32.W))
  var reg_map_with_next = Map[Int, (UInt, UInt, Boolean, Boolean)]()

  for (el <- reg_map) {
    val reg_next: (UInt, Boolean) = reg_map.getOrElse(el._1, Tuple2(reg_dummy, false))
    reg_map_with_next += el._1 -> Tuple4(el._2._1, reg_next._1, el._2._2, reg_next._2)
  }

  reg_q1 := reg_a + reg_b
  reg_q2 := reg_a(15, 0) * reg_c(15, 0)

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

  // TODO:
  io.tx_st := DontCare
  io.tx_st.valid := false.B

}
