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
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

class Axi4Manager(addr_w: Int, data_w: Int, id_w: Int) extends Module {
  val io = IO(new Bundle {
    val m = new AxiIf(addr_w.W, data_w.W, id_w.W, aruser_w = 2.W, awuser_w = 2.W)

    val config_axi_cache = Input(UInt(4.W))
    val config_axi_prot = Input(UInt(3.W))
    val config_axi_user = Input(UInt(2.W))

    val wr_cmd = new Axi4ManagerWrCmd(addr_w)
    val rd_cmd = new Axi4ManagerRdCmd(addr_w)
    val done_clear = Input(Bool())

    val diag_cntr_rd_cyc = Output(UInt(32.W))
    val diag_cntr_rd_ok = Output(UInt(32.W))
    val diag_cntr_wr_cyc = Output(UInt(32.W))
  })

  val inst_rd = Module(new Axi4ManagerRd(addr_w, data_w, id_w))

  io.m.AR <> inst_rd.io.m.AR
  io.m.R <> inst_rd.io.m.R
  inst_rd.io.m.AW := DontCare
  inst_rd.io.m.W := DontCare
  inst_rd.io.m.B := DontCare

  inst_rd.io.config_axi_cache := io.config_axi_cache
  inst_rd.io.config_axi_prot := io.config_axi_prot
  inst_rd.io.config_axi_user := io.config_axi_user

  inst_rd.io.rd_cmd <> io.rd_cmd
  inst_rd.io.done_clear := io.done_clear
  io.diag_cntr_rd_cyc := inst_rd.io.diag_cntr_rd_cyc
  io.diag_cntr_rd_ok := inst_rd.io.diag_cntr_rd_ok

  val inst_wr = Module(new Axi4ManagerWr(addr_w, data_w, id_w))

  io.m.AW <> inst_wr.io.m.AW
  io.m.W <> inst_wr.io.m.W
  io.m.B <> inst_wr.io.m.B
  inst_wr.io.m.AR := DontCare
  inst_wr.io.m.R := DontCare

  inst_wr.io.config_axi_cache := io.config_axi_cache
  inst_wr.io.config_axi_prot := io.config_axi_prot
  inst_wr.io.config_axi_user := io.config_axi_user

  inst_wr.io.wr_cmd <> io.wr_cmd
  inst_wr.io.done_clear := io.done_clear
  io.diag_cntr_wr_cyc := inst_wr.io.diag_cntr_wr_cyc

}
