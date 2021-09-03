package pcie_endpoint

import chisel3._

class Configuration extends Module {
  val io = IO(new Bundle {
    val cfg = Input(new Interfaces.TLConfig)

    val conf_internal = Output(new Interfaces.ConfigIntern)
  })

  // from "Configuration Space Register Access Timing"
  val tl_cfg_add_reg = RegNext(io.cfg.add(0))
  val tl_cfg_add_reg2 = RegNext(tl_cfg_add_reg)

  val cfgctl_addr_change = RegNext(tl_cfg_add_reg2 =/= tl_cfg_add_reg)
  val cfgctl_addr_change2 = RegNext(cfgctl_addr_change)
  val cfgctl_addr_strobe = RegNext(cfgctl_addr_change2)

  val reg_busdev = RegInit(UInt(12.W), 0.U)
  when(cfgctl_addr_strobe) {
    when(io.cfg.add === 0xf.U) {
      printf(p"Configuration: busdev = ${io.cfg.ctl}\n")
      reg_busdev := io.cfg.ctl(11, 0)
    }
  }

  io.conf_internal.busdev := reg_busdev

}
