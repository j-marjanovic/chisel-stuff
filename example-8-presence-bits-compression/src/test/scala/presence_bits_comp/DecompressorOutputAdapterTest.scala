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

package presence_bits_comp

import bfmtester._
import chisel3._

class DecompressorOutputAdapterReg(val w: Int, val addr_w: Int, val data_w: Int) extends Module {
  val io = IO(new Bundle {
    val from_kernel = Flipped(new DecompressorKernelOutputInterface(w))
    val to_axi = Flipped(new AxiMasterCoreWriteIface(addr_w.W, data_w.W))
  })

  val mod_dut = Module(new DecompressorOutputAdapter(w, addr_w, data_w))

  mod_dut.io.from_kernel.data := RegNext(io.from_kernel.data)
  mod_dut.io.from_kernel.vld := RegNext(io.from_kernel.vld)
  io.from_kernel.ready := mod_dut.io.from_kernel.ready

  io.to_axi.addr := mod_dut.io.to_axi.addr
  io.to_axi.len := mod_dut.io.to_axi.len
  io.to_axi.start := mod_dut.io.to_axi.start
  io.to_axi.data := mod_dut.io.to_axi.data
  io.to_axi.valid := mod_dut.io.to_axi.valid
  mod_dut.io.to_axi.ready := RegNext(io.to_axi.ready)
}

class DecompressorOutputAdapterTest(c: DecompressorOutputAdapterReg) extends BfmTester(c) {

  val kernel_w: Int = c.w
  val axi_w: Int = c.data_w

  val mod_drv = new DecompressorOutputAdapterDriver(
    c.io.from_kernel,
    this.rnd,
    kernel_w,
    bfm_peek,
    bfm_poke,
    println
  )

  val mod_mon = new DecompressorOutputAdapterMonitor(
    c.io.to_axi,
    this.rnd,
    axi_w,
    kernel_w,
    bfm_peek,
    bfm_poke,
    println
  )

  step(100)

  val data = mod_mon.get_data()
  for ((d, i) <- data.zipWithIndex) {
    val recv_lo = d & 0xf
    val exp_lo = (i % kernel_w) + 1
    expect(recv_lo == exp_lo, f"lower nibble should match (${recv_lo}%x != ${exp_lo}%x)")

    val recv_hi = (d >> 4) & 0xf
    val exp_hi = (i / 8) & 0xf
    expect(recv_hi == exp_hi, f"higher nibble should match (${recv_hi}%x != ${exp_hi}%x)")
  }
}
