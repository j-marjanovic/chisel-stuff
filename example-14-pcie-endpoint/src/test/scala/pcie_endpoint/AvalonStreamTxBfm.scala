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

  class RecvData(val data: BigInt, val empty: BigInt) {}

  val recv_buffer = ListBuffer[RecvData]()

  private def printWithBg(s: String): Unit = {
    // dark blue on light gray
    println("\u001b[38;5;18;47m" + s + "\u001b[39;49m")
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    poke(av_st_tx.ready, 1)
    val valid = peek(av_st_tx.valid)
    if (valid > 0) {
      val data = peek(av_st_tx.data)
      val empty = peek(av_st_tx.empty)
      recv_buffer += new RecvData(data, empty)
      printWithBg(f"${t}%5d AvalonStreamTxBfm: data = ${data}%x, empty = ${empty}")
    }
  }

  printWithBg(f"      AvalonStreamTxBfm: BFM initialized")

}
