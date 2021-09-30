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

// processes MRd and MWr, forwards completion
class MemoryReadWriteCompl extends Module {
  val io = IO(new Bundle {
    val rx_st = new Interfaces.AvalonStreamRx

    // BAR 0
    val mem_cmd_bar0 = new Interfaces.MemoryCmd

    // BAR 2
    val mem_cmd_bar2 = new Interfaces.MemoryCmd

    // completion
    val cpld = new Bundle {
      val data = Output(UInt(256.W))
      val valid = Output(Bool())
      val sop = Output(Bool())
      val eop = Output(Bool())
    }
  })

  io.rx_st.ready := true.B
  io.rx_st.mask := false.B // we are always ready

  val reg_mem_cmd_bar0 = Reg(Valid(Output((new Interfaces.MemoryCmd).bits)))
  io.mem_cmd_bar0.bits := reg_mem_cmd_bar0.bits
  io.mem_cmd_bar0.valid := reg_mem_cmd_bar0.valid

  val reg_mem_cmd_bar2 = Reg(Valid(Output((new Interfaces.MemoryCmd).bits)))
  io.mem_cmd_bar2.bits := reg_mem_cmd_bar2.bits
  io.mem_cmd_bar2.valid := reg_mem_cmd_bar2.valid

  reg_mem_cmd_bar0.valid := false.B
  reg_mem_cmd_bar2.valid := false.B

  val reg_is_cpld = RegInit(false.B)
  val reg_is_cpld_single = RegInit(false.B)
  reg_is_cpld_single := false.B

  val reg_cpld_data = RegNext(io.rx_st.data)
  io.cpld.data := reg_cpld_data
  io.cpld.valid := reg_is_cpld_single || reg_is_cpld

  when(io.rx_st.valid) {
    val rx_data_hdr = WireInit(io.rx_st.data.asTypeOf(new CommonHdr))
    printf(p"MemoryReadWriteCompl: rx_data_hdr = $rx_data_hdr\n")
    when(rx_data_hdr.fmt === Fmt.MRd32.asUInt() && rx_data_hdr.typ === Typ.MRdMWr.asUInt()) {
      val mrd32 = io.rx_st.data.asTypeOf(new MRd32)
      printf(p"MemoryReadWriteCompl: mrd32 = $mrd32\n")
      when(io.rx_st.bar(0)) {
        reg_mem_cmd_bar0.valid := true.B
        reg_mem_cmd_bar0.bits.address := mrd32.addr << 2
        reg_mem_cmd_bar0.bits.byteenable := Cat(mrd32.last_be, mrd32.first_be)
        reg_mem_cmd_bar0.bits.read_write_b := true.B
        reg_mem_cmd_bar0.bits.len := mrd32.length
        reg_mem_cmd_bar0.bits.pcie_req_id := mrd32.req_id
        reg_mem_cmd_bar0.bits.pcie_tag := mrd32.tag
      }.elsewhen(io.rx_st.bar(2)) {
        reg_mem_cmd_bar2.valid := true.B
        reg_mem_cmd_bar2.bits.address := mrd32.addr << 2
        reg_mem_cmd_bar2.bits.byteenable := Cat(mrd32.last_be, mrd32.first_be)
        reg_mem_cmd_bar2.bits.read_write_b := true.B
        reg_mem_cmd_bar2.bits.len := mrd32.length
        reg_mem_cmd_bar2.bits.pcie_req_id := mrd32.req_id
        reg_mem_cmd_bar2.bits.pcie_tag := mrd32.tag
      }
    }.elsewhen(rx_data_hdr.fmt === Fmt.MWr32.asUInt() && rx_data_hdr.typ === Typ.MRdMWr.asUInt()) {
        val mwr32 = io.rx_st.data.asTypeOf(new MWr32)
        printf(p"MemoryReadWriteCompl: mwr32 = $mwr32\n")
        when(io.rx_st.bar(0)) {
          reg_mem_cmd_bar0.valid := true.B
          reg_mem_cmd_bar0.bits.address := mwr32.addr << 2
          reg_mem_cmd_bar0.bits.byteenable := Cat(mwr32.last_be, mwr32.first_be)
          reg_mem_cmd_bar0.bits.read_write_b := false.B
          reg_mem_cmd_bar0.bits.len := mwr32.length
          when(mwr32.addr(0)) {
            reg_mem_cmd_bar0.bits.writedata := Cat(mwr32.dw0, mwr32.dw0_unalign)
          }.otherwise {
            reg_mem_cmd_bar0.bits.writedata := Cat(mwr32.dw1, mwr32.dw0)
          }
          reg_mem_cmd_bar0.bits.pcie_req_id := mwr32.req_id
          reg_mem_cmd_bar0.bits.pcie_tag := mwr32.tag
        }.elsewhen(io.rx_st.bar(2)) {
          reg_mem_cmd_bar2.valid := true.B
          reg_mem_cmd_bar2.bits.address := mwr32.addr << 2
          reg_mem_cmd_bar2.bits.byteenable := Cat(mwr32.last_be, mwr32.first_be)
          reg_mem_cmd_bar2.bits.read_write_b := false.B
          reg_mem_cmd_bar2.bits.len := mwr32.length
          when(mwr32.addr(0)) {
            reg_mem_cmd_bar2.bits.writedata := Cat(mwr32.dw0, mwr32.dw0_unalign)
          }.otherwise {
            reg_mem_cmd_bar2.bits.writedata := Cat(mwr32.dw1, mwr32.dw0)
          }
          reg_mem_cmd_bar2.bits.pcie_req_id := mwr32.req_id
          reg_mem_cmd_bar2.bits.pcie_tag := mwr32.tag
        }
        // TODO: handle other BARs - probably an error of some kind
      }
      .elsewhen(rx_data_hdr.fmt === Fmt.MWr32.asUInt() && rx_data_hdr.typ === Typ.Cpl.asUInt()) {
        when(io.rx_st.eop) {
          reg_is_cpld_single := true.B
        }.otherwise {
          reg_is_cpld := true.B
        }
      }
      .otherwise {
        printf(p"MemoryReadWrite: unrecognized hdr = $rx_data_hdr\n")
      }
  }

  when(reg_is_cpld && io.rx_st.valid && io.rx_st.eop) {
    reg_is_cpld := false.B
  }

  io.cpld.sop := RegNext(io.rx_st.sop)
  io.cpld.eop := RegNext(io.rx_st.eop)

}