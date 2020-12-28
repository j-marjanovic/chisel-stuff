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

import chisel3._
import chisel3.util._

class DecompressorOutputAdapter(val w: Int, val addr_w: Int, val data_w: Int) extends Module {
  val io = IO(new Bundle {
    val from_kernel = Flipped(new DecompressorKernelOutputInterface(w))
    val to_axi = Flipped(new AxiMasterCoreWriteIface(addr_w.W, data_w.W))
  })

  assert(w == 8, "only tested with the width of 8")

  // tie-off, not used in this module
  io.to_axi.addr := 0.U
  io.to_axi.len := 0.U
  io.to_axi.start := 0.U

  // input data
  val data_in_prev = RegEnable(io.from_kernel.data.asUInt(), 0.U, io.from_kernel.vld)
  val data_vld = RegInit(false.B)
  when (io.from_kernel.vld) {
    data_vld := !data_vld
  }

  // output data
  val data_out_vld = RegNext(data_vld && io.from_kernel.vld, false.B)
  val data_cat = Cat(io.from_kernel.data.asUInt(), data_in_prev)
  val data_out = RegEnable(data_cat, 0.U, data_vld && io.from_kernel.vld)

  io.to_axi.data := data_out
  io.to_axi.valid := data_out_vld

}
