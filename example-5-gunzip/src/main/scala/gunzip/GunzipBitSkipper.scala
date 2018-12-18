/*
MIT License

Copyright (c) 2018 Jan Marjanovic

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

package gunzip

import chisel3._
import chisel3.util._


/** an FPGA equivalent of /dev/null, but with limited length
  *
  * @param initial_bit_skip bits to skip before done
  */
class GunzipBitSkipper(val initial_bit_skip: Int) extends Module {
  val io = IO(new Bundle {
    val data_in_valid = Input(Bool())
    val data_in_ready = Output(Bool())
    val done = Output(Bool())
  })

  val BIT_CNTR_W = log2Ceil(initial_bit_skip + 1)
  val bit_cntr = RegInit(0.U(BIT_CNTR_W.W))

  when (io.data_in_ready && io.data_in_valid && bit_cntr < initial_bit_skip.U) {
    bit_cntr := bit_cntr + 1.U
  }

  io.done := bit_cntr === initial_bit_skip.U
  io.data_in_ready := bit_cntr =/= initial_bit_skip.U
}
