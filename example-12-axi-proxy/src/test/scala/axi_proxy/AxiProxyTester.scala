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

import bfmtester._

class AxiProxyTester(c: AxiProxyReg) extends BfmTester(c) {

  //==========================================================================
  // BFMs
  val mod_axi_mngr = BfmFactory.create_axilite_master(c.io.ctrl)
  val mod_axi_mem = BfmFactory.create_axi_slave(c.io.m)

  //==========================================================================
  // helper functions

  def read_blocking(addr: BigInt, CYC_TIMEOUT: Int = 100): BigInt = {
    mod_axi_mngr.readPush(addr)
    for (_ <- 0 to CYC_TIMEOUT) {
      val resp = mod_axi_mngr.getResponse()
      if (resp.isDefined) {
        return resp.get.rd_data
      }
      step(1)
    }

    throw new RuntimeException("AXI read timeout")
  }

  def write_blocking(addr: BigInt, data: BigInt, CYC_TIMEOUT: Int = 100): Unit = {
    mod_axi_mngr.writePush(addr, data)
    for (_ <- 0 to CYC_TIMEOUT) {
      val resp = mod_axi_mngr.getResponse()
      if (resp.isDefined) {
        return
      }
      step(1)
    }

    throw new RuntimeException("AXI write timeout")
  }

  //==========================================================================
  // main procedure
  step(10)

  // some basic checks (ID register, version, scratch)
  expect(read_blocking(0) == 0xa8122081L, "ID reg")

  val ver_reg = read_blocking(addr = 4)
  expect(ver_reg > 0 && ver_reg < 0x00ffffff, "version reg")

  expect(read_blocking(0x10) == 0x300, "status - both FSMs ready")
  write_blocking(0x44, 0xab)
  write_blocking(0x40, 0xcd000080L)
  write_blocking(0x100, 0x04030201)
  write_blocking(0x104, 0x08070605)
  write_blocking(0x14, 1)

  step(100)
  expect(read_blocking(0x10) == 0x301, "status - WR FSM done")
  write_blocking(0x14, 0x100)
  expect(read_blocking(0x10) == 0x300, "status - both FSMs ready")

  write_blocking(0x14, 2)
  step(100)
  expect(read_blocking(0x200) == 0x04030201, "data readback - word #0")
  expect(read_blocking(0x204) == 0x08070605, "data readback - word #1")

}
