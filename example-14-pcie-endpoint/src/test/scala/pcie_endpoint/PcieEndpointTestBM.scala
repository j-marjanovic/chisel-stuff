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

class PcieEndpointTestBM(c: PcieEndpoint) extends BfmTester(c) {
  val bfm_avalon_agent = BfmFactory.create_avalon_slave(c.avmm_bar0, "BAR0")
  val bfm_avalon_st_tx = new AvalonStreamTxBfm(c.tx_st, bfm_peek, bfm_poke, println)
  val bfm_avalon_st_rx = new AvalonStreamRxBfm(c.rx_st, bfm_peek, bfm_poke, println)
  val bfm_avalon_st_data_in = new AvalonStreamDataInBfm(c.dma_in, bfm_peek, bfm_poke, println)
  val bfm_tl_config = new TLConfigBFM(c.tl_cfg, bfm_peek, bfm_poke, println, rnd)

  var tag = 1

  def read32(addr: Long, bar_idx: Int): Long = {
    bfm_avalon_st_rx.transmit_mrd32(
      PciePackets.MRd32(
        Addr30_2 = (0xd9000000L | addr) >> 2,
        ProcHint = 0,
        ReqID = 0x12,
        Tag = tag,
        LastBE = 0,
        FirstBE = 0,
        Fmt = PciePackets.Fmt.ThreeDw.id,
        Type = 0,
        rsvd2 = false,
        TrClass = 0,
        rsvd1 = false,
        Attr2 = 0,
        rsvd0 = false,
        TH = false,
        TD = false,
        EP = false,
        Attr1_0 = 0,
        AT = 0,
        Length = 1
      ),
      1 << bar_idx
    )
    step(30)

    val resp_valid = bfm_avalon_st_tx.recv_buffer.nonEmpty
    expect(resp_valid, "received response")

    if (resp_valid) {

      val cpld_raw = bfm_avalon_st_tx.recv_buffer.remove(0).data
      val cpld: PciePackets.CplD = PciePackets.to_cpld(cpld_raw)
      println(s"CplD = ${cpld}")
      expect(cpld.Tag == tag, "Tag in CplD")

      tag += 1
      if (tag > 31) tag = 1

      if ((addr & 0x7) == 4) {
        cpld.Dw0_unalign
      } else {
        cpld.Dw0
      }
    } else {
      -1
    }
  }

  def read64(addr: Long, bar_idx: Int): BigInt = {
    bfm_avalon_st_rx.transmit_mrd32(
      PciePackets.MRd32(
        Addr30_2 = (0xd9000000L | addr) >> 2,
        ProcHint = 0,
        ReqID = 0x12,
        Tag = tag,
        LastBE = 0,
        FirstBE = 0,
        Fmt = PciePackets.Fmt.ThreeDw.id,
        Type = 0,
        rsvd2 = false,
        TrClass = 0,
        rsvd1 = false,
        Attr2 = 0,
        rsvd0 = false,
        TH = false,
        TD = false,
        EP = false,
        Attr1_0 = 0,
        AT = 0,
        Length = 2
      ),
      1 << bar_idx
    )
    step(50)

    val resp_valid = bfm_avalon_st_tx.recv_buffer.nonEmpty
    expect(resp_valid, "received response")

    if (resp_valid) {
      val cpld_raw = bfm_avalon_st_tx.recv_buffer.remove(0).data
      val cpld: PciePackets.CplD = PciePackets.to_cpld(cpld_raw)
      println(s"CplD = ${cpld}")
      expect(cpld.Tag == tag, "Tag in CplD")

      tag += 1
      if (tag > 31) tag = 1

      if ((addr & 0x7) == 4) {
        (BigInt(cpld.Dw0) << 32) | cpld.Dw0_unalign
      } else {
        (BigInt(cpld.Dw1) << 32) | cpld.Dw0
      }
    } else {
      -1
    }
  }

  def write32(addr: Long, bar_idx: Int, data: Long): Unit = {
    bfm_avalon_st_rx.transmit_mwr32(
      PciePackets.MWr32(
        Dw1 = 0,
        Dw0 = if ((addr & 0x7) == 4) 0 else data,
        Dw0_unalign = if ((addr & 0x7) == 4) data else 0,
        Addr30_2 = (0xd9000000L | addr) >> 2,
        ProcHint = 0,
        ReqID = 0x12,
        Tag = tag,
        LastBE = 0,
        FirstBE = 0xf,
        Fmt = PciePackets.Fmt.ThreeDwData.id,
        Type = 0,
        rsvd2 = false,
        TrClass = 0,
        rsvd1 = false,
        Attr2 = 0,
        rsvd0 = false,
        TH = false,
        TD = false,
        EP = false,
        Attr1_0 = 0,
        AT = 0,
        Length = 1
      ),
      1 << bar_idx
    )
    step(30)
  }

  def write64(addr: Long, bar_idx: Int, data: BigInt): Unit = {
    val word0 = (data & 0xffffffffL).toLong
    val word1 = ((data >> 32) & 0xffffffffL).toLong

    bfm_avalon_st_rx.transmit_mwr32(
      PciePackets.MWr32(
        Dw1 = word1,
        Dw0 = if ((addr & 0x7) == 4) word1 else word0,
        Dw0_unalign = if ((addr & 0x7) == 4) word0 else 0,
        Addr30_2 = (0xd9000000L | addr) >> 2,
        ProcHint = 0,
        ReqID = 0x12,
        Tag = tag,
        LastBE = 0xf,
        FirstBE = 0xf,
        Fmt = PciePackets.Fmt.ThreeDwData.id,
        Type = 0,
        rsvd2 = false,
        TrClass = 0,
        rsvd1 = false,
        Attr2 = 0,
        rsvd0 = false,
        TH = false,
        TD = false,
        EP = false,
        Attr1_0 = 0,
        AT = 0,
        Length = 2
      ),
      1 << bar_idx
    )
    step(30)
  }

  // wait for the configuration to be propagated into the DUT
  step(200)

  val id_reg = read32(0, 2)
  expect(id_reg == 0xd3a01a2L, "ID reg")

  val version_reg = read32(4, 2)
  expect(version_reg >> 16 < 10, "version")

  val dec_err = read32(0xc, 2)
  expect(dec_err == 0xbadcaffeL, "empty register")

  //==========================================================================
  // short write
  write32(0x20, 2, 0xdf901000L)
  step(20)

  write32(0x24, 2, 0x7)
  step(20)

  write32(0x28, 2, 64)
  step(20)

  write32(0x2c, 2, 0x8000000aL)
  step(20)

  bfm_avalon_st_data_in.transmit_data(
    BigInt("000f000e000d000c000b000a0009000800070006000500040003000200010000", 16)
  )
  bfm_avalon_st_data_in.transmit_data(
    BigInt("001f001e001d001c001b001a0019001800170016001500140013001200110010", 16)
  )
  bfm_avalon_st_data_in.transmit_data(
    BigInt("002f002e002d002c002b002a0029002800270026002500240023002200210020", 16)
  )

  write32(0x14, 2, 1)
  step(50)

  var len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 3, "three sample captured - bus mastering write")
  if (len > 0) {
    val mwr_raw = bfm_avalon_st_tx.recv_buffer.remove(0)
    val mwr = PciePackets.to_mwr32(mwr_raw.data)
    println(f"MWr32 = ${mwr}")

    bfm_avalon_st_tx.recv_buffer.remove(0)
    bfm_avalon_st_tx.recv_buffer.remove(0)
  }

  expect(
    bfm_avalon_st_tx.recv_buffer.isEmpty,
    "the receive buffer is empty after the previous transaction"
  )

  //==========================================================================
  // short read
  write32(0x20, 2, 0xabcd1000L)
  step(20)

  write32(0x24, 2, 0x7)
  step(20)

  write32(0x28, 2, 4)
  step(20)

  write32(0x2c, 2, 0xb)
  step(20)

  write32(0x14, 2, 1)
  step(20)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured - bus mastering read")
  if (len > 0) {
    val mrd_raw = bfm_avalon_st_tx.recv_buffer.remove(0)
    val mrd = PciePackets.to_mrd64(mrd_raw.data)
    println(f"MRd64 = ${mrd}")
    expect(mrd.Addr63_32 == 7, "MRd - addr 64")
    expect((mrd.Addr30_2.toLong << 2) == 0xabcd1000L, "MRd - addr 32")
    expect(mrd.Length == 1, "MRd - length")
  }

  //==========================================================================
  // memory read, longer
  write32(0x20, 2, 0xabcd1000L)
  step(20)

  write32(0x24, 2, 0x7)
  step(20)

  write32(0x28, 2, 128)
  step(20)

  write32(0x2c, 2, 0xb)
  step(20)

  write32(0x14, 2, 1)
  step(20)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "1 sample captured (128 bytes) - bus mastering read")
  if (len > 0) {
    bfm_avalon_st_tx.recv_buffer.remove(0)
  }

  step(50)

  //==========================================================================
  // memory read, even longer
  write32(0x20, 2, 0xabcd1000L)
  step(20)

  write32(0x24, 2, 0x7)
  step(20)

  write32(0x28, 2, 1024)
  step(20)

  write32(0x2c, 2, 0xb)
  step(20)

  write32(0x14, 2, 1)
  step(20)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 4, "4 samples captured (1024 bytes) - bus mastering read")
  if (len > 0) {
    for (_ <- 0 until 4) {
      bfm_avalon_st_tx.recv_buffer.remove(0)
    }
  }

}
