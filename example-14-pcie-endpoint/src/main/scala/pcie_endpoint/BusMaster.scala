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

  val mod_regs = Module(new BusMasterRegs)
  mod_regs.io.ctrl_cmd <> io.ctrl_cmd
  mod_regs.io.ctrl_resp <> io.ctrl_resp

  val mod_engine = Module(new BusMasterEngine)
  mod_engine.io.tx_st <> io.tx_st
  mod_engine.io.conf_internal := io.conf_internal
  mod_engine.io.dma_desc := mod_regs.io.dma_desc

  mod_regs.io.fsm_busy := mod_engine.io.fsm_busy

}
