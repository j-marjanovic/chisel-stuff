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

class PresenceBitsDecompressor(
    val w: Int = 8,
    val addr_w: Int = 48,
    val data_w: Int = 128,
    val id_w: Int = 6
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W)
    val ctrl = new AxiLiteIf(8.W, 32.W)
  })

  val VERSION: Int = 0x00000100

  val axi_read = Wire(new AxiMasterCoreReadIface(addr_w.W, data_w.W))
  val axi_write = Wire(new AxiMasterCoreWriteIface(addr_w.W, data_w.W))

  val kernel_in = Wire(new DecompressorKernelInputInterface(w))
  val kernel_out = Wire(new DecompressorKernelOutputInterface(w))

  val mod_axi = Module(new AxiMasterCore(addr_w, data_w, id_w))
  mod_axi.io.m <> io.m
  mod_axi.io.write <> axi_write
  mod_axi.io.read <> axi_read

  val mod_in_adapter = Module(new DecompressorInputAdapter(w, addr_w, data_w))
  mod_in_adapter.io.from_axi.data := axi_read.data
  mod_in_adapter.io.from_axi.valid := axi_read.valid
  axi_read.ready := mod_in_adapter.io.from_axi.ready
  mod_in_adapter.io.to_kernel <> kernel_in

  val mod_kernel = Module(new DecompressorKernel(w))
  mod_kernel.io.in <> kernel_in
  mod_kernel.io.out <> kernel_out

  val mod_out_adapter = Module(new DecompressorOutputAdapter(w, addr_w, data_w))
  mod_out_adapter.io.from_kernel <> kernel_out
  axi_write.data := mod_out_adapter.io.to_axi.data
  axi_write.valid := mod_out_adapter.io.to_axi.valid
  mod_out_adapter.io.to_axi.ready := axi_write.ready

  val mod_axi_slave = Module(
    new DecompressorAxiSlave(version = VERSION, addr_w = io.ctrl.addr_w.get)
  )
  mod_axi_slave.io.ctrl <> io.ctrl
  axi_write.addr := mod_axi_slave.io.write_addr
  axi_write.len := mod_axi_slave.io.write_len
  axi_write.start := mod_axi_slave.io.write_start

  axi_read.addr := mod_axi_slave.io.read_addr
  axi_read.len := mod_axi_slave.io.read_len
  axi_read.start := mod_axi_slave.io.read_start

}
