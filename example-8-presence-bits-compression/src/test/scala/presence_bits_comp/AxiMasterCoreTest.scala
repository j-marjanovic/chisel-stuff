/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import chisel3._
import bfmtester._

class AxiMasterCoreReg extends Module {
  //==========================================================================
  val addr_w = 48
  val data_w = 128
  val id_w = 6

  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W)
    val read = new AxiMasterCoreReadIface(addr_w.W, data_w.W)
    val write = new AxiMasterCoreWriteIface(addr_w.W, data_w.W)

    val cntr_wr_resp_okay = Output(UInt(32.W))
    val cntr_wr_resp_exokay = Output(UInt(32.W))
    val cntr_wr_resp_slverr = Output(UInt(32.W))
    val cntr_wr_resp_decerr = Output(UInt(32.W))

    val cntr_rd_resp_okay = Output(UInt(32.W))
    val cntr_rd_resp_exokay = Output(UInt(32.W))
    val cntr_rd_resp_slverr = Output(UInt(32.W))
    val cntr_rd_resp_decerr = Output(UInt(32.W))
  })

  val inst_axi = Module(new AxiMasterCore)

  io.m.AW.bits.id := inst_axi.io.m.AW.bits.id
  io.m.AW.bits.addr := inst_axi.io.m.AW.bits.addr
  io.m.AW.bits.len := inst_axi.io.m.AW.bits.len
  io.m.AW.bits.size := inst_axi.io.m.AW.bits.size
  io.m.AW.bits.burst := inst_axi.io.m.AW.bits.burst
  io.m.AW.bits.lock := inst_axi.io.m.AW.bits.lock
  io.m.AW.bits.cache := inst_axi.io.m.AW.bits.cache
  io.m.AW.bits.prot := inst_axi.io.m.AW.bits.prot
  io.m.AW.bits.qos := inst_axi.io.m.AW.bits.qos
  io.m.AW.valid := inst_axi.io.m.AW.valid
  inst_axi.io.m.AW.ready := RegNext(io.m.AW.ready)

  io.m.AR.bits.id := inst_axi.io.m.AR.bits.id
  io.m.AR.bits.addr := inst_axi.io.m.AR.bits.addr
  io.m.AR.bits.len := inst_axi.io.m.AR.bits.len
  io.m.AR.bits.size := inst_axi.io.m.AR.bits.size
  io.m.AR.bits.burst := inst_axi.io.m.AR.bits.burst
  io.m.AR.bits.lock := inst_axi.io.m.AR.bits.lock
  io.m.AR.bits.cache := inst_axi.io.m.AR.bits.cache
  io.m.AR.bits.prot := inst_axi.io.m.AR.bits.prot
  io.m.AR.bits.qos := inst_axi.io.m.AR.bits.qos
  io.m.AR.valid := inst_axi.io.m.AR.valid
  inst_axi.io.m.AR.ready := RegNext(io.m.AR.ready)

  io.m.W.bits.id := inst_axi.io.m.W.bits.id
  io.m.W.bits.data := inst_axi.io.m.W.bits.data
  io.m.W.bits.strb := inst_axi.io.m.W.bits.strb
  io.m.W.bits.last := inst_axi.io.m.W.bits.last
  io.m.W.valid := inst_axi.io.m.W.valid
  inst_axi.io.m.W.ready := RegNext(io.m.W.ready)

  inst_axi.io.m.B.bits.id := RegNext(io.m.B.bits.id)
  inst_axi.io.m.B.bits.resp := RegNext(io.m.B.bits.resp)
  inst_axi.io.m.B.valid := RegNext(io.m.B.valid)
  io.m.B.ready := inst_axi.io.m.B.ready

  inst_axi.io.m.R.bits.id := RegNext(io.m.R.bits.id)
  inst_axi.io.m.R.bits.data := RegNext(io.m.R.bits.data)
  inst_axi.io.m.R.bits.resp := RegNext(io.m.R.bits.resp)
  inst_axi.io.m.R.bits.last := RegNext(io.m.R.bits.last)
  inst_axi.io.m.R.valid := RegNext(io.m.R.valid)
  io.m.R.ready := inst_axi.io.m.R.ready

  inst_axi.io.read.addr := RegNext(io.read.addr)
  inst_axi.io.read.len := RegNext(io.read.len)
  inst_axi.io.read.start := RegNext(io.read.start)
  inst_axi.io.read.ready := RegNext(io.read.ready)
  io.read.data := inst_axi.io.read.data
  io.read.valid := inst_axi.io.read.valid

  inst_axi.io.write.addr := RegNext(io.write.addr)
  inst_axi.io.write.len := RegNext(io.write.len)
  inst_axi.io.write.start := RegNext(io.write.start)
  inst_axi.io.write.data := RegNext(io.write.data)
  inst_axi.io.write.valid := RegNext(io.write.valid)
  io.write.ready := inst_axi.io.write.ready

  io.cntr_wr_resp_okay := inst_axi.io.cntr_wr_resp_okay
  io.cntr_wr_resp_exokay := inst_axi.io.cntr_wr_resp_exokay
  io.cntr_wr_resp_slverr := inst_axi.io.cntr_wr_resp_slverr
  io.cntr_wr_resp_decerr := inst_axi.io.cntr_wr_resp_decerr

  io.cntr_rd_resp_okay := inst_axi.io.cntr_rd_resp_okay
  io.cntr_rd_resp_exokay := inst_axi.io.cntr_rd_resp_exokay
  io.cntr_rd_resp_slverr := inst_axi.io.cntr_rd_resp_slverr
  io.cntr_rd_resp_decerr := inst_axi.io.cntr_rd_resp_decerr
}

class AxiMasterCoreTest(c: AxiMasterCoreReg) extends BfmTester(c) {
  val DATA_LEN = 70

  val mod_axi_slave = new AxiMemSlave(c.io.m, this.rnd, bfm_peek, bfm_poke, println)
  val mod_usr_drv = new AxiMasterCoreUserDriver(c.io.write, bfm_peek, bfm_poke, println)
  val mod_usr_mon = new AxiMasterCoreUserMonitor(c.io.read, this.rnd, bfm_peek, bfm_poke, println)

  for (y <- 0 until DATA_LEN) {
    mod_usr_drv.add_data((0 to 15).map(x => (x | ((y & 0xf) << 4)).toByte))
  }

  step(10)

  mod_axi_slave.mem_stats()
  mod_usr_drv.start_write(0x4b010000, DATA_LEN)
  step(500)
  mod_axi_slave.mem_stats()
  mod_usr_drv.stats()

  expect(c.io.cntr_wr_resp_okay, Math.ceil(DATA_LEN / 32).toInt + 1)
  expect(c.io.cntr_wr_resp_exokay, 0)
  expect(c.io.cntr_wr_resp_slverr, 0)
  expect(c.io.cntr_wr_resp_decerr, 0)

  mod_usr_mon.start_read(0x4b010000, DATA_LEN)
  step(500)

  println(s"c.io.cntr_rd_resp_okay = ${peek(c.io.cntr_rd_resp_okay)}")
  expect(c.io.cntr_rd_resp_okay, Math.ceil(DATA_LEN / 32).toInt + 1)
  expect(c.io.cntr_rd_resp_exokay, 0)
  expect(c.io.cntr_rd_resp_slverr, 0)
  expect(c.io.cntr_rd_resp_decerr, 0)


}
