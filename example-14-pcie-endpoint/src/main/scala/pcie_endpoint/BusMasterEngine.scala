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

class BusMasterEngine(val if_width: Int) extends Module {
  val io = IO(new Bundle {
    val conf_internal = Input(new Interfaces.ConfigIntern)
    val dma_desc = Flipped(Valid(new Interfaces.DmaDesc))

    val fsm_busy = Output(Bool())
    val mrd_in_flight_dec = Flipped(Valid(UInt(10.W)))
    val arb_hint = Output(Bool())
    val tx_st = new Interfaces.AvalonStreamTx(if_width)

    val dma_in = new Interfaces.AvalonStreamDataIn(if_width)

    val irq_fire = Output(Bool())
  })

  val MAX_PAYLOAD_SIZE_BYTES: Int = 256
  val MAX_PAYLOAD_SIZE_DWS: Int = MAX_PAYLOAD_SIZE_BYTES / 4

  val reg_mwr64 = Reg(new MWr64NoPayload)
  reg_mwr64.fmt := Fmt.MWr64.asUInt()
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
  reg_mwr64.req_id := io.conf_internal.busdev << 3.U
  reg_mwr64.ph := 0.U
  reg_mwr64.first_be := 0xf.U

  val reg_mrd64 = Reg(new MWr64NoPayload)
  reg_mrd64.fmt := Fmt.MRd64.asUInt()
  reg_mrd64.typ := 0.U
  reg_mrd64.r1 := false.B
  reg_mrd64.tc := 0.U
  reg_mrd64.r2 := false.B
  reg_mrd64.attr2 := false.B
  reg_mrd64.r3 := false.B
  reg_mrd64.th := false.B
  reg_mrd64.td := false.B
  reg_mrd64.ep := false.B
  reg_mrd64.attr1_0 := 0.U
  reg_mrd64.at := 0.U
  reg_mrd64.req_id := io.conf_internal.busdev << 3.U
  reg_mrd64.ph := 0.U
  reg_mrd64.first_be := 0xf.U

  val desc_len_dws = WireInit(io.dma_desc.bits.len_bytes / 4.U)
  val len_all_dws = Reg(UInt(32.W))
  val len_pkt_dws = Reg(UInt(32.W))

  val MRD_PKTS_MAX = 24 // we have 5 bits for tags (32 packets)
  val MRD_DWS_MAX = MAX_PAYLOAD_SIZE_DWS * MRD_PKTS_MAX
  val mrd_dws_in_flight = RegInit(0.U(util.log2Ceil(MRD_DWS_MAX + 1).W))
  val mrd_in_flight_inc = Wire(Bool())

  val dma_in_valid = Wire(Bool())

  val reg_irq_fire = RegInit(false.B)
  io.irq_fire := reg_irq_fire
  reg_irq_fire := false.B

  //==========================================================================
  // FSM
  object State extends ChiselEnum {
    val sIdle, sTxWrHdr256, sTxWrHdr64_0, sTxWrHdr64_1, sTxWrData, sTxRdHdr64_0, sTxRdHdr,
        sTxRdWait = Value
  }

  val state = RegInit(State.sIdle)
  io.fsm_busy := state =/= State.sIdle

  switch(state) {
    is(State.sIdle) {
      when(io.dma_desc.valid) {
        when(!io.dma_desc.bits.control.write_read_n) {
          if (if_width == 256) {
            state := State.sTxRdHdr
          } else if (if_width == 64) {
            state := State.sTxRdHdr64_0
          }

          len_all_dws := desc_len_dws
          len_pkt_dws := Mux(
            desc_len_dws > MAX_PAYLOAD_SIZE_DWS.U,
            MAX_PAYLOAD_SIZE_DWS.U,
            desc_len_dws
          )
          reg_mrd64.addr31_2 := io.dma_desc.bits.addr32_0 >> 2.U
          reg_mrd64.addr63_32 := io.dma_desc.bits.addr63_32
          reg_mrd64.length := Mux(
            desc_len_dws > MAX_PAYLOAD_SIZE_DWS.U,
            MAX_PAYLOAD_SIZE_DWS.U,
            desc_len_dws
          )
          reg_mrd64.last_be := Mux(desc_len_dws > 1.U, 0xf.U, 0.U)
        }.otherwise {
          if (if_width == 256) {
            state := State.sTxWrHdr256
          } else if (if_width == 64) {
            state := State.sTxWrHdr64_0
          }
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
        }
      }
    }
    is(State.sTxWrHdr256) {
      when(io.tx_st.ready && dma_in_valid) {
        when(len_pkt_dws > 4.U) {
          state := State.sTxWrData
        }.otherwise {
          state := State.sIdle
          reg_irq_fire := true.B
        }
        reg_mwr64.tag := (reg_mwr64.tag + 1.U) & 0x1f.U
        len_pkt_dws := len_pkt_dws - 4.U
        len_all_dws := len_all_dws - 4.U
        // address not incremented here
      }
    }
    is(State.sTxWrHdr64_0) {
      when(io.tx_st.ready) {
        state := State.sTxWrHdr64_1
      }
    }
    is(State.sTxWrHdr64_1) {
      when(io.tx_st.ready) {
        state := State.sTxWrData
      }
    }
    is(State.sTxWrData) {
      when(
        (io.tx_st.ready && dma_in_valid) || (io.tx_st.ready && (len_pkt_dws <= (if_width / 32 / 2).U))
      ) {
        len_pkt_dws := len_pkt_dws - Mux(
          len_pkt_dws > (if_width / 32).U,
          (if_width / 32).U,
          len_pkt_dws
        )
        len_all_dws := len_all_dws - Mux(
          len_pkt_dws > (if_width / 32).U,
          (if_width / 32).U,
          len_pkt_dws
        )
        reg_mwr64.addr31_2 := reg_mwr64.addr31_2 + (if_width / 32).U
        // we do not handle 32-bit address overflow -> very unlikely

        when(len_pkt_dws <= (if_width / 32).U) {
          when(len_all_dws > (if_width / 32).U) {
            if (if_width == 256) {
              state := State.sTxWrHdr256
            } else if (if_width == 64) {
              state := State.sTxWrHdr64_0
            }
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
          }.otherwise {
            state := State.sIdle
            reg_irq_fire := true.B
          }
        }
      }
    }
    is(State.sTxRdHdr64_0) {
      when(io.tx_st.ready) {
        state := State.sTxRdHdr
      }
    }
    is(State.sTxRdHdr) {
      when(io.tx_st.ready) {
        len_all_dws := len_all_dws - Mux(
          len_pkt_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          len_pkt_dws
        )
        reg_mrd64.addr31_2 := reg_mrd64.addr31_2 + MAX_PAYLOAD_SIZE_DWS.U
        reg_mrd64.length := Mux(
          len_all_dws > MAX_PAYLOAD_SIZE_DWS.U,
          MAX_PAYLOAD_SIZE_DWS.U,
          len_all_dws
        )
        reg_mrd64.tag := (reg_mrd64.tag + 1.U) & 0x1f.U

        when(len_all_dws > MAX_PAYLOAD_SIZE_DWS.U) {
          when(mrd_dws_in_flight < MRD_DWS_MAX.U - MAX_PAYLOAD_SIZE_DWS.U) {
            // stay in this state and continue sending packets ...

            // ... except if the interface width requires that we send header in more cycles
            if (if_width == 64) {
              state := State.sTxRdHdr64_0
            }
          }.otherwise {
            state := State.sTxRdWait
          }
        }.otherwise {
          state := State.sIdle
          reg_irq_fire := true.B
        }
      }
    }
    is(State.sTxRdWait) {
      when(mrd_dws_in_flight < MRD_DWS_MAX.U) {
        if (if_width == 256) {
          state := State.sTxRdHdr
        } else if (if_width == 64) {
          state := State.sTxRdHdr64_0
        }
      }
    }
  }

  // give a hint to the TX arbitration when doing long write bursts
  io.arb_hint := !(state === State.sIdle || state === State.sTxRdHdr) && len_all_dws > 8.U
  mrd_in_flight_inc := state === State.sTxRdHdr && io.tx_st.ready

  //==========================================================================
  when(io.mrd_in_flight_dec.valid && mrd_in_flight_inc) {
    mrd_dws_in_flight := mrd_dws_in_flight + MAX_PAYLOAD_SIZE_DWS.U - io.mrd_in_flight_dec.bits
  }.elsewhen(io.mrd_in_flight_dec.valid) {
      mrd_dws_in_flight := mrd_dws_in_flight - io.mrd_in_flight_dec.bits
    }
    .elsewhen(mrd_in_flight_inc) {
      mrd_dws_in_flight := mrd_dws_in_flight + MAX_PAYLOAD_SIZE_DWS.U
    }

  //==========================================================================
  // output data

  val mod_skid_buf = Module(new SkidBuffer(UInt(if_width.W)))

  val tx_enable = WireInit(
    state === State.sTxWrHdr256 ||
      ((state === State.sTxWrData) && len_pkt_dws > (if_width / 32 / 2).U)
  )
  mod_skid_buf.out.ready := tx_enable && io.tx_st.ready
  dma_in_valid := mod_skid_buf.out.valid
  val out_data = WireInit(mod_skid_buf.out.bits)
  val out_data_prev = RegEnable(out_data.asUInt(), mod_skid_buf.out.valid && io.tx_st.ready)

  io.dma_in.ready := mod_skid_buf.inp.ready
  mod_skid_buf.inp.valid := io.dma_in.valid
  mod_skid_buf.inp.bits := io.dma_in.data

  //==========================================================================
  // tx
  io.tx_st := DontCare
  io.tx_st.err := 0.U

  switch(state) {
    is(State.sIdle) {
      io.tx_st.valid := false.B
    }
    is(State.sTxWrHdr256) {
      io.tx_st.valid := dma_in_valid
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
      if (if_width == 256) {
        io.tx_st.data := Cat(
          out_data.asUInt()(127, 0),
          reg_mwr64.asUInt()(127, 0)
        )
      }
    }
    is(State.sTxWrHdr64_0) {
      io.tx_st.valid := true.B
      io.tx_st.sop := true.B
      io.tx_st.eop := false.B
      io.tx_st.empty := DontCare
      io.tx_st.data := reg_mwr64.asUInt()(63, 0)
    }
    is(State.sTxWrHdr64_1) {
      io.tx_st.valid := true.B
      io.tx_st.sop := false.B
      io.tx_st.eop := false.B
      io.tx_st.empty := DontCare
      io.tx_st.data := reg_mwr64.asUInt()(127, 64)
    }
    is(State.sTxWrData) {
      io.tx_st.valid := dma_in_valid
      io.tx_st.sop := false.B
      io.tx_st.eop := len_pkt_dws <= (if_width / 32).U
      when(len_pkt_dws >= (if_width / 32).U) {
        io.tx_st.empty := 0.U
      }.elsewhen(len_pkt_dws >= (if_width / 32 / 2).U) {
          io.tx_st.empty := 2.U
          io.tx_st.valid := true.B
        }
        .elsewhen(len_pkt_dws >= (if_width / 32 / 4).U) {
          io.tx_st.empty := 1.U
          io.tx_st.valid := true.B
        }

      if (if_width == 256) {
        io.tx_st.data := Cat(out_data.asUInt()(127, 0), out_data_prev.asUInt()(255, 128))
      } else if (if_width == 64) {
        io.tx_st.data := out_data
      } else {
        throw new Exception(s"Unsupported interface width ${if_width}")
      }
    }
    is(State.sTxRdHdr64_0) {
      io.tx_st.valid := true.B
      io.tx_st.sop := true.B
      io.tx_st.eop := false.B
      io.tx_st.empty := DontCare
      io.tx_st.data := reg_mrd64.asUInt()(63, 0)
    }
    is(State.sTxRdHdr) {
      io.tx_st.valid := true.B
      io.tx_st.eop := true.B
      io.tx_st.empty := 2.U
      if (if_width == 256) {
        io.tx_st.sop := true.B
        io.tx_st.data := Cat(
          out_data.asUInt()(127, 0), // for optimization
          reg_mrd64.asUInt()(127, 0)
        )
      } else if (if_width == 64) {
        io.tx_st.sop := false.B
        io.tx_st.data := reg_mrd64.asUInt()(127, 64)
      }
    }
    is(State.sTxRdWait) {
      io.tx_st.valid := false.B
    }
  }

}
