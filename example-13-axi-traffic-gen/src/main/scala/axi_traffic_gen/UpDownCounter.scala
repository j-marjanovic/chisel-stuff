package axi_traffic_gen

import chisel3._
import chisel3.util._

class UpDownCounter private (r: Range, inc: Bool, dec: Bool) {
  require(r.nonEmpty, s"Counter range cannot be empty, got: $r")
  require(r.start >= 0 && r.end >= 0, s"Counter range must be positive, got: $r")
  require(r.step == 1, s"Counter step must be 1, got: $r")

  private lazy val width = math.max(log2Up(r.last + 1), log2Up(r.head + 1))

  val value = RegInit(r.head.U(width.W))

  when (inc && !dec) {
    when(value < r.end.U) {
      value := value + 1.U
    }
  }

  when (dec && !inc) {
    when(value > 0.U) {
      value := value - 1.U
    }
  }

  def is_full(): Bool = {
    value === r.end.U
  }

  def is_almost_full(): Bool = {
    value === (r.end - 1).U
  }

  def is_empty(): Bool = {
    value === r.start.U
  }
}

object UpDownCounter {
  def apply(r: Range, inc: Bool, dec: Bool) = new UpDownCounter(r, inc, dec)
}
