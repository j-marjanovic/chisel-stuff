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

import bfmtester._

class PcieEndpoint64bInterfaceSimpleTest(c: PcieEndpoint) extends BfmTester(c) {
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

  //==========================================================================
  // read from addr 0
  poke_rx(
    data = BigInt("001A080F00000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0000", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0000", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0
  )
  step(50)

  var len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 3, "three sample captured - bus mastering read from qword-aligned address")
  var tmp = BigInt(0)

  for (i <- len - 1 to 0 by -1) {
    val cpld_raw: bfm_avalon_st_tx.RecvData = bfm_avalon_st_tx.recv_buffer.remove(i)
    tmp <<= 64
    tmp |= cpld_raw.data
  }

  println(f"tmp ${tmp}%x ")
  var cpld = PciePackets.to_cpld(tmp)
  println(s"cpld = ${cpld}")
  expect(cpld.Dw0 == 0xd3a01a2L, "id reg")

  //==========================================================================
  // read from addr 4
  poke_rx(
    data = BigInt("001A080F00000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0004", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0004", 16),
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
  expect(len == 2, "two sample captured - bus mastering read from non-qword-aligned address")

  for (i <- len - 1 to 0 by -1) {
    val cpld_raw: bfm_avalon_st_tx.RecvData = bfm_avalon_st_tx.recv_buffer.remove(i)
    tmp <<= 64
    tmp |= cpld_raw.data
  }

  println(f"tmp ${tmp}%x ")
  cpld = PciePackets.to_cpld(tmp)
  println(s"cpld = ${cpld}")
  expect(cpld.Dw0_unalign < 0xa0000, "version")

  //==========================================================================
  // write to 0x8
  poke_rx(
    data = BigInt("00000B0F40000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("DDCCBBAADF8C0008", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("E3AFD64BAABBCCDD", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("E3AFD64BAABBCCDD", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0
  )
  step(50)

  //==========================================================================
  // write to 0x20
  poke_rx(
    data = BigInt("0000090F40000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("11223344DF8C0020", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0975F1E444332211", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0975F1E444332211", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0
  )

  step(50)

  //==========================================================================
  // read from addr 0x24
  poke_rx(
    data = BigInt("001A080F00000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0024", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0000", 16),
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
  expect(len == 2, "two sample captured - bus mastering read from non-qword-aligned address")
  tmp = BigInt(0)

  for (i <- len - 1 to 0 by -1) {
    val cpld_raw: bfm_avalon_st_tx.RecvData = bfm_avalon_st_tx.recv_buffer.remove(i)
    tmp <<= 64
    tmp |= cpld_raw.data
  }

  println(f"tmp ${tmp}%x ")
  cpld = PciePackets.to_cpld(tmp)
  println(s"cpld = ${cpld}")
  expect(cpld.Dw0_unalign == 0, "reg addr63_32 before write")

  //==========================================================================
  // write to 0x24
  poke_rx(
    data = BigInt("00000A0F40000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("88776655DF8C0024", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("88776655DF8C0024", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 4,
    be = 0
  )
  step(50)

  //==========================================================================
  // read from addr 0x24
  poke_rx(
    data = BigInt("001A080F00000001", 16),
    sop = 1,
    eop = 0,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0024", 16),
    sop = 0,
    eop = 1,
    empty = 0,
    valid = 1,
    err = 0,
    bar = 4,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("00000000DF8C0000", 16),
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
  expect(len == 2, "two sample captured - bus mastering read from non-qword-aligned address")
  tmp = BigInt(0)

  for (i <- len - 1 to 0 by -1) {
    val cpld_raw: bfm_avalon_st_tx.RecvData = bfm_avalon_st_tx.recv_buffer.remove(i)
    tmp <<= 64
    tmp |= cpld_raw.data
  }

  println(f"tmp ${tmp}%x ")
  cpld = PciePackets.to_cpld(tmp)
  println(s"cpld = ${cpld}")
  expect(cpld.Dw0_unalign == 0x88776655L, "reg addr63_32 after write")

}
