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
import chisel3._




class Axi4ManagerTest(c: Axi4ManagerReg) extends BfmTester(c) {
  val mod_axi_mem = BfmFactory.create_axi_slave(c.io.m)

  step(10)

  poke(c.io.wr_cmd.data, 0xabcd1234L)
  poke(c.io.wr_cmd.addr, 0x40001020)
  poke(c.io.wr_cmd.valid, 1)
  step(1)
  poke(c.io.wr_cmd.data, 0)
  poke(c.io.wr_cmd.addr, 0)
  poke(c.io.wr_cmd.valid, 0)
  step(20)

  poke(c.io.rd_cmd.addr, 0x40001020)
  poke(c.io.rd_cmd.valid, 1)
  step(1)
  poke(c.io.rd_cmd.addr, 0)
  poke(c.io.rd_cmd.valid, 0)
  step(20)

  mod_axi_mem.mem_stats()
}
