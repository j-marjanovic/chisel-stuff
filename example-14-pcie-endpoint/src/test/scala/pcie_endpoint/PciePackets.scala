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

import scodec._
import scodec.bits._
import scodec.codecs._

object PciePackets {
  object Fmt extends Enumeration {
    val ThreeDw = Value(0)
    val FourDw = Value(1)
    val ThreeDwData = Value(2)
    val FourDwData = Value(3)
    val Prefix = Value(4)
  }

  case class CommonHdr(
      Fmt: Int,
      Type: Int,
      rsvd2: Boolean,
      TrClass: Int,
      rsvd1: Boolean,
      Attr2: Int,
      rsvd0: Boolean,
      TH: Boolean,
      TD: Boolean,
      EP: Boolean,
      Attr1_0: Int,
      AT: Int,
      Length: Int
  )

  private implicit val commonHdr = {
    ("Fmt" | uint(3)) ::
      ("Type" | uint(5)) ::
      ("rsvd1" | bool) ::
      ("TrClass" | uint(3)) ::
      ("rsvd1" | bool) ::
      ("Attr2" | uint(1)) ::
      ("rsvd0" | bool) ::
      ("TH" | bool) ::
      ("TD" | bool) ::
      ("EP" | bool) ::
      ("Attr1_0" | uint(2)) ::
      ("AddrType" | uint(2)) ::
      ("Length" | uint(10))
  }.as[CommonHdr]

  case class MRd32(
      // Figure 2-5: Fields Present in All TLP Headers
      Addr30_2: Int,
      ProcHint: Int,
      ReqID: Int,
      Tag: Int,
      LastBE: Int,
      FirstBE: Int,
      Fmt: Int,
      Type: Int,
      rsvd2: Boolean,
      TrClass: Int,
      rsvd1: Boolean,
      Attr2: Int,
      rsvd0: Boolean,
      TH: Boolean,
      TD: Boolean,
      EP: Boolean,
      Attr1_0: Int,
      AT: Int,
      Length: Int
  )

  private implicit val mrd32 = {
    ("Addr30_2" | uint(30)) ::
      ("ProcHint" | uint(2)) ::
      ("ReqID" | uint(16)) ::
      ("Tag" | uint(8)) ::
      ("LastBE" | uint(4)) ::
      ("FirstBE" | uint(4)) ::
      ("Fmt" | uint(3)) ::
      ("Type" | uint(5)) ::
      ("rsvd1" | bool) ::
      ("TrClass" | uint(3)) ::
      ("rsvd1" | bool) ::
      ("Attr2" | uint(1)) ::
      ("rsvd0" | bool) ::
      ("TH" | bool) ::
      ("TD" | bool) ::
      ("EP" | bool) ::
      ("Attr1_0" | uint(2)) ::
      ("AddrType" | uint(2)) ::
      ("Length" | uint(10))
  }.as[MRd32]

  case class MWr32(
      // Figure 2-5: Fields Present in All TLP Headers
      Dw1: Long,
      Dw0: Long,
      Dw0_unalign: Long,
      Addr30_2: Int,
      ProcHint: Int,
      ReqID: Int,
      Tag: Int,
      LastBE: Int,
      FirstBE: Int,
      Fmt: Int,
      Type: Int,
      rsvd2: Boolean,
      TrClass: Int,
      rsvd1: Boolean,
      Attr2: Int,
      rsvd0: Boolean,
      TH: Boolean,
      TD: Boolean,
      EP: Boolean,
      Attr1_0: Int,
      AT: Int,
      Length: Int
  )

  private implicit val mwr32 = {
    ("Dw1" | ulong(32)) ::
      ("Dw0" | ulong(32)) ::
      ("Dw0_unalign" | ulong(32)) ::
      ("Addr30_2" | uint(30)) ::
      ("ProcHint" | uint(2)) ::
      ("ReqID" | uint(16)) ::
      ("Tag" | uint(8)) ::
      ("LastBE" | uint(4)) ::
      ("FirstBE" | uint(4)) ::
      ("Fmt" | uint(3)) ::
      ("Type" | uint(5)) ::
      ("rsvd1" | bool) ::
      ("TrClass" | uint(3)) ::
      ("rsvd1" | bool) ::
      ("Attr2" | uint(1)) ::
      ("rsvd0" | bool) ::
      ("TH" | bool) ::
      ("TD" | bool) ::
      ("EP" | bool) ::
      ("Attr1_0" | uint(2)) ::
      ("AddrType" | uint(2)) ::
      ("Length" | uint(10))
  }.as[MWr32]

  case class CplD(
      Dw1: Long,
      Dw0: Long,
      Dw0_unalign: Long,
      ReqID: Int,
      Tag: Int,
      rsvd: Boolean,
      LoAddr: Int,
      ComplID: Int,
      ComplStatus: Int,
      BCM: Boolean,
      ByteCount: Int,
      Fmt: Int,
      Type: Int,
      rsvd2: Boolean,
      TrClass: Int,
      rsvd1: Boolean,
      Attr2: Int,
      rsvd0: Boolean,
      TH: Boolean,
      TD: Boolean,
      EP: Boolean,
      Attr1_0: Int,
      AT: Int,
      Length: Int
  )

  private implicit val cpld = {
    ("Dw1" | ulong(32)) ::
      ("Dw0" | ulong(32)) ::
      ("Dw0_unalign" | ulong(32)) ::
      ("ReqID" | uint(16)) ::
      ("Tag" | uint(8)) ::
      ("rsvd" | bool) ::
      ("LoAddr" | uint(7)) ::
      ("ComplID" | uint(16)) ::
      ("ComplStatus" | uint(3)) ::
      ("BCM" | bool) ::
      ("ByteCount" | uint(12)) ::
      ("Fmt" | uint(3)) ::
      ("Type" | uint(5)) ::
      ("rsvd1" | bool) ::
      ("TrClass" | uint(3)) ::
      ("rsvd1" | bool) ::
      ("Attr2" | uint(1)) ::
      ("rsvd0" | bool) ::
      ("TH" | bool) ::
      ("TD" | bool) ::
      ("EP" | bool) ::
      ("Attr1_0" | uint(2)) ::
      ("AddrType" | uint(2)) ::
      ("Length" | uint(10))
  }.as[CplD]

  def to_hdr(raw: BigInt): CommonHdr = {
    val size_bytes: Int = (commonHdr.sizeBound.exact.get / 8).toInt
    commonHdr.decode(BitVector(raw.toByteArray.reverse.slice(0, size_bytes).reverse)).require.value
  }

  def to_mwr32(raw: BigInt): MWr32 = {
    val size_bytes: Int = (mwr32.sizeBound.exact.get / 8).toInt
    val bs: Array[Byte] = raw.toByteArray.reverse.slice(0, size_bytes).reverse
    val ext: Array[Byte] = (for (_ <- 0 until size_bytes - bs.length) yield 0.toByte).toArray
    val tot_bs: Array[Byte] = ext ++ bs
    mwr32.decode(BitVector(tot_bs)).require.value
  }

  def to_mrd32(raw: BigInt): MRd32 = {
    val size_bytes: Int = (mrd32.sizeBound.exact.get / 8).toInt
    mrd32.decode(BitVector(raw.toByteArray.reverse.slice(0, size_bytes).reverse)).require.value
  }

  def to_cpld(raw: BigInt): CplD = {
    val size_bytes: Int = (cpld.sizeBound.exact.get / 8).toInt
    val bs: Array[Byte] = raw.toByteArray.reverse.slice(0, size_bytes).reverse
    val ext: Array[Byte] = (for (_ <- 0 until size_bytes - bs.length) yield 0.toByte).toArray
    val tot_bs: Array[Byte] = ext ++ bs
    cpld.decode(BitVector(tot_bs)).require.value
  }

  def to_bigint(pkt: MWr32): BigInt = {
    val ba = Array[Byte](0) ++ mwr32.encode(pkt).require.toByteArray
    BigInt(ba)
  }

  def to_bigint(pkt: MRd32): BigInt = {
    val ba = Array[Byte](0) ++ mrd32.encode(pkt).require.toByteArray
    BigInt(ba)
  }
}
