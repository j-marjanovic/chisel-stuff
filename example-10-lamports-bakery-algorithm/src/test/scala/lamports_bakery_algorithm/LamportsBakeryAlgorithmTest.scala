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

package lamports_bakery_algorithm

import bfmtester._

case class DutDone(message: String = "") extends Exception(message)

class LamportsBakeryAlgorithmTest(c: LamportsBakeryAlgorithm) extends BfmTester(c) {
  // BFMs
  val mod_axi_mngr = BfmFactory.create_axilite_master(c.io.ctrl)
  val mod_axi_mem = new Axi4LiteMemSubordinate(c.io.m, rnd, bfm_peek, bfm_poke, println)

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
  val id_reg = read_blocking(0)
  expect(id_reg == 0xbace2a19L, "ID reg")

  val ver_reg = read_blocking(addr = 4)
  // expect(ver_reg == 0x010300, "Version register")

  // config addresses
  write_blocking(0x24, 0xab)
  write_blocking(0x20, 0xcd001008L)
  write_blocking(0x2c, 0xab)
  write_blocking(0x28, 0xcd001040L)
  write_blocking(0x34, 0xab)
  write_blocking(0x30, 0xcd001080L)

  // config instance (1 of 4)
  write_blocking(0x40, 1)
  write_blocking(0x44, 3)

  // config nr loops
  write_blocking(0x50, 9)

  // config PRBS delay
  write_blocking(0x55, 0xa111)

  // start
  write_blocking(0x14, 1)

  // wait for DONE
  try {
    for (_ <- 0 until 1000) {
      val status = read_blocking(0x10)
      if ((status & 1) == 1) {
        throw DutDone()
      }
      step(50)
    }
    expect(good = false, "Dut did not reach DONE state")
  } catch {
    case c: DutDone =>
  }

  // clear DONE
  write_blocking(0x14, 2)

  val status = read_blocking(0x10)
  expect(status == 0, "Clear command cleared DONE bit")

}
