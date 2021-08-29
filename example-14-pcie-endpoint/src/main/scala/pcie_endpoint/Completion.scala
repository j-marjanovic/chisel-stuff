package pcie_endpoint

import chisel3._
import chisel3.util._

class Completion extends Module {
  val io = IO(new Bundle {
    val mem_resp = Flipped(new Interfaces.MemoryResp)

    val tx_st = new Interfaces.AvalonStreamTx

    val conf_internal = Input(new Interfaces.ConfigIntern)
  })

  val cmd_queue = Queue(io.mem_resp, 4)

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

  cpld.length := 0.U // TODO
  cpld.compl_id := io.conf_internal.busdev << 8
  cpld.byte_count := 0.U
  cpld.compl_status := 0.U
  cpld.req_id := 0.U
  cpld.tag := 0.U
  cpld.lo_addr := 0.U

  reg_sop := false.B
  reg_eop := false.B
  reg_empty := 1.U
  reg_err := false.B

  when(cmd_queue.valid) {
    printf(p"Completion: ${cmd_queue.bits}\n")
    cpld.length := 1.U
    cpld.compl_id := 0x4.U << 8 //io.conf_internal.busdev << 8
    cpld.byte_count := 4.U
    cpld.tag := 0.U //0x123.U
    cpld.req_id := 0x18.U
    cpld.dw0 := cmd_queue.bits.dw0
    reg_data := cpld.asUInt()
    reg_valid := true.B
    reg_sop := true.B
    reg_eop := true.B
  }.otherwise {
    reg_data := 0.U
    reg_valid := false.B
  }
}