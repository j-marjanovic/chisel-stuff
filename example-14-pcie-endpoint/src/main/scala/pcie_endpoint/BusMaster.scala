package pcie_endpoint

import chisel3._
import chisel3.util._

class BusMaster extends Module {
  val io = IO(new Bundle {
    val ctrl_cmd = Flipped(new Interfaces.MemoryCmd)
    val ctrl_resp = new Interfaces.MemoryResp

    val tx_st = new Interfaces.AvalonStreamTx
  })

  io.ctrl_cmd.ready := true.B

  val reg_id = 0xd3a01a2.U(32.W)
  val reg_version = 0x00000100.U(32.W)
  val reg_scratch = Reg(UInt(32.W))
  val reg_a = Reg(UInt(32.W))
  val reg_b = Reg(UInt(32.W))
  val reg_q = Reg(UInt(32.W))

  reg_q := reg_a + reg_b

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

      switch(io.ctrl_cmd.bits.address(7, 0)) {
        is(0.U) {
          reg_ctrl_resp.bits.dw0 := reg_id
          reg_ctrl_resp.bits.dw1 := reg_version
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(4.U) {
          reg_ctrl_resp.bits.dw0 := reg_version
          reg_ctrl_resp.bits.dw1 := reg_scratch
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(8.U) {
          reg_ctrl_resp.bits.dw0 := reg_scratch
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(0xc.U) {
          reg_ctrl_resp.bits.dw1 := reg_a
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(0x10.U) {
          reg_ctrl_resp.bits.dw0 := reg_a
          reg_ctrl_resp.bits.dw1 := reg_b
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(0x14.U) {
          reg_ctrl_resp.bits.dw0 := reg_b
          reg_ctrl_resp.bits.dw1 := reg_q
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
        is(0x18.U) {
          reg_ctrl_resp.bits.dw0 := reg_q
          reg_ctrl_resp.bits.len := 1.U // TODO
        }
      }
    }.otherwise {
      switch(io.ctrl_cmd.bits.address(7, 0)) {
        is(0x10.U) {
          reg_a := io.ctrl_cmd.bits.writedata
        }
        is(0x14.U) {
          reg_b := io.ctrl_cmd.bits.writedata
        }
      }
    }
  }

  // TODO:
  io.tx_st := DontCare
  io.tx_st.valid := false.B

}
