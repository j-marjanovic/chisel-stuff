package lamports_bakery_algorithm

import bfmtester._
import chisel3._

class Axi4ManagerReg(val addr_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, 128.W, 4.W, aruser_w = 2.W, awuser_w = 2.W)

    val config_axi_cache = Input(UInt(4.W))
    val config_axi_prot = Input(UInt(3.W))
    val config_axi_user = Input(UInt(2.W))

    val wr_cmd = new Axi4ManagerWrCmd(addr_w, 32)
    val rd_cmd = new Axi4ManagerRdCmd(addr_w, 32)
  })

  val inst_dut = Module(new Axi4Manager(addr_w))

  io.m.AW.bits.id := inst_dut.io.m.AW.bits.id
  io.m.AW.bits.addr := inst_dut.io.m.AW.bits.addr
  io.m.AW.bits.len := inst_dut.io.m.AW.bits.len
  io.m.AW.bits.size := inst_dut.io.m.AW.bits.size
  io.m.AW.bits.burst := inst_dut.io.m.AW.bits.burst
  io.m.AW.bits.lock := inst_dut.io.m.AW.bits.lock
  io.m.AW.bits.cache := inst_dut.io.m.AW.bits.cache
  io.m.AW.bits.prot := inst_dut.io.m.AW.bits.prot
  io.m.AW.bits.qos := inst_dut.io.m.AW.bits.qos
  io.m.AW.bits.user := inst_dut.io.m.AW.bits.user
  io.m.AW.valid := inst_dut.io.m.AW.valid
  inst_dut.io.m.AW.ready := RegNext(io.m.AW.ready)

  io.m.AR.bits.id := inst_dut.io.m.AR.bits.id
  io.m.AR.bits.addr := inst_dut.io.m.AR.bits.addr
  io.m.AR.bits.len := inst_dut.io.m.AR.bits.len
  io.m.AR.bits.size := inst_dut.io.m.AR.bits.size
  io.m.AR.bits.burst := inst_dut.io.m.AR.bits.burst
  io.m.AR.bits.lock := inst_dut.io.m.AR.bits.lock
  io.m.AR.bits.cache := inst_dut.io.m.AR.bits.cache
  io.m.AR.bits.prot := inst_dut.io.m.AR.bits.prot
  io.m.AR.bits.qos := inst_dut.io.m.AR.bits.qos
  io.m.AR.bits.user := inst_dut.io.m.AR.bits.user
  io.m.AR.valid := inst_dut.io.m.AR.valid
  inst_dut.io.m.AR.ready := RegNext(io.m.AR.ready)

  io.m.W.bits.data := inst_dut.io.m.W.bits.data
  io.m.W.bits.strb := inst_dut.io.m.W.bits.strb
  io.m.W.bits.last := inst_dut.io.m.W.bits.last
  io.m.W.bits.user := inst_dut.io.m.W.bits.user
  io.m.W.valid := inst_dut.io.m.W.valid
  inst_dut.io.m.W.ready := RegNext(io.m.W.ready)

  inst_dut.io.m.B.bits.id := RegNext(io.m.B.bits.id)
  inst_dut.io.m.B.bits.resp := RegNext(io.m.B.bits.resp)
  inst_dut.io.m.B.bits.user := RegNext(io.m.B.bits.user)
  inst_dut.io.m.B.valid := RegNext(io.m.B.valid)
  io.m.B.ready := inst_dut.io.m.B.ready

  inst_dut.io.m.R.bits.id := RegNext(io.m.R.bits.id)
  inst_dut.io.m.R.bits.data := RegNext(io.m.R.bits.data)
  inst_dut.io.m.R.bits.resp := RegNext(io.m.R.bits.resp)
  inst_dut.io.m.R.bits.last := RegNext(io.m.R.bits.last)
  inst_dut.io.m.R.bits.user := RegNext(io.m.R.bits.user)
  inst_dut.io.m.R.valid := RegNext(io.m.R.valid)
  io.m.R.ready := inst_dut.io.m.R.ready

  io.config_axi_cache <> inst_dut.io.config_axi_cache
  io.config_axi_prot <> inst_dut.io.config_axi_prot
  io.config_axi_user <> inst_dut.io.config_axi_user
  io.wr_cmd <> inst_dut.io.wr_cmd
  io.rd_cmd <> inst_dut.io.rd_cmd
}
