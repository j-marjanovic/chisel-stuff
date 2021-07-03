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

package axi_traffic_gen

import bfmtester._
import bfmtester.util._
import chisel3._
import chisel3.util._

import scala.collection.mutable.ListBuffer

class AxiTrafficGen(
    val addr_w: Int = 40,
    val data_w: Int = 128,
    val id_w: Int = 2,
    val ctrl_data_w: Int = 32,
    val ctrl_addr_w: Int = 10
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)
    val ctrl = new AxiLiteIf(ctrl_addr_w.W, ctrl_data_w.W)
  })

  import AxiTrafficGen.area_map

  // control
  private val mod_ctrl = Module(
    new AxiLiteSubordinateGenerator(area_map = area_map, addr_w = ctrl_addr_w)
  )
  io.ctrl <> mod_ctrl.io.ctrl

  // version reg
  mod_ctrl.io.inp("VERSION_MAJOR") := 0.U
  mod_ctrl.io.inp("VERSION_MINOR") := 3.U
  mod_ctrl.io.inp("VERSION_PATCH") := 0.U

  // AXI interface
  private val mod_axi = Module(new Axi4Manager(addr_w, data_w, id_w))
  mod_axi.io.m <> io.m
  mod_axi.io.config_axi_cache := mod_ctrl.io.out("CONFIG_AXI_CACHE")
  mod_axi.io.config_axi_prot := mod_ctrl.io.out("CONFIG_AXI_PROT")
  mod_axi.io.config_axi_user := mod_ctrl.io.out("CONFIG_AXI_USER")
  mod_ctrl.io.inp("STATS_CNTR_RD") := mod_axi.io.diag_cntr_rd
  mod_ctrl.io.inp("STATS_CNTR_WR") := mod_axi.io.diag_cntr_wr

  // write command
  mod_axi.io.wr_cmd.addr := Cat(
    mod_ctrl.io.out("ADDR_HI_").asUInt(),
    mod_ctrl.io.out("ADDR_LO_").asUInt()
  )
  mod_axi.io.wr_cmd.valid := mod_ctrl.io.out("CONTROL_START_WR")
  mod_axi.io.wr_cmd.len := mod_ctrl.io.out("LENGTH_")
  mod_ctrl.io.inp("STATUS_READY_WR") := mod_axi.io.wr_cmd.ready
  mod_ctrl.io.inp("STATUS_DONE_WR") := mod_axi.io.wr_cmd.done

  // read command
  mod_axi.io.rd_cmd.addr := Cat(
    mod_ctrl.io.out("ADDR_HI_").asUInt(),
    mod_ctrl.io.out("ADDR_LO_").asUInt()
  )

  mod_axi.io.rd_cmd.valid := mod_ctrl.io.out("CONTROL_START_RD")
  mod_axi.io.rd_cmd.len := mod_ctrl.io.out("LENGTH_")
  mod_ctrl.io.inp("STATUS_READY_RD") := mod_axi.io.rd_cmd.ready
  mod_ctrl.io.inp("STATUS_DONE_RD") := mod_axi.io.rd_cmd.done

  // done clear
  mod_axi.io.done_clear := mod_ctrl.io.out("CONTROL_DONE_CLEAR")

}

object AxiTrafficGen {
  import bfmtester.util.AxiLiteSubordinateGenerator._

  // format: off
  val regs: ListBuffer[Reg] = ListBuffer[Reg](
    new Reg("ID_REG", 0,
      new Field("ID", hw_access = Access.NA, sw_access = Access.R,  hi = 31, Some(0), reset = Some(0xa8172a9eL.U)) // AXI TRA GE
    ),
    new Reg("VERSION", 4,
      new Field("PATCH", hw_access = Access.W, sw_access = Access.R, hi = 7, lo = Some(0)),
      new Field("MINOR", hw_access = Access.W, sw_access = Access.R, hi = 15, lo = Some(8)),
      new Field("MAJOR", hw_access = Access.W, sw_access = Access.R, hi = 23, lo = Some(16))
    ),
    new Reg("SCRATCH", 0xc,
      new Field("FIELD", hw_access = Access.NA, sw_access = Access.RW, hi = 31, lo = Some(0))
    ),
    new Reg("STATUS", 0x10,
      new Field("DONE_WR", hw_access = Access.W, sw_access = Access.R, hi = 0, lo = None),
      new Field("DONE_RD", hw_access = Access.W, sw_access = Access.R, hi = 1, lo = None),
      new Field("READY_WR", hw_access = Access.W, sw_access = Access.R, hi = 8, lo = None),
      new Field("READY_RD", hw_access = Access.W, sw_access = Access.R, hi = 9, lo = None),
    ),
    new Reg("CONTROL", 0x14,
      new Field("START_WR", hw_access = Access.R, sw_access = Access.RW, hi = 0, lo = None, singlepulse = true),
      new Field("START_RD", hw_access = Access.R, sw_access = Access.RW, hi = 1, lo = None, singlepulse = true),
      new Field("DONE_CLEAR", hw_access = Access.R, sw_access = Access.RW, hi = 8, lo = None, singlepulse = true),
    ),
    new Reg("CONFIG_AXI", 0x20,
      new Field("CACHE", hw_access = Access.R, sw_access = Access.RW, hi= 3, lo = Some(0)),
      new Field("PROT", hw_access = Access.R, sw_access = Access.RW, hi= 10, lo = Some(8)),
      new Field("USER", hw_access = Access.R, sw_access = Access.RW, hi= 17, lo = Some(16))
    ),
    new Reg("STATS", addr = 0x24,
      new Field("CNTR_WR", hw_access = Access.W, sw_access = Access.R, hi = 9, lo = Some(0)),
      new Field("CNTR_RD", hw_access = Access.W, sw_access = Access.R, hi = 25, lo = Some(16)),
    ),
    new Reg("LENGTH", addr = 0x30,
      new Field("", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0)),
    ),
    new Reg("ADDR_LO", 0x40,
      new Field("", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0)),
    ),
    new Reg("ADDR_HI", 0x44,
      new Field("", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0)),
    ),
  )
  // format: on

  val area_map = new AreaMap(regs: _*)
}
