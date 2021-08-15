package pcie_endpoint

import chisel3._

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
  }

  class MemoryCmd extends Bundle {
    val valid = Output(Bool())

    // Memory transaction
    val address = Output(UInt(32.W))
    val byteenable = Output(UInt(8.W))
    val read_write_b = Output(Bool())
    val writedata = Output(UInt(64.W))

    // PCIe-related thing
    val pcie_req_id = Output(UInt(16.W))
    val pcie_tag = Output(UInt(8.W))
  }

  // TODO: cmd to generate completion for UR, ...

}
