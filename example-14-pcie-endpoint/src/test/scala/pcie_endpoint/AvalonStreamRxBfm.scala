/*
Copyright (c) 2021 Jan Marjanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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

  class El(val data: BigInt, val sop: BigInt, val eop: BigInt, val bar: BigInt)

  private val data = ListBuffer[El]()

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  def transmit_mrd32(mrd32: PciePackets.MRd32, bar_mask: Int): Unit = {
    val if_width = av_st_rx.data.getWidth
    if (av_st_rx.data.getWidth == 256) {
      data += new El(PciePackets.to_bigint(mrd32), 1, 1, bar_mask)
    } else if (if_width == 64) {
      val bs = PciePackets.to_bigint(mrd32)
      val mask = BigInt(2).pow(64) - 1
      val els = for (i <- 0 until 4) yield (bs >> (i * 64)) & mask
      data += new El(els(0), 1, 0, bar_mask)
      data += new El(els(1), 0, 1, bar_mask)
    } else {
      throw new Exception(s"Unsupported interface width ${if_width}")
    }
  }

  def transmit_mwr32(mwr32: PciePackets.MWr32, bar_mask: Int): Unit = {
    val if_width = av_st_rx.data.getWidth
    if (av_st_rx.data.getWidth == 256) {
      data += new El(PciePackets.to_bigint(mwr32), 1, 1, bar_mask)
    } else if (if_width == 64) {
      val bs = PciePackets.to_bigint(mwr32)
      val mask = BigInt(2).pow(64) - 1
      val els = for (i <- 0 until 4) yield (bs >> (i * 64)) & mask
      data += new El(els(0), 1, 0, bar_mask)
      if ((mwr32.Addr30_2 & 1) == 1) {
        data += new El(els(1), 0, 1, bar_mask)
      } else {
        data += new El(els(1), 0, 0, bar_mask)
        data += new El(els(2), 0, 1, bar_mask)
      }
    } else {
      throw new Exception(s"Unsupported interface width ${if_width}")
    }
  }

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    if (data.nonEmpty) {
      val data_beat = data.remove(0)
      printWithBg(f"${t}%5d AvalonStreamRxBfm: driving data = ${data_beat.data}%x")
      poke(av_st_rx.data, data_beat.data)
      poke(av_st_rx.sop, data_beat.sop)
      poke(av_st_rx.eop, data_beat.eop)
      poke(av_st_rx.empty, 1) // not used in 64-bit interface
      poke(av_st_rx.valid, 1)
      poke(av_st_rx.err, 0)
      poke(av_st_rx.be, 0)
      poke(av_st_rx.bar, data_beat.bar)
    } else {
      poke(av_st_rx.sop, 0)
      poke(av_st_rx.eop, 0)
      poke(av_st_rx.valid, 0)
    }
  }

  printWithBg(f"      AvalonStreamRxBfm: BFM initialized")
}
