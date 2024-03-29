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
    val addr_w: Int = 40,
    val data_w: Int = 128,
    val id_w: Int = 5,
    val ctrl_data_w: Int = 32,
    val ctrl_addr_w: Int = 10
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)
    val ctrl = new AxiLiteIf(ctrl_addr_w.W, ctrl_data_w.W)
    val irq_req = Output(Bool())
    val dbg_last_cntr = Output(UInt(32.W))
    val dbg_last_data = Output(UInt(32.W))
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
    new Reg("CONFIG", addr = 8,
      new Field("DIST", hw_access = Access.W, sw_access = Access.R, hi = 6, lo = Some(0)),
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
    new Reg("CONFIG", 0x54,
      new Field("DLY_PRBS_INIT", hw_access = Access.R, sw_access = Access.RW, hi= 15, lo = Some(0))
    ),
    new Reg("CONFIG_AXI", 0x58,
      new Field("CACHE", hw_access = Access.R, sw_access = Access.RW, hi= 3, lo = Some(0)),
      new Field("PROT", hw_access = Access.R, sw_access = Access.RW, hi= 10, lo = Some(8)),
      new Field("USER", hw_access = Access.R, sw_access = Access.RW, hi= 17, lo = Some(16))
    ),
    new Reg("DIAG", 0x60,
      new Field("LAST_CNTR", hw_access = Access.W, sw_access = Access.R, hi = 31, lo = Some(0))
    ),
    new Reg("DIAG", 0x64,
      new Field("LAST_DATA", hw_access = Access.W, sw_access = Access.R, hi = 31, lo = Some(0))
    ),
  )
  // format: on

  // consts
  val DIST_BYTES: UInt = 64.U

  // control
  val mod_ctrl = Module(new AxiLiteSubordinateGenerator(area_map = area_map, addr_w = ctrl_addr_w))
  io.ctrl <> mod_ctrl.io.ctrl

  mod_ctrl.io.inp("VERSION_MAJOR") := 2.U
  mod_ctrl.io.inp("VERSION_MINOR") := 1.U
  mod_ctrl.io.inp("VERSION_PATCH") := 0.U
  mod_ctrl.io.inp("CONFIG_DIST") := DIST_BYTES

  // manager interface
  val mod_axi = Module(new Axi4Manager(addr_w))
  mod_axi.io.m <> io.m
  mod_axi.io.config_axi_cache := mod_ctrl.io.out("CONFIG_AXI_CACHE")
  mod_axi.io.config_axi_prot := mod_ctrl.io.out("CONFIG_AXI_PROT")
  mod_axi.io.config_axi_user := mod_ctrl.io.out("CONFIG_AXI_USER")

  // lock module
  val mod_lock = Module(new LamportsBakeryAlgorithmLock(DIST_BYTES, addr_w, data_w))
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
    mod_ctrl.io.out("ADDR_COUNTER_HI").asUInt(),
    mod_ctrl.io.out("ADDR_COUNTER_LO").asUInt()
  )
  mod_ctrl.io.inp("DIAG_LAST_DATA") := mod_incr.io.last_data

  // mux
  def mux_rd_cmd[T <: Axi4ManagerRdCmd](sel: UInt, out: T, in: Vec[T]): Unit = {
    out.addr := in(sel).addr
    out.valid := in(sel).valid

    for (i <- in.indices) {
      in(i).data := Mux(sel === i.U, out.data, 0.U)
      in(i).ready := Mux(sel === i.U, out.ready, 0.U)
      in(i).done := Mux(sel === i.U, out.done, 0.U)
    }
  }

  def mux_wr_cmd[T <: Axi4ManagerWrCmd](sel: UInt, out: T, in: Vec[T]): Unit = {
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
  io.irq_req := mod_seq.io.done

  mod_lock.io.lock := mod_seq.io.lock_lock
  mod_lock.io.unlock := mod_seq.io.lock_unlock
  mod_seq.io.lock_locked := mod_lock.io.locked
  mod_seq.io.lock_unlocked := mod_lock.io.unlocked

  mod_incr.io.start := mod_seq.io.incr_start
  mod_seq.io.incr_done := mod_incr.io.done

  mux_sel := mod_seq.io.mux_sel

  // random delay
  val DLY_CNTR_W = 6
  val mod_dly_gen = Module(new DelayGenLfsr(DLY_CNTR_W))
  mod_dly_gen.io.load := mod_ctrl.io.out("CONTROL_START")
  mod_dly_gen.io.d := mod_ctrl.io.out("CONFIG_DLY_PRBS_INIT")

  mod_dly_gen.io.start := mod_seq.io.dly_gen_start
  mod_seq.io.dly_gen_done := mod_dly_gen.io.done

  // diag
  io.dbg_last_cntr := mod_seq.io.last_cntr
  io.dbg_last_data := mod_incr.io.last_data

}
