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
        FirstBE = 0xf,
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

    val resp_valid = bfm_avalon_st_tx.data.nonEmpty
    expect(resp_valid, "received response")

    if (resp_valid) {

      val cpld_raw = bfm_avalon_st_tx.data.remove(0)
      val cpld: PciePackets.CplD = PciePackets.to_cpld(cpld_raw)
      expect(cpld.Tag == tag, "Tag in CplD")

      tag += 1
      if (tag > 31) tag = 1
      cpld.Dw0
    } else {
      -1
    }
  }

  def write32(addr: Long, bar_idx: Int, data: Long): Unit = {
    bfm_avalon_st_rx.transmit_mwr32(
      PciePackets.MWr32(
        Dw1 = 0,
        Dw0 = data,
        Dw0_unalign = 0,
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

  // wait for the configuration to be propagated into the DUT
  step(200)

  val id_reg = read32(0, 2)
  expect(id_reg == 0xd3a01a2L, "ID reg")

  val version_reg = read32(4, 2)
  expect(version_reg == 0x100, "version")

  val dec_err = read32(0xc, 2)
  expect(dec_err == 0xbadcaffeL, "empty register")

  write32(0x10, 2, 2)
  write32(0x14, 2, 3)
  val q = read32(0x18, 2)
  expect(q == 5, "q = a + b")
}
