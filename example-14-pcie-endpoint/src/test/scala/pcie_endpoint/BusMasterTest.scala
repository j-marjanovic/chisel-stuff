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

import bfmtester.BfmTester

class BusMasterTest(c: BusMaster) extends BfmTester(c) {
  val bfm_avalon_st_tx = new AvalonStreamTxBfm(c.io.tx_st, bfm_peek, bfm_poke, println)

  var tag = 1

  def write(addr: BigInt, data: BigInt) = {
    poke(c.io.ctrl_cmd.bits.address, addr)
    poke(c.io.ctrl_cmd.bits.byteenable, 0xf)
    poke(c.io.ctrl_cmd.bits.read_write_b, 0)
    poke(c.io.ctrl_cmd.bits.writedata, data)
    poke(c.io.ctrl_cmd.bits.len, 0x1)
    poke(c.io.ctrl_cmd.bits.pcie_req_id, 0x18)
    poke(c.io.ctrl_cmd.bits.pcie_tag, tag)
    poke(c.io.ctrl_cmd.valid, 1)
    step(1)

    poke(c.io.ctrl_cmd.bits.address, 0)
    poke(c.io.ctrl_cmd.bits.byteenable, 0)
    poke(c.io.ctrl_cmd.bits.read_write_b, 0)
    poke(c.io.ctrl_cmd.bits.writedata, 0)
    poke(c.io.ctrl_cmd.bits.len, 0)
    poke(c.io.ctrl_cmd.bits.pcie_req_id, 0)
    poke(c.io.ctrl_cmd.bits.pcie_tag, 0)
    poke(c.io.ctrl_cmd.valid, 0)
    step(1)

    tag += 1
  }

  write(0x20, 0xcd001000L)
  write(0x28, 4)
  write(0x2c, 0x9)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 2 * 4)
  write(0x2c, 0xa)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 4 * 4)
  write(0x2c, 0xb)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 8 * 4)
  write(0x2c, 0xc)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 16 * 4)
  write(0x2c, 0xd)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 256)
  write(0x2c, 0xe)
  write(0x14, 1)
  step(20)

  write(0x20, 0xcd001000L)
  write(0x28, 256 * 4)
  write(0x2c, 0xf)
  write(0x14, 1)
  step(100)
}
