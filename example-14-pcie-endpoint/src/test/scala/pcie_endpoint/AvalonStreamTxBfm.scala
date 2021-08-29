package pcie_endpoint

import bfmtester.Bfm
import chisel3.Bits

import scala.collection.mutable.ListBuffer

class AvalonStreamTxBfm(
    val av_st_tx: Interfaces.AvalonStreamTx,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit
) extends Bfm {

  val data = ListBuffer[BigInt]()

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    val valid = peek(av_st_tx.valid)
    if (valid > 0) {
      val data = peek(av_st_tx.data)
      this.data += data
      println(f"data = ${data}%x")
    }
  }
}
