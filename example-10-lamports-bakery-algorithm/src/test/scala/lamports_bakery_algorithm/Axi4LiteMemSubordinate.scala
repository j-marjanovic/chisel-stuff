/*
Copyright (c) 2020-2021 Jan Marjanovic

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

package lamports_bakery_algorithm

import chisel3._
import bfmtester._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

class Axi4LiteMemSubordinate(
    val axi: AxiLiteIf,
    val rnd: Random,
    val peek: Bits => BigInt,
    val poke: (Bits, BigInt) => Unit,
    val println: String => Unit,
    val ident: String = ""
) extends Bfm {

  private val sparse_mem: mutable.HashMap[BigInt, Byte] = new mutable.HashMap()
  private val width_bits: Int = axi.W.bits.wdata.getWidth

  private def printWithBg(s: String): Unit = {
    // black on magenta
    println("\u001b[97;44m" + s + "\u001b[39;49m")
  }

  def mem_set(start_addr: BigInt, els: Seq[Byte]): Unit = {
    for ((el, i) <- els.zipWithIndex) {
      sparse_mem += Tuple2(start_addr + i, el)
    }
  }

  def mem_get_el(addr: BigInt): Byte = {
    try {
      sparse_mem(addr)
    } catch {
      // return 0 for uninitialized memory
      case _: java.util.NoSuchElementException => 0
    }
  }

  def mem_get_word(addr: BigInt, word_size: Int): BigInt = {
    var tmp: BigInt = 0
    for (i <- word_size - 1 to 0 by -1) {
      tmp <<= 8
      tmp |= mem_get_el(addr + i) & 0xff
    }
    tmp
  }

  def mem_set_word(start_addr: BigInt, data: BigInt, word_size: Int): Unit = {
    for (i <- 0 until word_size) {
      val b = (data >> (8 * i)) & 0xff
      mem_set(start_addr + i, List[Byte](b.toByte))
    }
  }

  def mem_stats(): Unit = {
    printWithBg(f"      Axi4LiteMem($ident): Mem nr_els = ${sparse_mem.size}")
  }

  private class ReadCh {
    private var in_drive: Boolean = false
    private var in_drive_prev: Boolean = false
    private var ready_prev: Boolean = false
    private var cur_addr: Option[BigInt] = None

    def update_read(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
      // AR
      val arready: Boolean = rnd.nextBoolean()
      val arvalid: Boolean = peek(axi.AR.valid) > 0
      poke(axi.AR.ready, if (arready) 1 else 0)
      if (arready && arvalid) {
        val addr = peek(axi.AR.bits.addr)
        // printWithBg(f"${t}%5d Axi4LiteMem($ident): Read Data (addr=0x${addr}%x)")
        cur_addr = Some(addr)
      }

      // R
      if (!in_drive && cur_addr.isDefined) {
        in_drive = true
        val addr: BigInt = cur_addr.get
        cur_addr = None
        val data: BigInt = mem_get_word(addr, width_bits / 8)
        printWithBg(
          f"${t}%5d Axi4LiteMem($ident): Read Data (addr=0x${addr}%x, data=0x${data}%08x)"
        )
        poke(axi.R.bits.rdata, data)
        poke(axi.R.valid, 1)
        poke(axi.R.bits.rresp, 0)
      }

      val ready = peek(axi.R.ready) > 0
      if (in_drive_prev && ready_prev) {
        // printWithBg(f"${t}%5d Axi4LiteMem($ident): Read clear")
        in_drive = false
        poke(axi.R.bits.rdata, 0)
        poke(axi.R.valid, 0)
        poke(axi.R.bits.rresp, 0)
      }

      in_drive_prev = in_drive
      ready_prev = ready
    }
  }

  private class WriteCh {
    private val write_addrs: ListBuffer[BigInt] = ListBuffer[BigInt]()
    private val write_data: ListBuffer[BigInt] = ListBuffer[BigInt]()
    private var gen_resp: Boolean = false

    def update_write_addr(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
      // AW
      val awready: Boolean = rnd.nextBoolean()
      val awvalid: Boolean = peek(axi.AW.valid) > 0
      poke(axi.AW.ready, if (awready) 1 else 0)
      if (awready && awvalid) {
        var addr = peek(axi.AW.bits.addr)

        printWithBg(f"${t}%5d Axi4LiteMem($ident): Write Address (addr=0x${addr}%x)")

        write_addrs += addr
      }
    }

    def update_write_data(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
      // W
      val wready: Boolean = rnd.nextBoolean()
      val wvalid: Boolean = peek(axi.W.valid) > 0
      poke(axi.W.ready, if (wready) 1 else 0)

      if (wready && wvalid) {
        val data = peek(axi.W.bits.wdata)
        printWithBg(f"${t}%5d Axi4LiteMem($ident): Write Data (data=0x${data}%08x)")
        write_data += data
      }
    }

    def update_write_resp(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
      if (write_addrs.nonEmpty && write_data.nonEmpty) {
        val addr = write_addrs.remove(0)
        val data = write_data.remove(0)
        mem_set_word(addr, data, width_bits / 8)
        gen_resp = true
      }

      if (gen_resp) {
        poke(axi.B.valid, 1)
        poke(axi.B.bits, AxiIfRespToInt(AxiIfResp.OKAY))
      } else {
        poke(axi.B.valid, 0)
        poke(axi.B.bits, 0)
      }

      val ready = peek(axi.B.ready) > 0
      if (ready) {
        gen_resp = false
      }
    }
  }

  private val rd_ch = new ReadCh()
  private val wr_ch = new WriteCh()

  override def update(t: Long, poke: (Bits, BigInt) => Unit): Unit = {
    rd_ch.update_read(t, poke)
    wr_ch.update_write_data(t, poke)
    wr_ch.update_write_addr(t, poke)
    wr_ch.update_write_resp(t, poke)
  }

  printWithBg(f"      Axi4LiteMem($ident): BFM initialized")
}
