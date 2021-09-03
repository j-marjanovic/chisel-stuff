package pcie_endpoint

import chisel3._
import bfmtester._

import scala.collection.mutable.ListBuffer

class AvalonStreamRxBfm(
    val av_st_rx: Interfaces.AvalonStreamRx,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit
) extends Bfm {

  sealed trait MrdOrMWr
  case class MRd32(pkt: PciePackets.MRd32) extends MrdOrMWr
  case class MWr32(pkt: PciePackets.MWr32) extends MrdOrMWr

  private val data = ListBuffer[(MrdOrMWr, Int)]()

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  def transmit_mrd32(mrd32: PciePackets.MRd32, bar_mask: Int): Unit = {
    data += Tuple2(MRd32(mrd32), bar_mask)
  }

  def transmit_mwr32(mwr32: PciePackets.MWr32, bar_mask: Int): Unit = {
    data += Tuple2(MWr32(mwr32), bar_mask)
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    if (data.nonEmpty) {
      val data_beat = data.remove(0)
      data_beat._1 match {
        case pkt: MRd32 =>
          printWithBg(f"      AvalonStreamRxBfm: driving MRd32")
          poke(av_st_rx.data, PciePackets.to_bigint(pkt.pkt))
          poke(av_st_rx.sop, 1)
          poke(av_st_rx.eop, 1)
          poke(av_st_rx.empty, 1)
          poke(av_st_rx.valid, 1)
          poke(av_st_rx.err, 0)
          poke(av_st_rx.be, 0x000f0000)
          poke(av_st_rx.bar, data_beat._2)
        case pkt: MWr32 =>
          printWithBg(f"      AvalonStreamRxBfm: driving MWr32")
          poke(av_st_rx.data, PciePackets.to_bigint(pkt.pkt))
          poke(av_st_rx.sop, 1)
          poke(av_st_rx.eop, 1)
          poke(av_st_rx.empty, 1)
          poke(av_st_rx.valid, 1)
          poke(av_st_rx.err, 0)
          poke(av_st_rx.be, 0x000f0000)
          poke(av_st_rx.bar, data_beat._2)
      }
    } else {
      poke(av_st_rx.sop, 0)
      poke(av_st_rx.eop, 0)
      poke(av_st_rx.valid, 0)
    }
  }

  printWithBg(f"      AvalonStreamRxBfm: BFM initialized")
}
