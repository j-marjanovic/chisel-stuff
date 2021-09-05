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

class PcieEndpointSimple64Test(c: PcieEndpoint) extends BfmTester(c) {
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

  // write 0xaa to 0x10
  println("=======================================================================")

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("FFDD87BDFFB9D572CEB2950B000000AA00000000DF8C00100000020F40000001", 16),
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
    data = BigInt("FFDD87BDFFB9D572CEB2950B000000AA00000000DF8C00100000020F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  // read from 0x10, expect 0xaa
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("00000000000100040F0018000100000400000000DF8C00100018000F00000001", 16),
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
    data = BigInt("00000000000100040F0018000100000400000000DF8C00100018000F00000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  var len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured - read from 0x10")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000000AA0000000000180010040000044A000001",
        16
      ),
      "resp to read from 0x10, expect 0xaa"
    )
  }

  // write 0xbb to 0x14
  println("=======================================================================")

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("72DF55CE4E66133C32E31D472F032F0F000000BBDF8C00140000000F40000001", 16),
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
    data = BigInt("72DF55CE4E66133C32E31D472F032F0F000000BBDF8C00140000000F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  // read from 0x14, expect 0xbb
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("000000008C0000040F0018000100000400000000DF8C00140018000F00000001", 16),
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
    data = BigInt("000000008C0000040F0018000100000400000000DF8C00140018000F00000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured - read from 0x14")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000000BB00180014040000044A000001",
        // bb00180014040000044a000001
        16
      ),
      "resp to read from 0x14, expect 0xbb"
    )
  }

  // write 0xcc to 0x18
  println("=======================================================================")

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("C1FDC64B8F4B79FABDE83137000000CC00000000DF8C00180000010F40000001", 16),
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
    data = BigInt("C1FDC64B8F4B79FABDE83137000000CC00000000DF8C00180000010F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  // read from 0x18, expect 0xcc
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("000000000C000004040018000100000400000000DF8C00180018000F00000001", 16),
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
    data = BigInt("000000000C000004040018000100000400000000DF8C00180018000F00000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured - read from 0x18")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000000CC0000000000180018040000044A000001",
        16
      ),
      "resp to read from 0x1c, expect 0xcc"
    )
  }

  // read from 0x10 (two dwords), expect 0xbb000000aa
  println("=======================================================================")

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("0000000000020004030018000100000400000000DF8C0010001800FF00000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0000000000020004030018000100000400000000DF8C0010001800FF00000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000000bb000000AA0000000000180010040000084A000002",
        16
      ),
      "resp to read from 0x10, expect 0xbb000000aa"
    )
  }

  // read from 0x14 (two dwords), expect 0xcc000000bb
  println("=======================================================================")

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("0000000010008CDF0F0018000100000000000000DF8C0014001800FF00000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0000000010008CDF0F0018000100000000000000DF8C0014001800FF00000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "cc000000BB00180014040000084A000002",
        16
      ),
      "resp to read from 0x14, expect 0xbb000000aa"
    )
  }

  println("=======================================================================")

  // 0x10 d 0x2200000011
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("7A6ED5E700000000000000220000001100000000DF8C0010000002FF40000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0x00ff0000
  )
  step(1)
  poke_rx(
    data = BigInt("7A6ED5E700000000000000220000001100000000DF8C0010000002FF40000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x00ff0000
  )
  step(50)

  // read from 0x10 (two dwords), expect 0x2200000011
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("0000000014008CDF0F0018000100000000000000DF8C0010001800FF00000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0000000014008CDF0F0018000100000000000000DF8C0010001800FF00000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "0000000000000022000000110000000000180010040000084A000002",
        // 110000000000180010040000084a000002
        16
      ),
      "resp to read from 0x1c, expect 0xcc"
    )
  }

  println("=======================================================================")

  //  0x14 d 0x5500000044
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("F4BF7D6F9F2324AB000000000000005500000044DF8C0014000000FF40000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0x00ff0000
  )
  step(1)
  poke_rx(
    data = BigInt("F4BF7D6F9F2324AB000000000000005500000044DF8C0014000000FF40000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x00ff0000
  )
  step(50)

  // read from 0x10 (two dwords), expect 0x2200000011
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0)
  step(1)
  poke_rx(
    data = BigInt("0000000010008CDFFF0018000200000000000000DF8C0014001800FF00000002", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0
  )
  step(1)
  poke_rx(
    data = BigInt("0000000010008CDFFF0018000200000000000000DF8C0014001800FF00000002", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0
  )
  step(50)

  len = bfm_avalon_st_tx.recv_buffer.length
  expect(len == 1, "one sample captured")
  if (len > 0) {
    val data = bfm_avalon_st_tx.recv_buffer.remove(0)
    expect(
      data.data == BigInt(
        "000000000550000004400180014040000084A000002",
        16
      ),
      "resp to read from 0x1c, expect 0xcc"
    )
  }
}
