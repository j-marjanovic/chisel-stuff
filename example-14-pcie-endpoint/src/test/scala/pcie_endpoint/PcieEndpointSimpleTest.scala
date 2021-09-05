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
import bfmtester._

import scala.util.Random

class PcieEndpointSimpleTest(c: PcieEndpoint) extends BfmTester(c) {
  val bfm_avalon_agent = BfmFactory.create_avalon_slave(c.avmm_bar0, "BAR0")
  val bfm_avalon_st_tx = new AvalonStreamTxBfm(c.tx_st, bfm_peek, bfm_poke, println)
  val bfm_tl_config = new TLConfigBFM(c.tl_cfg, bfm_peek, bfm_poke, println, rnd)

  private def poke_rx(
      data: BigInt,
      sop: BigInt,
      eop: BigInt,
      empty: BigInt,
      valid: BigInt,
      err: BigInt,
      bar: BigInt,
      be: BigInt
  ): Unit = {
    poke(c.rx_st.data, data)
    poke(c.rx_st.sop, sop)
    poke(c.rx_st.eop, eop)
    poke(c.rx_st.empty, empty)
    poke(c.rx_st.valid, valid)
    poke(c.rx_st.err, err)
    poke(c.rx_st.bar, bar)
    poke(c.rx_st.be, be)
  }

  //
  step(200)

  // write

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("56F7C9CC639A671FE7C8C4160000010000000000D90001000000020F40000001", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(1)
  poke_rx(
    data = BigInt("56F7C9CC639A671FE7C8C4160000010000000000D90001000000020F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  expect(bfm_avalon_agent.mem_get_word(0xd9000100L, 4) == 0x100, "recvd data")

  // read
  poke_rx(
    data = BigInt("00000000000000D90F0018000100000000000000D90001000018000F00000001", 16),
    sop = 1,
    eop = 1,
    empty = 2,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(1)
  poke_rx(
    data = BigInt("00000000000000D90F0018000100000000000000D90001000018000F00000001", 16),
    sop = 1,
    eop = 1,
    empty = 2,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  var len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000001000000000000180000040000044A000001",
        16
      ),
      "reference data"
    )
  }

  bfm_avalon_agent.mem_stats()

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("00000000000300040F0018000100000400000000DF8C00140018000F00000001", 16),
    sop = 1,
    eop = 1,
    empty = 2,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0x000f0000
  )
  step(1)
  poke_rx(
    data = BigInt("00000000000300040F0018000100000400000000DF8C00140018000F00000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0x000f0000
  )
  step(50)

  // 00000000000300040F0018000100000400000000DF8C00140018000F00000001
  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(data.empty == 2, "empty for non-aligned read")
  }

  // write 0x7B to address 0x14
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("F4BF7D6F990324AFBDFE6AED0377F1E60000007BDF8C00140000010F40000001", 16),
    sop = 1,
    eop = 1,
    empty = 2,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0x000f0000
  )
  step(1)
  poke_rx(
    data = BigInt("F4BF7D6F990324AFBDFE6AED0377F1E60000007BDF8C00140000010F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0x000f0000
  )
  step(50)

  // read from 0x14
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("0000000008008CDF0F0018000100000000000000DF8C00140018000F00000001", 16),
    sop = 1,
    eop = 1,
    empty = 2,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0000000008008CDF0F0018000100000000000000DF8C00140018000F00000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(data.empty == 2, "empty for non-aligned read")
    val cpld: PciePackets.CplD = PciePackets.to_cpld(data.data)
    println(f"CplD = ${cpld}, data = ${cpld.Dw0_unalign}%x")
    expect(cpld.Dw0_unalign == 0x7b, "readback after write")
  }

}
