package poor_mans_system_ila

import chisel3._
import chisel3.util._

class CorrelatorChannel(val pattern: UInt) extends Module {

  val t = UInt((log2Up(pattern.getWidth)+2).W)

  val io = IO(new Bundle {
    val inp = Input(Bool())
    val out = Output(t)
    val out_valid = Output(Bool())
  })

  val pos = RegInit(t, 0.U)
  val accum = RegInit(t, 0.U)
  val out = RegInit(t, 0.U)
  val out_valid = RegInit(Bool(), false.B)

  io.out := out
  io.out_valid := out_valid

  when (pos === (pattern.getWidth-1).U) {
    out := accum
    out_valid := true.B
    pos := 0.U
  } .otherwise {
    out_valid := false.B
    pos := pos + 1.U
  }

  val inp_pat_match = Wire(UInt(2.W)) // Init(io.inp === pattern(pos))
  inp_pat_match := 0.U
  when (io.inp === pattern(pos)) {
    when (io.inp === true.B) {
      inp_pat_match := 2.U
    } .otherwise {
      inp_pat_match := 1.U
    }
  }

  when (out_valid) {
    accum := inp_pat_match
  } .otherwise {
    accum := accum + inp_pat_match
  }
}

class CorrelatorBank extends Module {
  val io = IO(new Bundle {

  })

}
