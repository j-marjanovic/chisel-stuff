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

import chisel3._
import bfmtester._

class PresenceBitsDecompressorReg(
    val w: Int = 8,
    val addr_w: Int = 48,
    val data_w: Int = 128,
    val id_w: Int = 6
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W)
    val ctrl = new AxiLiteIf(8.W, 32.W)
  })

  val inst_dut = Module(new PresenceBitsDecompressor(w, addr_w, data_w, id_w))

  io.m.AW.bits.id := inst_dut.io.m.AW.bits.id
  io.m.AW.bits.addr := inst_dut.io.m.AW.bits.addr
  io.m.AW.bits.len := inst_dut.io.m.AW.bits.len
  io.m.AW.bits.size := inst_dut.io.m.AW.bits.size
  io.m.AW.bits.burst := inst_dut.io.m.AW.bits.burst
  io.m.AW.bits.lock := inst_dut.io.m.AW.bits.lock
  io.m.AW.bits.cache := inst_dut.io.m.AW.bits.cache
  io.m.AW.bits.prot := inst_dut.io.m.AW.bits.prot
  io.m.AW.bits.qos := inst_dut.io.m.AW.bits.qos
  io.m.AW.valid := inst_dut.io.m.AW.valid
  inst_dut.io.m.AW.ready := RegNext(io.m.AW.ready)

  io.m.AR.bits.id := inst_dut.io.m.AR.bits.id
  io.m.AR.bits.addr := inst_dut.io.m.AR.bits.addr
  io.m.AR.bits.len := inst_dut.io.m.AR.bits.len
  io.m.AR.bits.size := inst_dut.io.m.AR.bits.size
  io.m.AR.bits.burst := inst_dut.io.m.AR.bits.burst
  io.m.AR.bits.lock := inst_dut.io.m.AR.bits.lock
  io.m.AR.bits.cache := inst_dut.io.m.AR.bits.cache
  io.m.AR.bits.prot := inst_dut.io.m.AR.bits.prot
  io.m.AR.bits.qos := inst_dut.io.m.AR.bits.qos
  io.m.AR.valid := inst_dut.io.m.AR.valid
  inst_dut.io.m.AR.ready := RegNext(io.m.AR.ready)

  io.m.W.bits.id := inst_dut.io.m.W.bits.id
  io.m.W.bits.data := inst_dut.io.m.W.bits.data
  io.m.W.bits.strb := inst_dut.io.m.W.bits.strb
  io.m.W.bits.last := inst_dut.io.m.W.bits.last
  io.m.W.valid := inst_dut.io.m.W.valid
  inst_dut.io.m.W.ready := RegNext(io.m.W.ready)

  inst_dut.io.m.B.bits.id := RegNext(io.m.B.bits.id)
  inst_dut.io.m.B.bits.resp := RegNext(io.m.B.bits.resp)
  inst_dut.io.m.B.valid := RegNext(io.m.B.valid)
  io.m.B.ready := inst_dut.io.m.B.ready

  inst_dut.io.m.R.bits.id := RegNext(io.m.R.bits.id)
  inst_dut.io.m.R.bits.data := RegNext(io.m.R.bits.data)
  inst_dut.io.m.R.bits.resp := RegNext(io.m.R.bits.resp)
  inst_dut.io.m.R.bits.last := RegNext(io.m.R.bits.last)
  inst_dut.io.m.R.valid := RegNext(io.m.R.valid)
  io.m.R.ready := inst_dut.io.m.R.ready

  io.ctrl <> inst_dut.io.ctrl
}

class PresenceBitsDecompressorTest(c: PresenceBitsDecompressorReg) extends BfmTester(c) {
  // BFMs
  val mod_axi_master = BfmFactory.create_axilite_master(c.io.ctrl)
  val mod_axi_mem_slave = BfmFactory.create_axi_slave(c.io.m)

  // constants
  val READ_ADDR = 0x0a010000
  val WRITE_ADDR = 0x0a020000

  //==========================================================================
  // 1. populate the memory with a compressed image
  val filename: String = "/dense_matrix_with_sparse_els.npy"
  val orig_data: List[Byte] = CompressorDecompressor.read_from_file(getClass.getResource(filename))
  val comp_data: List[Byte] = CompressorDecompressor.compress(orig_data)
  val orig_len = orig_data.length
  val comp_len = comp_data.length

  println(s"Compressed ${filename.replace("/", "")}")
  println(s"  input length = ${orig_len}, output length = ${comp_len}")
  println(f"  compression ratio = ${orig_len.toFloat / comp_len}%.2f")

  // 1a. check if SW decompressor works
  val decomp_data: List[Byte] = CompressorDecompressor.decompress(comp_data)
  expect(orig_len == decomp_data.length, "data length remains the same")
  for ((orig, proc) <- orig_data.zip(decomp_data)) {
    expect(orig == proc, "data matches after comp and SW decomp")
  }

  // 1b. load the data into the memory
  val comp_data_with_zero_pad: List[Byte] = comp_data ++ List.fill[Byte](16)(0)
  mod_axi_mem_slave.mem_set(READ_ADDR, comp_data_with_zero_pad)
  mod_axi_mem_slave.mem_stats()

  // 2. prepare the write side
  // set addresses, lengths
  mod_axi_master.writePush(0x44, data = WRITE_ADDR)
  mod_axi_master.writePush(0x48, data = 0)
  mod_axi_master.writePush(0x4c, data = orig_len / (128 / 8) + 1)
  mod_axi_master.writePush(0x40, data = 1)
  step(50)
  for (i <- 0 until 4) {
    val wr_resp = mod_axi_master.getResponse().get
    expect(wr_resp.success, "write successful")
  }

  // 3. prepare the read side
  mod_axi_master.writePush(0x24, data = READ_ADDR)
  mod_axi_master.writePush(0x28, data = 0)
  mod_axi_master.writePush(0x2c, data = comp_len / (128 / 8) + 1)
  mod_axi_master.writePush(0x20, data = 1)
  step(50)
  for (i <- 0 until 4) {
    val wr_resp = mod_axi_master.getResponse().get
    expect(wr_resp.success, "write successful")
  }

  // 4. check the uncompressed data in the memory
  step(10000)
  mod_axi_mem_slave.mem_stats()

  var s: String = ""
  for (i <- 0 until orig_len) {
    val recv_b: Byte = mod_axi_mem_slave.mem_get_el(WRITE_ADDR + i)
    val exp_b: Byte = orig_data(i)
    /*
    s += f"${recv_b}%02x"
    if (i % 8 == 7) {
      println(s)
      s = ""
    }
     */
    expect(recv_b == exp_b, f"response (offs = ${i}, exp = ${exp_b}%x, recv = ${recv_b}%x)")
  }

}
