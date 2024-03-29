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

import bfmtester.AvalonMMIf
import chisel3._
import chisel3.util._

class PcieEndpointWrapper(val if_width: Int) extends RawModule {
  val coreclkout_hip = IO(Input(Clock()))

  // hip_rst interface
  val reset_status = IO(Input(Bool()))
  val serdes_pll_locked = IO(Input(Bool()))
  val pld_clk_inuse = IO(Input(Bool()))
  val pld_core_ready = IO(Output(Bool()))
  val testin_zero = IO(Input(Bool()))

  val pld_clk_hip = IO(Output(Clock()))

  val rx_st = IO(new Interfaces.AvalonStreamRx(if_width))
  val tx_st = IO(new Interfaces.AvalonStreamTx(if_width))

  val app_int = IO(new Interfaces.AppInt)

  val dma_out = IO(new Interfaces.AvalonStreamDataOut(if_width))
  val dma_in = IO(new Interfaces.AvalonStreamDataIn(if_width))

  // config_tl interface
  val tl_cfg = IO(Input(new Interfaces.TLConfig))
  val cpl_err = IO(Output(UInt(7.W)))
  val cpl_pending = IO(Output(Bool()))
  val hpg_ctrler = IO(Output(UInt(5.W)))

  val avmm_bar0 = IO(new AvalonMMIf(32, 32, 1))

  val reset_out = IO(Output(Bool()))

  // logic

  val pcie_endpoint = withClockAndReset(coreclkout_hip, reset_status) {
    Module(new PcieEndpoint(if_width))
  }

  val tx_ready_corr = withClockAndReset(coreclkout_hip, reset_status) {
    Module(new ReadyCorrection)
  }

  pcie_endpoint.rx_st <> rx_st
  pcie_endpoint.tl_cfg <> tl_cfg
  pcie_endpoint.avmm_bar0 <> avmm_bar0
  pcie_endpoint.app_int <> app_int
  pcie_endpoint.dma_out <> dma_out
  pcie_endpoint.dma_in <> dma_in

  // tx
  tx_ready_corr.core_ready := tx_st.ready
  tx_ready_corr.app_valid := pcie_endpoint.tx_st.valid
  pcie_endpoint.tx_st.ready := tx_ready_corr.app_ready
  tx_st.valid := tx_ready_corr.core_valid
  tx_st.data := pcie_endpoint.tx_st.data
  tx_st.sop := pcie_endpoint.tx_st.sop
  tx_st.eop := pcie_endpoint.tx_st.eop
  tx_st.empty := pcie_endpoint.tx_st.empty
  tx_st.err := pcie_endpoint.tx_st.err

  // as in example design
  pld_core_ready := serdes_pll_locked
  pld_clk_hip := coreclkout_hip

  // tie-offs
  cpl_err := 0.U
  cpl_pending := 0.U
  hpg_ctrler := 0.U

  withClock(coreclkout_hip) {
    reset_out := RegNext(reset_status && serdes_pll_locked)
  }
}

class PcieEndpoint(val if_width: Int) extends MultiIOModule {
  val rx_st = IO(new Interfaces.AvalonStreamRx(if_width))
  val tx_st = IO(new Interfaces.AvalonStreamTx(if_width))
  val tl_cfg = IO(Input(new Interfaces.TLConfig))
  val avmm_bar0 = IO(new AvalonMMIf(32, 32, 1))
  val app_int = IO(new Interfaces.AppInt)
  val dma_out = IO(new Interfaces.AvalonStreamDataOut(if_width))
  val dma_in = IO(new Interfaces.AvalonStreamDataIn(if_width))

  val mod_config = Module(new Configuration)
  mod_config.io.cfg <> tl_cfg

  val reg_mem_rw = Reg(Output(new Interfaces.AvalonStreamRx(if_width)))
  reg_mem_rw := rx_st
  rx_st.ready := true.B
  rx_st.mask := false.B // we are always ready

  val mod_mem_read_write = Module(new MemoryReadWriteCompl(if_width))
  mod_mem_read_write.io.rx_st.data := reg_mem_rw.data
  mod_mem_read_write.io.rx_st.sop := reg_mem_rw.sop
  mod_mem_read_write.io.rx_st.eop := reg_mem_rw.eop
  mod_mem_read_write.io.rx_st.empty := reg_mem_rw.empty
  mod_mem_read_write.io.rx_st.valid := reg_mem_rw.valid
  mod_mem_read_write.io.rx_st.err := reg_mem_rw.err
  mod_mem_read_write.io.rx_st.bar := reg_mem_rw.bar
  mod_mem_read_write.io.rx_st.be := reg_mem_rw.be

  val mod_avalon_agent_bar0 = Module(new AvalonAgent)
  mod_avalon_agent_bar0.io.mem_cmd <> mod_mem_read_write.io.mem_cmd_bar0
  mod_avalon_agent_bar0.io.avmm <> avmm_bar0

  val mod_compl_gen = Module(new CompletionGen(if_width))
  mod_compl_gen.io.mem_resp <> mod_avalon_agent_bar0.io.mem_resp
  mod_compl_gen.io.conf_internal := mod_config.io.conf_internal

  val mod_compl_recv = Module(new CompletionRecv(if_width))
  mod_compl_recv.io.data := mod_mem_read_write.io.cpld.data
  mod_compl_recv.io.valid := mod_mem_read_write.io.cpld.valid
  mod_compl_recv.io.sop := mod_mem_read_write.io.cpld.sop
  dma_out := mod_compl_recv.io.data_out

  val mod_bus_master = Module(new BusMaster(if_width))
  mod_bus_master.io.conf_internal := mod_config.io.conf_internal
  mod_bus_master.io.ctrl_cmd <> mod_mem_read_write.io.mem_cmd_bar2
  mod_compl_gen.io.bm_resp <> mod_bus_master.io.ctrl_resp
  mod_bus_master.io.mrd_in_flight_dec := mod_compl_recv.io.mrd_in_flight_dec
  mod_bus_master.io.dma_in <> dma_in

  val mod_tx_arbiter = Module(new TxArbiter(if_width))
  mod_tx_arbiter.io.cpld <> mod_compl_gen.io.tx_st
  mod_tx_arbiter.io.bm <> mod_bus_master.io.tx_st
  mod_tx_arbiter.io.bm_hint := mod_bus_master.io.arb_hint
  mod_tx_arbiter.io.tx_st <> tx_st

  val mod_int_ctrl = Module(new InterruptCtrl)
  mod_int_ctrl.io.app_int <> app_int
  mod_int_ctrl.io.conf_internal <> mod_config.io.conf_internal
  mod_int_ctrl.io.trigger <> mod_bus_master.io.irq_fire

}
