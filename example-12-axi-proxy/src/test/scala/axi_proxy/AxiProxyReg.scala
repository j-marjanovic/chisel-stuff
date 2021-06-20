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

package axi_proxy

import chisel3._
import bfmtester._

class AxiProxyReg(
    val addr_w: Int = 40,
    val data_w: Int = 128,
    val id_w: Int = 5,
    val ctrl_data_w: Int = 32,
    val ctrl_addr_w: Int = 10
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)
    val ctrl = new AxiLiteIf(ctrl_addr_w.W, ctrl_data_w.W)
  })

  val inst_dut = Module(new AxiProxy(addr_w, data_w, id_w, ctrl_data_w, ctrl_addr_w))

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

  io.ctrl <> inst_dut.io.ctrl
}
