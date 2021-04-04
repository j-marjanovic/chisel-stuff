/*
Copyright (c) 2018-2021 Jan Marjanovic

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

package poor_mans_system_ila

import chisel3._
import chisel3.util._

class DualPortRam(val RAM_WIDTH: Int, val RAM_DEPTH: Int)
    extends BlackBox(
      Map("RAM_WIDTH" -> RAM_WIDTH, "RAM_DEPTH" -> RAM_DEPTH)
    )
    with HasBlackBoxResource {

  val io = IO(new Bundle {
    val clk = Input(Clock())
    val addra = Input(UInt(log2Ceil(RAM_DEPTH).W))
    val dina = Input(UInt(RAM_WIDTH.W))
    val douta = Output(UInt(RAM_WIDTH.W))
    val wea = Input(Bool())
    val addrb = Input(UInt(log2Ceil(RAM_DEPTH).W))
    val dinb = Input(UInt(RAM_WIDTH.W))
    val doutb = Output(UInt(RAM_WIDTH.W))
    val web = Input(Bool())
  })

  addResource("/DualPortRam.v")

}
