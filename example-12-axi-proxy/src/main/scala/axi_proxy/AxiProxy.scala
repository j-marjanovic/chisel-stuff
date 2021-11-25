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

package axi_proxy

import bfmtester._
import bfmtester.util._
import chisel3._
import chisel3.util._

import scala.collection.mutable.ListBuffer

class AxiProxy(
    val addr_w: Int = 40,
    val data_w: Int = 128,
    val id_w: Int = 5,
    val ctrl_data_w: Int = 32,
    val ctrl_addr_w: Int = 10
) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)
    val ctrl = new AxiLiteIf(ctrl_addr_w.W, ctrl_data_w.W)
  })

  import AxiProxy.area_map

  // control
  private val mod_ctrl = Module(
    new AxiLiteSubordinateGenerator(area_map = area_map, addr_w = ctrl_addr_w)
  )
  io.ctrl <> mod_ctrl.io.ctrl

  // version reg
  mod_ctrl.io.inp("VERSION_MAJOR") := 0.U
  mod_ctrl.io.inp("VERSION_MINOR") := 3.U
  mod_ctrl.io.inp("VERSION_PATCH") := 1.U

  // AXI interface
  private val mod_axi = Module(new Axi4Manager(addr_w, data_w))
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
  private val wr_data = Wire(Vec(data_w / ctrl_data_w, UInt(ctrl_data_w.W)))
  for (i <- 0 until data_w / ctrl_data_w) {
    wr_data(i) := mod_ctrl.io.out(s"DATA_WR${i}_DATA")
  }
  mod_axi.io.wr_cmd.data := wr_data.asUInt()
  mod_axi.io.wr_cmd.valid := mod_ctrl.io.out("CONTROL_START_WR")
  mod_ctrl.io.inp("STATUS_READY_WR") := mod_axi.io.wr_cmd.ready

  // read command
  mod_axi.io.rd_cmd.addr := Cat(
    mod_ctrl.io.out("ADDR_HI_").asUInt(),
    mod_ctrl.io.out("ADDR_LO_").asUInt()
  )
  for (i <- 0 until data_w / ctrl_data_w) {
    mod_ctrl.io.inp(s"DATA_RD${i}_DATA") := mod_axi.io.rd_cmd.data(32 * i + 31, 32 * i)
  }
  mod_axi.io.rd_cmd.valid := mod_ctrl.io.out("CONTROL_START_RD")
  mod_ctrl.io.inp("STATUS_READY_RD") := mod_axi.io.rd_cmd.ready

  // read done
  private val reg_done_wr = RegInit(false.B)
  mod_ctrl.io.inp("STATUS_DONE_WR") := reg_done_wr
  when(mod_axi.io.wr_cmd.done) {
    reg_done_wr := true.B
  }.elsewhen(mod_ctrl.io.out("CONTROL_DONE_CLEAR").asUInt() === true.B) {
    reg_done_wr := false.B
  }

  // write done
  private val reg_done_rd = RegInit(false.B)
  mod_ctrl.io.inp("STATUS_DONE_RD") := reg_done_rd
  when(mod_axi.io.rd_cmd.done) {
    reg_done_rd := true.B
  }.elsewhen(mod_ctrl.io.out("CONTROL_DONE_CLEAR").asUInt() === true.B) {
    reg_done_rd := false.B
  }
}

object AxiProxy {
  import bfmtester.util.AxiLiteSubordinateGenerator._

  // format: off
  val regs: ListBuffer[Reg] = ListBuffer[Reg](
    new Reg("ID_REG", 0,
      new Field("ID", hw_access = Access.NA, sw_access = Access.R,  hi = 31, Some(0), reset = Some(0xa8122081L.U)) // AXI PROXY
    ),
    new Reg("VERSION", 4,
      new Field("PATCH", hw_access = Access.W, sw_access = Access.R, hi = 7, lo = Some(0)),
      new Field("MINOR", hw_access = Access.W, sw_access = Access.R, hi = 15, lo = Some(8)),
      new Field("MAJOR", hw_access = Access.W, sw_access = Access.R, hi = 23, lo = Some(16)),
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
    new Reg("ADDR_LO", 0x40,
      new Field("", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0)),
    ),
    new Reg("ADDR_HI", 0x44,
      new Field("", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0)),
    ),
  )

  regs ++= (
    for (i <- 0 until 128/32) yield
      new Reg(s"DATA_WR$i", 0x60 + 4*i,
        new Field("DATA", hw_access = Access.R, sw_access = Access.RW, hi= 31, lo = Some(0))
      )
    )

  regs ++= (
    for (i <- 0 until 128/32) yield
      new Reg(s"DATA_RD$i", 0xa0 + 4*i,
        new Field("DATA", hw_access = Access.W, sw_access = Access.R, hi= 31, lo = Some(0)),
      )
    )
  // format: on

  val area_map = new AreaMap(regs: _*)
}
