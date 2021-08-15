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
import chisel3.util._

class PcieEndpoint extends Module {
  val io = IO(new Bundle {
    val rx_st = new Interfaces.AvalonStreamRx
  })

  val mod_mem_read_write = Module(new MemoryReadWrite)
  val reg_mem_rw = Reg(Output(new Interfaces.AvalonStreamRx))
  mod_mem_read_write.io.rx_st.data := reg_mem_rw.data
  mod_mem_read_write.io.rx_st.sop := reg_mem_rw.sop
  mod_mem_read_write.io.rx_st.eop := reg_mem_rw.eop
  mod_mem_read_write.io.rx_st.empty := reg_mem_rw.empty
  mod_mem_read_write.io.rx_st.valid := reg_mem_rw.valid
  mod_mem_read_write.io.rx_st.err := reg_mem_rw.err
  mod_mem_read_write.io.rx_st.bar := reg_mem_rw.bar
  mod_mem_read_write.io.rx_st.be := reg_mem_rw.be
  mod_mem_read_write.io.rx_st.parity := reg_mem_rw.parity

  when(io.rx_st.valid) {
    val rx_data_hdr = WireInit(io.rx_st.data.asTypeOf(new CommonHdr))
    when(rx_data_hdr.fmt === Fmt.MRd32.asUInt() || rx_data_hdr.fmt === Fmt.MWr32.asUInt()) {
      reg_mem_rw := io.rx_st
    }.otherwise {
      reg_mem_rw.valid := false.B
    }
  }.otherwise {
    reg_mem_rw.valid := false.B
  }

  io.rx_st.ready := true.B
  io.rx_st.mask := false.B // we are always ready
}
