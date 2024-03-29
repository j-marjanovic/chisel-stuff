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

class PcieEndpointTest(c: PcieEndpoint) extends BfmTester(c) {
  val bfm_avalon_agent = BfmFactory.create_avalon_slave(c.avmm_bar0, "BAR0")
  val bfm_avalon_st_tx = new AvalonStreamTxBfm(c.tx_st, bfm_peek, bfm_poke, println)
  val bfm_avalon_st_rx = new AvalonStreamRxBfm(c.rx_st, bfm_peek, bfm_poke, println)
  val bfm_tl_config = new TLConfigBFM(c.tl_cfg, bfm_peek, bfm_poke, println, rnd)

  // wait for the configuration to be propagated into the DUT
  step(200)

  bfm_avalon_st_rx.transmit_mwr32(
    PciePackets.MWr32(
      Dw1 = 0,
      Dw0 = 0xaabbccddL,
      Dw0_unalign = 0,
      Addr30_2 = 0xd9000220L >> 2,
      ProcHint = 0,
      ReqID = 0x12,
      Tag = 10,
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
    1
  )
  step(30)

  bfm_avalon_st_rx.transmit_mrd32(
    PciePackets.MRd32(
      Addr30_2 = 0xd9000220L >> 2,
      ProcHint = 0,
      ReqID = 0x12,
      Tag = 11,
      LastBE = 0xf,
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
    1
  )

  step(30)
  val cpld_raw = bfm_avalon_st_tx.recv_buffer.remove(0).data
  val cpld: PciePackets.CplD = PciePackets.to_cpld(cpld_raw)
  println(f"CplD = ${cpld_raw}%x, $cpld, ${cpld.Dw0}%x")
  expect(cpld.Dw0 == 0xaabbccddL, "dw0 in CplD")
  println(f"cpld.ComplID = ${cpld.ComplID}%x, cpld.ReqID = ${cpld.ReqID}%x")
  expect(cpld.ComplID == 4 << 8, "ComplID in CplD")
  expect(cpld.ReqID == 0x12, "ReqID in CplD")
  expect(cpld.LoAddr == 0x20, "LoAddr in CplD")

}
