/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import bfmtester._
import chisel3._

import scala.collection.mutable.ListBuffer

class DecompressorInputAdapterReg(val w: Int, val addr_w: Int, val data_w: Int) extends Module {
  val io = IO(new Bundle {
    val from_axi = Flipped(new AxiMasterCoreReadIface(addr_w.W, data_w.W))
    val to_kernel = Flipped(new DecompressorKernelInputInterface(w))
  })

  val inst_dut = Module(new DecompressorInputAdapter(w, addr_w, data_w))
  io.from_axi.addr := inst_dut.io.from_axi.addr
  io.from_axi.len := inst_dut.io.from_axi.len
  io.from_axi.start := inst_dut.io.from_axi.start
  inst_dut.io.from_axi.data := RegNext(io.from_axi.data)
  inst_dut.io.from_axi.valid := RegNext(io.from_axi.valid)
  io.from_axi.ready := inst_dut.io.from_axi.ready

  io.to_kernel.en := inst_dut.io.to_kernel.en
  inst_dut.io.to_kernel.adv := RegNext(io.to_kernel.adv)
  inst_dut.io.to_kernel.adv_en := RegNext(io.to_kernel.adv_en)
  io.to_kernel.data := inst_dut.io.to_kernel.data
}


class DecompressorInputAdapterTest(c: DecompressorInputAdapterReg) extends BfmTester(c) {
  private def seq_to_bigint(xs: Seq[Byte]): BigInt = {
    var tmp: BigInt = 0
    for (x <- xs.reverse) {
      tmp <<= 8
      tmp |= x & 0xff
    }
    tmp
  }

  val DATA_LEN = 32
  val PKT_SIZE = 16 // = 128/8
  val mod_driver = new DecompressorInputAdapterDriver(c.io.from_axi, bfm_peek, bfm_poke, println)
  val mod_monitor =
    new DecompressorInputAdapterMonitor(
      c.io.to_kernel,
      this.rnd,
      DATA_LEN * (PKT_SIZE - 1),
      bfm_peek,
      bfm_poke,
      println
    )

  val exp_resp: ListBuffer[BigInt] = ListBuffer()
  for (y <- 0 until DATA_LEN) {
    val xs: Seq[Byte] = (0 until PKT_SIZE).map(x => (x | ((y & 0xf) << 4)).toByte)
    mod_driver.add_data(xs)
    exp_resp.append(seq_to_bigint(xs))
  }

  step(200)

  val resp: List[Byte] = mod_monitor.get_data()
  for (i <- 0 until DATA_LEN) {
    val xs = seq_to_bigint(resp.slice(i * PKT_SIZE, i * PKT_SIZE + PKT_SIZE))
    println(f"received data = ${xs}%x")
    expect(xs == exp_resp(i), "recv data should match expected data")
  }
}
