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
import chisel3.util._
import chisel3.experimental.ChiselEnum

class BusMasterEngine extends Module {
  val io = IO(new Bundle {
    val conf_internal = Input(new Interfaces.ConfigIntern)
    val dma_desc = Flipped(Valid(new Interfaces.DmaDesc))

    val tx_st = new Interfaces.AvalonStreamTx
  })

  val MAX_PAYLOAD_SIZE_BYTES: Int = 128
  val MAX_PAYLOAD_SIZE_DWS: Int = MAX_PAYLOAD_SIZE_BYTES / 4

  val reg_mwr64 = Reg(new MWr64NoPayload)
  reg_mwr64.fmt := 0x3.U
  reg_mwr64.typ := 0.U
  reg_mwr64.r1 := false.B
  reg_mwr64.tc := 0.U
  reg_mwr64.r2 := false.B
  reg_mwr64.attr2 := false.B
  reg_mwr64.r3 := false.B
  reg_mwr64.th := false.B
  reg_mwr64.td := false.B
  reg_mwr64.ep := false.B
  reg_mwr64.attr1_0 := 0.U
  reg_mwr64.at := 0.U
  reg_mwr64.tag := 0.U
  reg_mwr64.req_id := io.conf_internal.busdev << 8.U
  reg_mwr64.ph := 0.U

  reg_mwr64.first_be := 0xf.U

  val desc_len_dws = WireInit(io.dma_desc.bits.len_bytes / 4.U)
  val len_all_dws = Reg(UInt(32.W))
  val len_pkt_dws = Reg(UInt(32.W))

  //==========================================================================
  // FSM
  object State extends ChiselEnum {
    val sIdle, sTxHdr, sTxData = Value
  }

  val state = RegInit(State.sIdle)

  switch(state) {
    is(State.sIdle) {
      when(io.dma_desc.valid) {
        state := State.sTxHdr
        len_all_dws := desc_len_dws
        len_pkt_dws := Mux(
          desc_len_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          desc_len_dws
        )
        reg_mwr64.addr31_2 := io.dma_desc.bits.addr32_0 >> 2.U
        reg_mwr64.addr63_32 := io.dma_desc.bits.addr63_32
        reg_mwr64.length := Mux(
          desc_len_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          desc_len_dws
        )
        reg_mwr64.last_be := Mux(desc_len_dws > 1.U, 0xf.U, 0.U)
      }.elsewhen(len_all_dws > 0.U && len_all_dws < 0xfffffff0L.U) {
        state := State.sTxHdr
        len_pkt_dws := Mux(
          len_all_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          len_all_dws
        )
        reg_mwr64.length := Mux(
          len_all_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          len_all_dws
        )
        reg_mwr64.last_be := Mux(len_all_dws > 1.U, 0xf.U, 0.U)
      }
    }
    is(State.sTxHdr) {
      when(io.tx_st.ready) {
        when(len_pkt_dws > 4.U) {
          state := State.sTxData
        }.otherwise {
          state := State.sIdle
        }
        len_pkt_dws := len_pkt_dws - 4.U
        len_all_dws := len_all_dws - 4.U
        // address not incremented here
      }
    }
    is(State.sTxData) {
      when(io.tx_st.ready) {
        len_pkt_dws := len_pkt_dws - 8.U
        len_all_dws := len_all_dws - 8.U
        reg_mwr64.addr31_2 := reg_mwr64.addr31_2 + 8.U
        // we do not handle 32-bit address overflow -> very unlikely

        when(len_pkt_dws <= 8.U) {
          state := State.sIdle
        }
      }
    }
  }

  //==========================================================================
  // output data

  val out_data = RegInit(VecInit.tabulate(256 / 16)((idx: Int) => idx.U(16.W)))
  val data_advance = WireInit((state === State.sTxData || state === State.sTxHdr) && io.tx_st.ready)
  val data_init = WireInit(state === State.sIdle)

  when(data_init) {
    for (idx <- 0 until 256 / 16) {
      out_data(idx) := idx.U
    }
  }.elsewhen(data_advance) {
    for (idx <- 0 until 256 / 16) {
      out_data(idx) := out_data(idx) + (256 / 16).U
    }
  }

  val out_data_prev = RegEnable(out_data.asUInt(), data_advance)

  //==========================================================================
  // tx
  io.tx_st := DontCare
  io.tx_st.err := 0.U

  when(state === State.sTxHdr) {
    io.tx_st.valid := true.B
    io.tx_st.sop := true.B
    io.tx_st.eop := !(len_pkt_dws > 4.U)
    when(len_pkt_dws === 1.U) {
      io.tx_st.empty := 1.U
    }.elsewhen(len_pkt_dws === 2.U) {
        io.tx_st.empty := 1.U
      }
      .otherwise {
        io.tx_st.empty := 0.U
      }
    io.tx_st.data := Cat(
      out_data.asUInt()(127, 0),
      reg_mwr64.asUInt()(127, 0)
    )
  }.elsewhen(state === State.sTxData) {
      io.tx_st.valid := true.B
      io.tx_st.sop := false.B
      io.tx_st.eop := len_pkt_dws <= 8.U
      when(len_pkt_dws >= 8.U) {
        io.tx_st.empty := 0.U
      }.elsewhen(len_pkt_dws >= 4.U) {
          io.tx_st.empty := 2.U
        }
        .elsewhen(len_pkt_dws >= 2.U) {
          io.tx_st.empty := 1.U
        }
      io.tx_st.data := Cat(out_data.asUInt()(127, 0), out_data_prev.asUInt()(255, 128))
    }
    .otherwise {
      io.tx_st.valid := false.B
    }

}
