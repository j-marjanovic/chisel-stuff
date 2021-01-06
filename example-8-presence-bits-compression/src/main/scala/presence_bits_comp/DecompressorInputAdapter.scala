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

class DecompressorInputAdapter(val w: Int, val addr_w: Int, val data_w: Int) extends Module {
  val io = IO(new Bundle {
    val from_axi = Flipped(new AxiMasterCoreReadIface(addr_w.W, data_w.W))
    val to_kernel = Flipped(new DecompressorKernelInputInterface(w))
  })

  // tie-off, not used in this module
  io.from_axi.addr := 0.U
  io.from_axi.len := 0.U
  io.from_axi.start := 0.U

  // queue input
  val queue_in = Wire(Decoupled(UInt(io.from_axi.data.getWidth.W)))
  queue_in.bits := io.from_axi.data
  queue_in.valid := io.from_axi.valid

  // queue
  val queue = Queue(queue_in, 16, pipe = true)
  queue.nodeq()
  // assert((queue_in.valid && queue_in.ready) || (!queue_in.valid))
  io.from_axi.ready := queue_in.ready

  // queue output
  val queue_out = WireInit(queue.bits)
  val queue_out_prev = RegEnable(queue_out, queue.fire())
  val queue_out_comb = Cat(queue_out, queue_out_prev)
  val queue_out_vec = Wire(Vec(data_w / w * 2, UInt(w.W)))
  queue_out_vec := queue_out_comb.asTypeOf(queue_out_vec)

  // output sel
  val sel = RegInit(UInt((log2Ceil(data_w / 8) + 2).W), 16.U)
  for (i <- 0 to w) {
    io.to_kernel.data(i) := queue_out_vec(sel + i.U)
  }

  // select advance
  when(io.to_kernel.adv_en) {
    when((sel + io.to_kernel.adv >= 24.U) && queue.valid) {
      sel := sel + io.to_kernel.adv - 16.U
      queue.deq()
    }.otherwise {
      sel := sel + io.to_kernel.adv
    }
  }

  // kernel enable
  val kernel_en_reg = RegInit(false.B)
  io.to_kernel.en := kernel_en_reg
  when(queue.valid) {
    kernel_en_reg := true.B
  }.elsewhen(sel + io.to_kernel.adv >= 32.U) {
    kernel_en_reg := false.B
  }
}
