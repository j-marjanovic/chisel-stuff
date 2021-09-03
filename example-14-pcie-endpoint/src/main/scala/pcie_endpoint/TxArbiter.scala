package pcie_endpoint

import chisel3._

class TxArbiter extends Module {
  val io = IO(new Bundle {
    val tx_st = new Interfaces.AvalonStreamTx

    val cpld = Flipped(new Interfaces.AvalonStreamTx)
    val bm = Flipped(new Interfaces.AvalonStreamTx)

  })

  io.tx_st <> io.cpld

  io.bm.ready := false.B

  // TODO: implement mux

}
