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

import bfmtester.util._
import bfmtester._
import chisel3._

class PoorMansSystemILA(val BUF_LEN: Int = 4096) extends Module {
  import AxiLiteSubordinateGenerator._

  // format: off
  val area_map = new AreaMap(
    new Reg("ID_REG", 0,
      new Field("ID", hw_access = Access.NA, sw_access = Access.R,  hi = 31, Some(0), reset = Some(0x5157311a.U))
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
      new Field("DONE", hw_access = Access.W, sw_access = Access.R, hi = 0, lo = None, singlepulse = true),
    ),
    new Reg("CONTROL", 0x14,
      new Field("CLEAR", hw_access = Access.R, sw_access = Access.RW, hi = 0, lo = None, singlepulse = true),
      new Field("ENABLE", hw_access = Access.R, sw_access = Access.RW, hi = 31, lo = None)
    ),
    new Reg("TRIG_CTRL", 0x24,
      new Field("MASK", hw_access = Access.R, sw_access = Access.RW, hi = 9, lo = Some(0))
    ),
    new Mem("DATA", addr = 0x1000, nr_els = BUF_LEN, data_w = 32),
  )
  // format: on

  val io = IO(new Bundle {
    val ctrl = new AxiLiteIf(addr_w = 14.W)
    val MBDEBUG = new MbdebugBundle()
    val DEBUG_SYS_RESET = Input(Bool())
  })

  val mod_ctrl = Module(
    new AxiLiteSubordinateGenerator(area_map = area_map, addr_w = 14)
  )
  io.ctrl <> mod_ctrl.io.ctrl

  mod_ctrl.io.inp("VERSION_MAJOR") := 0x01.U
  mod_ctrl.io.inp("VERSION_MINOR") := 0x00.U
  mod_ctrl.io.inp("VERSION_PATCH") := 0x00.U

  val mod_mem = Module(new DualPortRam(32, BUF_LEN))
  mod_mem.io.clk := this.clock
  mod_mem.io.addra := mod_ctrl.io.out("MEM_DATA_ADDR").asUInt()
  mod_mem.io.dina := mod_ctrl.io.out("MEM_DATA_DIN").asUInt()
  mod_mem.io.wea := mod_ctrl.io.out("MEM_DATA_WE").asUInt().asBool()
  mod_ctrl.io.inp("MEM_DATA_DOUT") := mod_mem.io.douta

  val mod_kernel = Module(new PoorMansSystemILAKernel(BUF_LEN))
  mod_kernel.io.MBDEBUG := io.MBDEBUG
  mod_kernel.io.DEBUG_SYS_RESET := io.DEBUG_SYS_RESET

  mod_kernel.io.trigger_mask := mod_ctrl.io.out("TRIG_CTRL_MASK")

  mod_kernel.io.enable := mod_ctrl.io.out("CONTROL_ENABLE")
  mod_kernel.io.clear := mod_ctrl.io.out("CONTROL_CLEAR")
  mod_ctrl.io.inp("STATUS_DONE") := mod_kernel.io.done

  mod_mem.io.addrb := mod_kernel.io.addr
  mod_mem.io.dinb := mod_kernel.io.dout
  mod_mem.io.web := mod_kernel.io.we
}
