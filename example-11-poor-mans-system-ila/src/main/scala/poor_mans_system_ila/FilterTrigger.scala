package poor_mans_system_ila

import chisel3._
import chisel3.util._

class FilterTrigger extends Module {
  val io = IO(new Bundle {
    val data_in = Input(Bool())
    val level = Input(UInt(12.W))
    val trigger_out = Output(Bool())
    val debug_q = Output(UInt(12.W))
  })

  val q = RegInit(UInt(20.W), 0.U)
  val Q_MAX: UInt = (Math.pow(2.0, q.getWidth.toFloat).toInt - 1).U
  val C_INCR : UInt = 200.U
  val C_DECR : UInt = 1.U

  val data_in_reg = RegNext(io.data_in)
  val data_in_reg_p = RegNext(data_in_reg)

  val data_edge = WireInit(data_in_reg ^ data_in_reg_p)

  when (data_edge && q <= (Q_MAX - C_INCR)) {
    q := q + C_INCR
  } .elsewhen (!data_edge && q >= C_DECR) {
    q := q - C_DECR
  }

  val trig_out = RegNext(q(19, 7) > io.level)
  val trig_out_p = RegNext(trig_out)

  when (!trig_out_p && trig_out) {
    io.trigger_out := true.B
  } .otherwise {
    io.trigger_out := false.B
  }
  io.debug_q := q(19, 7)
}
