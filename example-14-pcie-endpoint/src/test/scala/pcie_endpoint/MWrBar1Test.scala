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

import chisel3.iotesters.PeekPokeTester

import scala.collection.mutable

class MWrBar1Test(c: PcieEndpoint) extends PeekPokeTester(c) {

  var deassert_waitreq = mutable.Queue[Long]()
  val wr_data = mutable.Queue[BigInt]()
  val wr_addr = mutable.Queue[BigInt]()

  def step_one(): Unit = {
    val read = peek(c.io.avmm_bar0.read) > 0
    val write = peek(c.io.avmm_bar0.write) > 0
    poke(c.io.avmm_bar0.waitrequest, 1)

    if (write || read) {
      deassert_waitreq.enqueue(t + 4)
    }

    if (deassert_waitreq.nonEmpty && deassert_waitreq.front == t) {
      deassert_waitreq.dequeue()
      poke(c.io.avmm_bar0.waitrequest, 0)
      if (write) {
        val data = peek(c.io.avmm_bar0.writedata)
        val addr = peek(c.io.avmm_bar0.address)
        println(f"${t} Avalon write, data = ${data}%08x, addr = ${addr}%08x")
        wr_addr.enqueue(addr)
        wr_data.enqueue(data)
      }
    }
  }

  override def step(n: Int): Unit = {
    for (_ <- 0 until n) {
      step_one()
      super.step(1)
    }
  }

  private def poke_rx(
      data: BigInt,
      sop: BigInt,
      eop: BigInt,
      empty: BigInt,
      valid: BigInt,
      err: BigInt,
      bar: BigInt,
      be: BigInt,
      parity: BigInt
  ): Unit = {
    poke(c.io.rx_st.data, data)
    poke(c.io.rx_st.sop, sop)
    poke(c.io.rx_st.eop, eop)
    poke(c.io.rx_st.empty, empty)
    poke(c.io.rx_st.valid, valid)
    poke(c.io.rx_st.err, err)
    poke(c.io.rx_st.bar, bar)
    poke(c.io.rx_st.be, be)
    poke(c.io.rx_st.parity, parity)
  }

  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0, parity = 0)
  step(1)
  poke_rx(data = 0, sop = 0, eop = 0, empty = 0, valid = 0, err = 0, bar = 0, be = 0, parity = 0)
  step(1)
  poke_rx(
    data = BigInt("6C671E65A35F5FC2DAA83AE60102030400000000D90000000000030F40000001", 16),
    sop = 1,
    eop = 1,
    empty = 1,
    valid = 1,
    err = 0,
    bar = 1,
    be = 0x000f0000,
    parity = 0xbe22f7f6
  )
  step(1)
  poke_rx(
    data = BigInt("6C671E65A35F5FC2DAA83AE60102030400000000D90000000000030F40000001", 16),
    sop = 0,
    eop = 0,
    empty = 0,
    valid = 0,
    err = 0,
    bar = 1,
    be = 0x000f0000,
    parity = 0xbe22f7f6
  )
  step(50)

  expect(wr_addr.length == 1, "received one address in a write transaction")
  expect(wr_data.length == 1, "received one datum in a write transaction")
  expect(wr_data.head == 0x01020304, "recvd data")
  expect(wr_addr.head == 0xd9000000L, "recvd addr")
}
