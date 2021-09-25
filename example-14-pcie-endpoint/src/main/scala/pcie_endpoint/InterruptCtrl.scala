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

import chisel3._

class InterruptCtrl extends Module {
  val io = IO(new Bundle {
    val app_int = new Interfaces.AppInt

    val conf_internal = Input(new Interfaces.ConfigIntern)

    val trigger = Input(Bool())
  })

  val msi_en = WireInit(io.conf_internal.msicsr(0))
  val reg_en = RegInit(false.B)

  val trigger_edge = WireInit(io.trigger && !RegNext(io.trigger))

  when(trigger_edge) {
    reg_en := true.B
  }.elsewhen((msi_en && io.app_int.msi_ack) || (!msi_en && io.app_int.ack)) {
    reg_en := false.B
  }

  io.app_int.sts := reg_en
  io.app_int.msi_req := msi_en && reg_en

  io.app_int.msi_num := 0.U
  io.app_int.msi_tc := 0.U

}
