/*
MIT License

Copyright (c) 2018-2019 Jan Marjanovic

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

package FPGAbignum

import chisel3._
import chisel3.util._

class FPGAbignumAdder(val data_width: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = new AxiStreamIf(data_width)
    val b = new AxiStreamIf(data_width)
    val q = Flipped(new AxiStreamIf(data_width))
  })

  val sIdle :: sCalc :: sStoreA :: sStoreB :: sLast :: Nil = Enum(5)
  val state = RegInit(sIdle)

  val reg_a = Reg(SInt(data_width.W))
  val reg_b = Reg(SInt(data_width.W))
  val carry = Reg(SInt(2.W))
  val is_last = RegInit(false.B)

  switch (state) {
    is (sIdle) {
      when (io.a.tvalid && !io.b.tvalid) {
        state := sStoreA
      } .elsewhen (!io.a.tvalid && io.b.tvalid) {
        state := sStoreB
      } .elsewhen (io.a.tvalid && io.b.tvalid) {
        when (io.a.tlast || io.b.tlast) {
          is_last := true.B
          state := sLast
        } .otherwise {
          state := sCalc
        }
      }
    }
    is (sCalc) {
      when (io.q.tready) {
        //when (is_last) {
        //  state := sLast
        //  is_last := false.B
        //} .otherwise {
          state := sIdle
        //}
      }
    }
    is (sStoreA) {
      when (io.b.tvalid) {
        state := sCalc
      }
    }
    is (sStoreB) {
      when (io.a.tvalid) {
        state := sCalc
      }
    }
    is (sLast) {
      when (io.q.tready) {
        state := sIdle
      }
    }
  }


  io.a.tready := (state === sIdle) || (state === sStoreB)
  io.b.tready := (state === sIdle) || (state === sStoreA)

  io.q.tvalid := (state === sCalc) || (state === sLast)


  val acc = Wire(SInt((data_width + 2).W))
  acc := io.a.tdata.asSInt() + io.b.tdata.asSInt() + carry

  val tdata_reg = Reg(UInt(data_width.W))
  tdata_reg := acc.asUInt()(data_width, 0)
  io.q.tdata := tdata_reg

  io.q.tlast := (state === sLast)
  io.q.tuser := 0.U
}
