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

import scala.language.implicitConversions
import chisel3._
import chisel3.util._
import bfmtester._

class AxiMasterCore extends Module {
  //==========================================================================
  val addr_w = 48
  val data_w = 128
  val id_w = 6

  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W)
    val read = new AxiMasterCoreReadIface(addr_w.W, data_w.W)
    val write = new AxiMasterCoreWriteIface(addr_w.W, data_w.W)

    val cntr_rd_resp_okay = Output(UInt(32.W))
    val cntr_rd_resp_exokay = Output(UInt(32.W))
    val cntr_rd_resp_slverr = Output(UInt(32.W))
    val cntr_rd_resp_decerr = Output(UInt(32.W))

    val cntr_wr_resp_okay = Output(UInt(32.W))
    val cntr_wr_resp_exokay = Output(UInt(32.W))
    val cntr_wr_resp_slverr = Output(UInt(32.W))
    val cntr_wr_resp_decerr = Output(UInt(32.W))
  })

  //==========================================================================
  // tie-off for id signals
  io.m.AR.bits.id := 0.U
  io.m.AW.bits.id := 0.U
  io.m.W.bits.id := 0.U

  // tie-off for read addr
  io.m.AR.bits.size := log2Ceil(io.m.R.bits.data.getWidth / 8).U
  io.m.AR.bits.burst := AxiIfBurstType.BURST
  io.m.AR.bits.lock := 0.U
  io.m.AR.bits.cache := AxiIfAxCache(
    bufferable = false,
    cacheable = false,
    readalloc = false,
    writealloc = false
  )
  io.m.AR.bits.prot := 0.U
  io.m.AR.bits.qos := 0.U

  // tie-off for write addr
  io.m.AW.bits.size := log2Ceil(io.m.R.bits.data.getWidth / 8).U
  io.m.AW.bits.burst := AxiIfBurstType.BURST
  io.m.AW.bits.lock := 0.U
  io.m.AW.bits.cache := AxiIfAxCache(
    bufferable = false,
    cacheable = false,
    readalloc = false,
    writealloc = false
  )
  io.m.AW.bits.prot := 0.U
  io.m.AW.bits.qos := 0.U

  //==========================================================================
  val axi_reader = Module(new AxiMasterCoreReader(addr_w, data_w, id_w))
  io.m.AR.bits.addr := axi_reader.io.araddr
  io.m.AR.bits.len := axi_reader.io.arlen
  io.m.AR.valid := axi_reader.io.arvalid
  axi_reader.io.arready := io.m.AR.ready

  axi_reader.io.rid := io.m.R.bits.id
  axi_reader.io.rdata := io.m.R.bits.data
  axi_reader.io.rresp := io.m.R.bits.resp
  axi_reader.io.rlast := io.m.R.bits.last
  axi_reader.io.rvalid := io.m.R.valid
  io.m.R.ready := axi_reader.io.rready

  axi_reader.io.read_addr := io.read.addr
  axi_reader.io.read_len := io.read.len
  axi_reader.io.read_start := io.read.start
  io.read.data := axi_reader.io.out_data
  io.read.valid := axi_reader.io.out_valid

  io.cntr_rd_resp_okay := axi_reader.io.cntr_resp_okay
  io.cntr_rd_resp_exokay := axi_reader.io.cntr_resp_exokay
  io.cntr_rd_resp_slverr := axi_reader.io.cntr_resp_slverr
  io.cntr_rd_resp_decerr := axi_reader.io.cntr_resp_decerr

  //==========================================================================
  val axi_writer = Module(new AxiMasterCoreWriter(addr_w, data_w, id_w))
  io.m.AW.bits.addr := axi_writer.io.awaddr
  io.m.AW.bits.len := axi_writer.io.awlen
  io.m.AW.valid := axi_writer.io.awvalid
  axi_writer.io.awready := io.m.AW.ready

  io.m.W.bits.data := axi_writer.io.wdata
  io.m.W.bits.strb := axi_writer.io.wstrb
  io.m.W.bits.last := axi_writer.io.wlast
  io.m.W.valid := axi_writer.io.wvalid
  axi_writer.io.wready := io.m.W.ready

  axi_writer.io.bresp := io.m.B.bits.resp
  axi_writer.io.bvalid := io.m.B.valid
  io.m.B.ready := axi_writer.io.bready

  axi_writer.io.write_addr := io.write.addr
  axi_writer.io.write_len := io.write.len
  axi_writer.io.write_start := io.write.start
  axi_writer.io.in_data := io.write.data
  axi_writer.io.in_valid := io.write.valid
  io.write.ready := axi_writer.io.in_ready

  io.cntr_wr_resp_okay := axi_writer.io.cntr_resp_okay
  io.cntr_wr_resp_exokay := axi_writer.io.cntr_resp_exokay
  io.cntr_wr_resp_slverr := axi_writer.io.cntr_resp_slverr
  io.cntr_wr_resp_decerr := axi_writer.io.cntr_resp_decerr

}
