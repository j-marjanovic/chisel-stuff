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

package poor_mans_system_ila

import bfmtester._

import scala.collection.mutable.ListBuffer

class PoorMansSystemILAFilterTester(c: PoorMansSystemILA) extends BfmTester(c) {
  def read_blocking(addr: BigInt, CYC_TIMEOUT: Int = 100): BigInt = {
    mod_axi_manager.readPush(addr)
    for (_ <- 0 to CYC_TIMEOUT) {
      val resp = mod_axi_manager.getResponse()
      if (resp.isDefined) {
        return resp.get.rd_data
      }
      step(1)
    }

    throw new RuntimeException("AXI read timeout")
  }

  def write_blocking(addr: BigInt,
                     data: BigInt,
                     CYC_TIMEOUT: Int = 100): Unit = {
    mod_axi_manager.writePush(addr, data)
    for (_ <- 0 to CYC_TIMEOUT) {
      val resp = mod_axi_manager.getResponse()
      if (resp.isDefined) {
        return
      }
      step(1)
    }

    throw new RuntimeException("AXI write timeout")
  }

  def step_until_empty(): Unit = {
    while (true) {
      if (mod_slow_sig_gen.is_empty()) {
        return
      }
      step(1)
    }
  }

  def leftPad(s: String, l: Int = 32): String = {
    " " * (l - s.length) + s
  }

  // modules
  val mod_axi_manager = BfmFactory.create_axilite_master(c.io.ctrl)
  val mod_slow_sig_gen =
    new SlowSignalGen(c.io.MBDEBUG.TDO, bfm_peek, bfm_poke, println)


  step(10)

  // some basic checks (ID register, version, scratch)
  val id_reg = read_blocking(0)
  expect(id_reg == 0x5157311a, "ID reg")

  val ver_reg = read_blocking(addr = 4)
  expect(ver_reg == 0x010300, "Version register")

  val scratch_reg_val = 271828182
  write_blocking(0xc, scratch_reg_val)
  val scratch_reg_readback = read_blocking(0xc)
  expect(scratch_reg_val == scratch_reg_readback, "Scratch reg write + read")

  // enable
  write_blocking(0x24, 0x40000000) // 0x4000 - TDO
  step(200)

  mod_slow_sig_gen.append(0x00000007, 32)
  mod_slow_sig_gen.append(0x00000007, 32)
  mod_slow_sig_gen.append(0x00000007, 32)
  mod_slow_sig_gen.append(0x00000007, 32)
  step_until_empty()
  expect(read_blocking(0x10) == 0, "Status - short pulse does not trigger")


  mod_slow_sig_gen.append(0x584d4443, 32)
  mod_slow_sig_gen.append(0, 32)
  mod_slow_sig_gen.append(0, 32)
  mod_slow_sig_gen.append(0, 32)
  step_until_empty()

  // check done
  expect(read_blocking(0x10) == 1, "Status done")

  // read data out and print
  val data_list = ListBuffer[BigInt]()

  for (i <- 0 until c.BUF_LEN) {
    val data = read_blocking(0x1000 + i * 4)
    data_list += data
  }

  for (data <- data_list) {
    val idx = data.toLong >> 20
    println(f"${idx}%5d | ${leftPad(data.toLong.toBinaryString)}")
  }

  data_list.clear()

  println("Simulation done")
}
