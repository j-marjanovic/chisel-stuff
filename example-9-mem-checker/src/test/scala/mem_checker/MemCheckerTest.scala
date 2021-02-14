/*
Copyright (c) 2020-2021 Jan Marjanovic

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

package mem_checker

import bfmtester._

class MemCheckerTest(c: MemChecker) extends BfmTester(c) {
  // BFMs
  val mod_axi_master = BfmFactory.create_axilite_master(c.io.ctrl)
  val mod_axi_mem_slave = BfmFactory.create_avalon_slave(c.io.mem)

  // constants
  val READ_ADDR = 0x0a020000
  val WRITE_ADDR = READ_ADDR
  val LEN_BYTES = 32+256

  // 1. check version
  mod_axi_master.readPush(4)
  step(50)
  val rd_resp = mod_axi_master.getResponse().get
  expect(rd_resp.success, "read successful")
  expect(rd_resp.rd_data == 0x10000, "IP version")

  for (mode <- 0 to 7) {
    // 2. prepare the write side
    // set addresses, lengths
    mod_axi_master.writePush(0x10, data = 1 | mode << 8)
    mod_axi_master.writePush(0x68, data = WRITE_ADDR)
    mod_axi_master.writePush(0x6C, data = 0)
    mod_axi_master.writePush(0x70, data = LEN_BYTES)
    mod_axi_master.writePush(0x64, data = 1)
    step(100)
    for (i <- 0 until 5) {
      val wr_resp = mod_axi_master.getResponse().get
      expect(wr_resp.success, "write successful")
    }
    step(1000)

    // 3. prepare the read side
    mod_axi_master.writePush(0x10, data = 0 | mode << 8)
    mod_axi_master.writePush(0x28, data = READ_ADDR)
    mod_axi_master.writePush(0x2c, data = 0)
    mod_axi_master.writePush(0x30, data = LEN_BYTES)
    mod_axi_master.writePush(0x24, data = 1)
    step(100)
    for (i <- 0 until 5) {
      val wr_resp = mod_axi_master.getResponse().get
      expect(wr_resp.success, "write successful")
    }

    // 4.
    step(1000)
    mod_axi_mem_slave.mem_stats()
    mod_axi_master.readPush(0x20)
    mod_axi_master.readPush(0x60)
    step(100)
    val read_status = mod_axi_master.getResponse().get
    val write_status = mod_axi_master.getResponse().get
    expect(read_status.rd_data == 1, "read status reports done")
    expect(write_status.rd_data == 1, "write status reports done")

    // clear with write
    mod_axi_master.writePush(0x20, 1)
    mod_axi_master.writePush(0x60, 1)
    step(50)
    mod_axi_master.getResponse()
    mod_axi_master.getResponse()

    mod_axi_master.readPush(0x20)
    mod_axi_master.readPush(0x60)
    step(50)
    val read_status_clr = mod_axi_master.getResponse().get
    val write_status_clr = mod_axi_master.getResponse().get
    expect(read_status_clr.rd_data == 0, "read status was cleared (W1C)")
    expect(write_status_clr.rd_data == 0, "write status was cleared (W1C)")

    // check ok, check tot
    mod_axi_master.readPush(0xa0)
    mod_axi_master.readPush(0xa4)
    step(50)
    val check_tot = mod_axi_master.getResponse().get
    val check_ok = mod_axi_master.getResponse().get
    expect(check_tot.rd_data == LEN_BYTES / (c.data_w / 8), "check total")
    expect(check_ok.rd_data == LEN_BYTES / (c.data_w / 8), "check OK")

    // duration
    mod_axi_master.readPush(0x38)
    mod_axi_master.readPush(0x78)
    step(50)
    val read_duration = mod_axi_master.getResponse().get
    val write_duration = mod_axi_master.getResponse().get
    expect(read_duration.rd_data >= LEN_BYTES / (c.data_w / 8), "read duration")
    expect(write_duration.rd_data >= LEN_BYTES / (c.data_w / 8), "write duration")

    // counters
    mod_axi_master.readPush(0x34)
    mod_axi_master.readPush(0x74)
    step(50)
    val read_counter = mod_axi_master.getResponse().get
    val write_counter = mod_axi_master.getResponse().get
    val read_counter_exp = LEN_BYTES / (c.data_w / 8)
    val write_counter_exp = math.ceil(LEN_BYTES / 16 / 8).toInt
    expect(read_counter.rd_data == read_counter_exp, "read counter")
    expect(write_counter.rd_data == write_counter_exp, "read counter")
  }
}
