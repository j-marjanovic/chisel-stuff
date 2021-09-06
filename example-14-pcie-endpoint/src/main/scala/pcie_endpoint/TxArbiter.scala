package pcie_endpoint

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class TxArbiter extends Module {
  val io = IO(new Bundle {
    val tx_st = new Interfaces.AvalonStreamTx

    val cpld = Flipped(new Interfaces.AvalonStreamTx)
    val bm = Flipped(new Interfaces.AvalonStreamTx)

  })

  object State extends ChiselEnum {
    val sIdle, sCpld, sBm = Value
  }

  val state = RegInit(State.sIdle)

  switch(state) {
    is(State.sIdle) {
      when(io.cpld.valid) {
        state := State.sCpld
      }.elsewhen(io.bm.valid) {
        state := State.sBm
      }
    }
    is(State.sCpld) {
      when(io.cpld.valid && io.cpld.eop) {
        state := State.sIdle
      }
    }
    is(State.sBm) {
      when(io.bm.valid && io.bm.eop) {
        state := State.sIdle
      }
    }
  }

  io.tx_st := DontCare
  io.cpld.ready := false.B
  io.bm.ready := false.B
  io.tx_st.valid := false.B

  switch(state) {
    is(State.sCpld) {
      io.tx_st <> io.cpld
    }
    is(State.sBm) {
      io.tx_st <> io.bm
    }
  }
}
