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

object Interfaces {

  // directions as seen from the IP perspective
  class AvalonStreamRx extends Bundle {
    val data = Input(UInt(256.W))
    val sop = Input(Bool())
    val eop = Input(Bool())
    val empty = Input(UInt(2.W))
    val ready = Output(Bool())
    val valid = Input(Bool())
    val err = Input(Bool())

    // component specific
    val mask = Output(Bool())
    val bar = Input(UInt(8.W))
    val be = Input(UInt(32.W))
    // TODO: rxfc_cplbuf_ovf?
  }

  class AvalonStreamTx extends Bundle {
    val data = Output(UInt(256.W))
    val sop = Output(Bool())
    val eop = Output(Bool())
    val ready = Input(Bool())
    val valid = Output(Bool())
    val empty = Output(UInt(2.W))
    val err = Output(Bool())
  }

  class _MemoryCmd extends Bundle {
    // Memory transaction
    val address = UInt(32.W)
    val byteenable = UInt(8.W)
    val read_write_b = Bool()
    val writedata = UInt(64.W)
    val len = UInt(2.W)

    // PCIe-related thing
    val pcie_req_id = UInt(16.W)
    val pcie_tag = UInt(8.W)
  }

  class MemoryCmd extends DecoupledIO(new _MemoryCmd) {
    override def cloneType: MemoryCmd.this.type = (new MemoryCmd).asInstanceOf[this.type]
  }

  class _MemoryResp extends Bundle {
    val dw0 = UInt(32.W)
    val dw1 = UInt(32.W)
    val len = UInt(2.W)

    // PCIe-related thing
    val pcie_req_id = UInt(16.W)
    val pcie_tag = UInt(8.W)
    val pcie_lo_addr = UInt(7.W)
  }

  class MemoryResp extends DecoupledIO(new _MemoryResp) {
    override def cloneType: MemoryResp.this.type = (new MemoryResp).asInstanceOf[this.type]
  }

  // TODO: cmd to generate completion for UR, ...

  class TLConfig extends Bundle {
    val ctl = Input(UInt(32.W))
    val add = Input(UInt(4.W))
    val sts = Input(UInt(53.W))
  }

  class ConfigIntern extends Bundle {
    val busdev = UInt(12.W)
  }

  class DmaDesc extends Bundle {
    val addr32_0 = UInt(32.W)
    val addr63_32 = UInt(32.W)
    val len_bytes = UInt(32.W)
    val seq_nr = UInt(8.W)
  }
}
