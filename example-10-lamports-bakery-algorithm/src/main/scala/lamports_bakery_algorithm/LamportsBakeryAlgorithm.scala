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

package lamports_bakery_algorithm

import bfmtester._
import bfmtester.util._
import chisel3._
import chisel3.util._

class LamportsBakeryAlgorithm(
    val addr_w: Int = 49,
    val data_w: Int = 128,
    val id_w: Int = 6,
    val ctrl_addr_w: Int = 10
) extends Module {
  val io = IO(new Bundle {
    val m = Flipped(new AxiLiteIf(addr_w.W, data_w.W))
    val ctrl = new AxiLiteIf(ctrl_addr_w.W, 32.W)
  })

  import bfmtester.util.AxiLiteSubordinateGenerator._

  // format: off
  val area_map = new AreaMap(
    new Reg("ID_REG", 0,
      new Field("ID", hw_access = Access.NA, sw_access = Access.R,  hi = 31, Some(0), reset = Some(0xbace2a19L.U)) // BAKER ALG
    ),
    new Reg("VERSION", 4,
      new Field("PATCH", hw_access = Access.W, sw_access = Access.R, hi = 7, lo = Some(0), reset = Some(3.U)),
      new Field("MINOR", hw_access = Access.W, sw_access = Access.R, hi = 15, lo = Some(8), reset = Some(2.U)),
      new Field("MAJOR", hw_access = Access.W, sw_access = Access.R, hi = 23, lo = Some(16), reset = Some(1.U))
    ),
    new Reg("SCRATCH", 0xc,
      new Field("FIELD", hw_access = Access.NA, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("STATUS", 0x10,
      new Field("DONE", hw_access = Access.W, sw_access = Access.R, hi = 0, lo = None),
    ),
    new Reg("CONTROL", 0x14,
      new Field("START", hw_access = Access.R, sw_access = Access.RW, hi = 0, lo = None, singlepulse = true),
      new Field("CLEAR", hw_access = Access.R, sw_access = Access.RW, hi = 1, lo = None, singlepulse = true)
    ),

    new Reg("ADDR", 0x20,
      new Field("COUNTER_LO", hw_access = Access.R, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("ADDR", 0x24,
      new Field("COUNTER_HI", hw_access = Access.R, sw_access = Access.RW, hi = addr_w - 32, lo = Some(0))
    ),

    new Reg("ADDR", 0x28,
      new Field("CHOOSING_LO", hw_access = Access.R, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("ADDR", 0x2c,
      new Field("CHOOSING_HI", hw_access = Access.R, sw_access = Access.RW, hi = addr_w - 32, lo = Some(0))
    ),

    new Reg("ADDR", 0x30,
      new Field("NUMBER_LO", hw_access = Access.R, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("ADDR", 0x34,
      new Field("NUMBER_HI", hw_access = Access.R, sw_access = Access.RW, hi = addr_w - 32, lo = Some(0))
    ),

    new Reg("IDX", 0x40,
      new Field("INST", hw_access = Access.R, sw_access = Access.RW, hi = 3, lo = Some(0))
    ),
    new Reg("IDX", 0x44,
      new Field("LAST", hw_access = Access.R, sw_access = Access.RW, hi = 3, lo = Some(0))
    ),

    new Reg("CONFIG", 0x50,
      new Field("NR_CYCLES", hw_access = Access.R, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("DIAG", 0x54,
      new Field("LAST_CNTR", hw_access = Access.W, sw_access = Access.R, hi = 31, lo = Some(0))
    ),
  )
  // format: on

  // control
  val mod_ctrl = Module(new AxiLiteSubordinateGenerator(area_map = area_map, addr_w = ctrl_addr_w))
  io.ctrl <> mod_ctrl.io.ctrl

  mod_ctrl.io.inp("VERSION_MAJOR") := 0x03.U
  mod_ctrl.io.inp("VERSION_MINOR") := 0x14.U
  mod_ctrl.io.inp("VERSION_PATCH") := 0x16.U

  // manager interface
  val mod_axi = Module(new Axi4LiteManager(addr_w))
  mod_axi.io.m <> io.m

  // lock module
  val mod_lock = Module(new LamportsBakeryAlgorithmLock(addr_w, data_w))
  mod_lock.io.addr_choosing := Cat(
    mod_ctrl.io.out("ADDR_CHOOSING_HI").asUInt(),
    mod_ctrl.io.out("ADDR_CHOOSING_LO").asUInt()
  )
  mod_lock.io.addr_number := Cat(
    mod_ctrl.io.out("ADDR_NUMBER_HI").asUInt(),
    mod_ctrl.io.out("ADDR_NUMBER_LO").asUInt()
  )
  mod_lock.io.idx_inst := mod_ctrl.io.out("IDX_INST")
  mod_lock.io.idx_max := mod_ctrl.io.out("IDX_LAST")
  mod_lock.io.wr_cmd <> mod_axi.io.wr_cmd
  mod_lock.io.rd_cmd <> mod_axi.io.rd_cmd

  val mod_incr = Module(new LamportsBakeryAlgorithmIncr(addr_w, data_w))
  mod_incr.io.addr_cntr := Cat(
    mod_ctrl.io.out("ADDR_COUNTER_LO").asUInt(),
    mod_ctrl.io.out("ADDR_COUNTER_HI").asUInt()
  )

  // mux
  def mux_rd_cmd[T <: Axi4LiteManagerRdCmd](sel: UInt, out: T, in: Vec[T]): Unit = {
    out.addr := in(sel).addr
    out.valid := in(sel).valid

    for (i <- in.indices) {
      in(i).data := Mux(sel === i.U, out.data, 0.U)
      in(i).ready := Mux(sel === i.U, out.ready, 0.U)
      in(i).done := Mux(sel === i.U, out.done, 0.U)
    }
  }

  def mux_wr_cmd[T <: Axi4LiteManagerWrCmd](sel: UInt, out: T, in: Vec[T]): Unit = {
    out.addr := in(sel).addr
    out.valid := in(sel).valid
    out.data := in(sel).data

    for (i <- in.indices) {
      in(i).ready := Mux(sel === i.U, out.ready, 0.U)
      in(i).done := Mux(sel === i.U, out.done, 0.U)
    }
  }

  val mux_sel = Wire(Bool())
  mux_rd_cmd(mux_sel, mod_axi.io.rd_cmd, VecInit(Seq(mod_lock.io.rd_cmd, mod_incr.io.rd_cmd)))
  mux_wr_cmd(mux_sel, mod_axi.io.wr_cmd, VecInit(Seq(mod_lock.io.wr_cmd, mod_incr.io.wr_cmd)))

  // sequencer
  val mod_seq = Module(new LamportsBakeryAlgorithmSequencer)
  mod_seq.io.nr_cycles := mod_ctrl.io.out("CONFIG_NR_CYCLES")
  mod_seq.io.start := mod_ctrl.io.out("CONTROL_START")
  mod_seq.io.clear := mod_ctrl.io.out("CONTROL_CLEAR")
  mod_ctrl.io.inp("DIAG_LAST_CNTR") := mod_seq.io.last_cntr
  mod_ctrl.io.inp("STATUS_DONE") := mod_seq.io.done

  mod_lock.io.lock := mod_seq.io.lock_lock
  mod_lock.io.unlock := mod_seq.io.lock_unlock
  mod_seq.io.lock_locked := mod_lock.io.locked
  mod_seq.io.lock_unlocked := mod_lock.io.unlocked

  mod_incr.io.start := mod_seq.io.incr_start
  mod_seq.io.incr_done := mod_incr.io.done

  mux_sel := mod_seq.io.mux_sel
}
