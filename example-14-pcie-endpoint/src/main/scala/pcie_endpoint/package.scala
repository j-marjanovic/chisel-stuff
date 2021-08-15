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

import chisel3._
import chisel3.experimental.ChiselEnum

package object pcie_endpoint {

  object Fmt extends ChiselEnum {
    val MRd32 = Value(0.U)
    val MRd64 = Value(1.U)
    val MWr32 = Value(2.U)
    val MWr64 = Value(3.U)
  }

  class CommonHdr extends Bundle {
    // byte 0
    val fmt = UInt(3.W)
    val typ = UInt(5.W)

    // byte 1 - 3
    val rsvd = UInt(24.W)
  }

  class MRd32 extends Bundle {

    // byte 11 -8
    val addr = UInt(30.W)
    val ph = UInt(2.W)

    // byte 4, 5
    val req_id = UInt(16.W)

    // byte 6
    val tag = UInt(8.W)

    // byte 7
    val last_be = UInt(4.W)
    val first_be = UInt(4.W)

    // byte 0
    val fmt = UInt(3.W)
    val typ = UInt(5.W)

    // byte 1
    val r1 = Bool()
    val tc = UInt(3.W)
    val r2 = Bool()
    val attr2 = Bool()
    val r3 = Bool()
    val th = Bool()

    // byte 2 (w/o length9_8)
    val td = Bool()
    val ep = Bool()
    val attr1_0 = UInt(2.W)
    val at = UInt(2.W)

    // byte 3
    val length = UInt(10.W)
  }

  class MWr32 extends Bundle {
    val dw0 = UInt(32.W)
    val dw1 = UInt(32.W)

    // byte 11 -8
    val addr = UInt(30.W)
    val ph = UInt(2.W)

    // byte 4, 5
    val req_id = UInt(16.W)

    // byte 6
    val tag = UInt(8.W)

    // byte 7
    val last_be = UInt(4.W)
    val first_be = UInt(4.W)

    // byte 0
    val fmt = UInt(3.W)
    val typ = UInt(5.W)

    // byte 1
    val r1 = Bool()
    val tc = UInt(3.W)
    val r2 = Bool()
    val attr2 = Bool()
    val r3 = Bool()
    val th = Bool()

    // byte 2 (w/o length9_8)
    val td = Bool()
    val ep = Bool()
    val attr1_0 = UInt(2.W)
    val at = UInt(2.W)

    // byte 3
    val length = UInt(10.W)
  }
}
