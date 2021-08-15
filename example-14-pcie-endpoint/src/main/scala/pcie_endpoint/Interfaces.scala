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
    val parity = Input(UInt(32.W))
    // TODO: rxfc_cplbuf_ovf?
  }

  class AvalonMM extends Bundle {
    val address = Output(UInt(32.W))
    val byteenable = Output(UInt(4.W))
    val read = Output(Bool())
    val readdata = Input(UInt(32.W))
    val readdatavalid = Input(Bool())
    val response = Input(UInt(2.W))
    val write = Output(Bool())
    val writedata = Output(UInt(32.W))
    val waitrequest = Input(Bool())
  }

  class _MemoryCmd extends Bundle {
    // Memory transaction
    val address = UInt(32.W)
    val byteenable = UInt(8.W)
    val read_write_b = Bool()
    val writedata = UInt(64.W)

    // PCIe-related thing
    val pcie_req_id = UInt(16.W)
    val pcie_tag = UInt(8.W)
  }

  class MemoryCmd extends DecoupledIO(new _MemoryCmd) {
    override def cloneType: MemoryCmd.this.type = (new MemoryCmd).asInstanceOf[this.type]
  }

  //val MemoryCmd: DecoupledIO[_MemoryCmd] = chiselTypeOf(Decoupled(new _MemoryCmd))
  // TODO: cmd to generate completion for UR, ...

}
