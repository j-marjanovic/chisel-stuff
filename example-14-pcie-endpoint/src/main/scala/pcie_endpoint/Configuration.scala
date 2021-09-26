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

  val reg_msicsr = RegInit(UInt(16.W), 0.U)
  val reg_busdev = RegInit(UInt(13.W), 0.U)

  when(cfgctl_addr_strobe) {
    when(io.cfg.add === 0xd.U) {
      reg_msicsr := io.cfg.ctl(15, 0)
    }.elsewhen(io.cfg.add === 0xf.U) {
      printf(p"Configuration: busdev = ${io.cfg.ctl}\n")
      reg_busdev := io.cfg.ctl(12, 0)
    }
  }

  io.conf_internal.busdev := reg_busdev
  io.conf_internal.msicsr := reg_msicsr

}
