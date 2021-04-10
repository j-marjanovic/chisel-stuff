package poor_mans_system_ila

import bfmtester.Bfm
import chisel3.{Bits, Bool}

import scala.collection.mutable.ListBuffer

class SlowSignalGen(val signal: Bool,
                    val peek: Bits => BigInt,
                    val poke: (Bits, BigInt) => Unit,
                    val println: String => Unit)
    extends Bfm {

  private val buffer = ListBuffer[BigInt]()

  def append(x: BigInt, len: Int): Unit = {
    for (i <- 0 until len) {
      buffer += (x >> i) & 1
    }
  }

  var prescaler: Int = 0
  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    prescaler += 1
    if (prescaler == 10) {
      prescaler = 0
      if (buffer.nonEmpty) {
        val x = buffer.remove(0)
        poke(signal, x)
      }
    }
  }

  def is_empty(): Boolean = buffer.isEmpty
}
