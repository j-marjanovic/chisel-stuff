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

package poor_mans_system_ila

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class PoorMansSystemILAKernel(val nr_els: Int) extends Module {
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val clear = Input(Bool())
    val done = Output(Bool())

    val trigger_mask = Input(UInt())
    val trigger_force = Input(Bool())
    val trigger_filt_en = Input(Bool())
    val trigger_filt_level = Input(UInt(12.W))
    val trigger_corr_en = Input(Bool())
    val trigger_pre_len =  Input(UInt())
    val trigger = Output(Bool())

    val status_filt_high_mark = Output(UInt(12.W))

    val MBDEBUG = new MbdebugBundle()
    val DEBUG_SYS_RESET = Input(Bool())

    // mem interface
    val dout = Output(UInt(32.W))
    val addr = Output(UInt(log2Ceil(nr_els).W))
    val we = Output(Bool())
  })

  val PRETRIG_LEN: Int = 100

  val addr = Reg(UInt(log2Ceil(nr_els).W))
  val addr_last = Reg(addr.cloneType)

  val trigger = Wire(Bool())

  object State extends ChiselEnum {
    val sIdle, sPreTrigger, sPostTrigger, sDone = Value
  }
  val state = RegInit(State.sIdle)

  switch(state) {
    is(State.sIdle) {
      when(io.enable) {
        state := State.sPreTrigger
        addr := 0.U
      }
    }
    is(State.sPreTrigger) {
      addr := addr + 1.U
      when(trigger) {
        state := State.sPostTrigger
        addr_last := addr - PRETRIG_LEN.U
      }
    }
    is(State.sPostTrigger) {
      addr := addr + 1.U
      when(addr === addr_last) {
        state := State.sDone
      }
    }
    is(State.sDone) {
      when(io.clear) {
        state := State.sIdle
      }
    }
  }

  // trigger - filter
  val mod_filt = Module(new FilterTrigger())
  mod_filt.io.data_in := io.MBDEBUG.TDO
  mod_filt.io.level := io.trigger_filt_level

  // trigger - correlator
  val pattern = BigInt("3ff00000003ff00000003ff", 16).U
  val mod_corr_ch = Module(new CorrelatorChannel(pattern))
  mod_corr_ch.io.inp := io.MBDEBUG.TDO

  // trigger
  val mbdebug_prev = RegNext(io.MBDEBUG.asUInt())
  val mbdebug_edge = WireInit(mbdebug_prev ^ io.MBDEBUG.asUInt())
  trigger := ((mbdebug_edge & io.trigger_mask) =/= 0.U) ||
    io.trigger_force ||
    (io.trigger_filt_en && mod_filt.io.trigger_out) ||
    (io.trigger_corr_en && mod_corr_ch.io.out_valid && (mod_corr_ch.io.out > 80.U))
  io.trigger := trigger && state === State.sPreTrigger

  // output
  val cntr = Reg(UInt(12.W))
  cntr := cntr + 1.U

  val out_data = WireInit(
    Cat(cntr,              // 31:20
      trigger,             // 19
      state.asUInt(),      // 18:17
      io.DEBUG_SYS_RESET,  // 16
      io.MBDEBUG.asUInt()  // 15:0
    )
  )
  io.dout := out_data
  io.addr := addr
  io.we := (state === State.sPreTrigger) || (state === State.sPostTrigger)
  io.done := state === State.sDone

  val status_filt_high_mark_reg = RegInit(UInt(12.W), 0.U)
  when (mod_filt.io.debug_q > status_filt_high_mark_reg) {
    status_filt_high_mark_reg := mod_filt.io.debug_q
  }
  io.status_filt_high_mark := status_filt_high_mark_reg
}
