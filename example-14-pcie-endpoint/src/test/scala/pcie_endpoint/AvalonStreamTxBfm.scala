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

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    poke(av_st_tx.ready, 1)
    val valid = peek(av_st_tx.valid)
    if (valid > 0) {
      val data_beat = peek(av_st_tx.data)
      data += data_beat
      printWithBg(f"${t}%5d AvalonStreamTxBfm: data_beat = ${data_beat}%x")
    }
  }

  printWithBg(f"      AvalonStreamTxBfm: BFM initialized")

}
