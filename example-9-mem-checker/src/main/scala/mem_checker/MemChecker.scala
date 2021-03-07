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

package mem_checker

import bfmtester._
import chisel3._

class MemChecker(
    val addr_w: Int = 32,
    val data_w: Int = 512,
    val burst_w: Int = 8
) extends Module {
  val io = IO(new Bundle {
    val mem = new AvalonMMIf(data_w = data_w, addr_w = addr_w, burst_w = burst_w)
    val ctrl = new AxiLiteIf(8.W, 32.W)
  })

  val VERSION: Int = 0x00010003
  val BURST_LEN: Int = 128

  val mod_axi_slave = Module(
    new MemCheckerAxiSlave(
      version = VERSION,
      mem_data_w = data_w,
      BURST_LEN = BURST_LEN,
      addr_w = io.ctrl.addr_w.get
    )
  )
  val mod_avalon_wr = Module(new AvalonMMWriter(addr_w, data_w, burst_w, BURST_LEN))
  val mod_avalon_rd = Module(new AvalonMMReader(addr_w, data_w, burst_w, BURST_LEN))

  val mod_data_drv = Module(new DataDriver(data_w))
  val mod_data_chk = Module(new DataChecker(data_w))

  io.ctrl <> mod_axi_slave.io.ctrl

  // avalon wr
  io.mem.byteenable := mod_avalon_wr.io.byteenable
  io.mem.write := mod_avalon_wr.io.write
  io.mem.writedata := mod_avalon_wr.io.writedata
  mod_avalon_wr.io.response := io.mem.response
  mod_avalon_wr.io.waitrequest := io.mem.waitrequest
  mod_avalon_wr.io.writeresponsevalid := io.mem.writeresponsevalid

  mod_avalon_wr.io.ctrl_addr := mod_axi_slave.io.write_addr
  mod_avalon_wr.io.ctrl_len_bytes := mod_axi_slave.io.write_len
  mod_avalon_wr.io.ctrl_start := mod_axi_slave.io.write_start
  mod_axi_slave.io.wr_stats_resp_cntr := mod_avalon_wr.io.stats_resp_cntr
  mod_axi_slave.io.wr_stats_done := mod_avalon_wr.io.stats_done
  mod_axi_slave.io.wr_stats_duration := mod_avalon_wr.io.stats_duration

  // data drv
  mod_data_drv.io.ctrl_mode := mod_axi_slave.io.ctrl_mode

  mod_data_drv.io.data_init := mod_avalon_wr.io.data_init
  mod_data_drv.io.data_inc := mod_avalon_wr.io.data_inc
  mod_avalon_wr.io.data_data := mod_data_drv.io.data_data

  // avalon rd
  io.mem.read := mod_avalon_rd.io.read
  mod_avalon_rd.io.readdata := io.mem.readdata
  mod_avalon_rd.io.response := io.mem.response
  mod_avalon_rd.io.waitrequest := io.mem.waitrequest
  mod_avalon_rd.io.readdatavalid := io.mem.readdatavalid

  mod_avalon_rd.io.ctrl_addr := mod_axi_slave.io.read_addr
  mod_avalon_rd.io.ctrl_len_bytes := mod_axi_slave.io.read_len
  mod_avalon_rd.io.ctrl_start := mod_axi_slave.io.read_start
  mod_axi_slave.io.rd_stats_resp_cntr := mod_avalon_rd.io.stats_resp_cntr
  mod_axi_slave.io.rd_stats_done := mod_avalon_rd.io.stats_done
  mod_axi_slave.io.rd_stats_duration := mod_avalon_rd.io.stats_duration

  // data check
  mod_data_chk.io.ctrl_mode := mod_axi_slave.io.ctrl_mode

  mod_data_chk.io.data_init := mod_avalon_rd.io.data_init
  mod_data_chk.io.data_inc := mod_avalon_rd.io.data_inc
  mod_data_chk.io.data_data := mod_avalon_rd.io.data_data

  mod_axi_slave.io.check_tot := mod_data_chk.io.check_tot
  mod_axi_slave.io.check_ok := mod_data_chk.io.check_ok

  // 0 = read, 1 = write
  when(mod_axi_slave.io.ctrl_dir) {
    io.mem.address := mod_avalon_wr.io.address
    io.mem.burstcount := mod_avalon_wr.io.burstcount
  }.otherwise {
    io.mem.address := mod_avalon_rd.io.address
    io.mem.burstcount := mod_avalon_rd.io.burstcount
  }
}
