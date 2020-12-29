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

import bfmtester._

class DecompressorAxiSlaveTest(c: DecompressorAxiSlave) extends BfmTester(c) {

  val mod_axi = BfmFactory.create_axilite_master(c.io.ctrl)

  val mod_pulse_read_start = BfmFactory.create_pulse_detector(c.io.read_start, "read start")
  val mod_pulse_write_start = BfmFactory.create_pulse_detector(c.io.write_start, "write start")

  // check ident, version
  mod_axi.readPush(0x0)
  mod_axi.readPush(0x4)
  step(30)
  val id_reg: mod_axi.Resp = mod_axi.getResponse().get
  val version: mod_axi.Resp = mod_axi.getResponse().get
  expect(id_reg.rd_data == 0x72e5b175, "ID reg")
  expect(version.rd_data == 0x00010203, "version")

  // set addresses, lengths
  mod_axi.writePush(0x24, data = 0xaabbccdd)
  mod_axi.writePush(0x28, data = 0xfffe8001)
  mod_axi.writePush(0x2c, data = 0x41000)
  mod_axi.writePush(0x44, data = 0x44556677)
  mod_axi.writePush(0x48, data = 0xfff80203)
  mod_axi.writePush(0x4c, data = 0x82000)
  step(50)
  for (i <- 0 until 6) {
    val wr_resp = mod_axi.getResponse().get
    expect(wr_resp.success, "write successful")
  }

  expect(c.io.read_addr, BigInt("fffe8001aabbccdd", 16), "read address")
  expect(c.io.read_len, 0x41000, "read length")
  expect(c.io.write_addr, BigInt("fff8020344556677", 16), "write address")
  expect(c.io.write_len, 0x82000, "write length")

  // pulse on read interface
  mod_axi.writePush(0x20, data = 1)
  step(20)
  val read_start_list1: List[mod_pulse_read_start.Pulse] = mod_pulse_read_start.getPulses
  val write_start_list1: List[mod_pulse_write_start.Pulse] = mod_pulse_write_start.getPulses
  expect(read_start_list1.length == 1, "one pulse on the read start interface")
  expect(write_start_list1.isEmpty, "no pulses on the write start interface")
  expect(read_start_list1.head.len == 1, "read start pulse length of 1")

  // pulse on write interface
  mod_axi.writePush(0x40, data = 1)
  step(20)
  val read_start_list2: List[mod_pulse_read_start.Pulse] = mod_pulse_read_start.getPulses
  val write_start_list2: List[mod_pulse_write_start.Pulse] = mod_pulse_write_start.getPulses
  expect(read_start_list2.isEmpty, "no pulses on the write start interface")
  expect(write_start_list2.length == 1, "no pulses on the write start interface")
  expect(write_start_list2.head.len == 1, "write start pulse length of 1")
}
