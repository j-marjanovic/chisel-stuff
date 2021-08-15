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
import chisel3.experimental.ChiselEnum
import chisel3.util._

class AvalonAgent extends Module {
  val io = IO(new Bundle {
    val mem_cmd = Flipped(new Interfaces.MemoryCmd)
    val avmm = new Interfaces.AvalonMM
  })

  val cmd_queue = Queue(io.mem_cmd, 4)

  val reg_address = Reg(UInt(32.W))
  val reg_byteenable = Reg(UInt(4.W))
  val reg_read = Reg(Bool())
  val reg_write = Reg(Bool())
  val reg_writedata = Reg(UInt(32.W))
  io.avmm.address := reg_address
  io.avmm.byteenable := reg_byteenable
  io.avmm.read := reg_read
  io.avmm.write := reg_write
  io.avmm.writedata := reg_writedata

  object State extends ChiselEnum {
    val sIdle, sWrite, sRead = Value
  }

  val state = RegInit(State.sIdle)
  cmd_queue.ready := state === State.sIdle

  switch(state) {
    is(State.sIdle) {
      when(cmd_queue.valid) {
        printf("AvalongAgent: received cmd\n")
        reg_address := cmd_queue.bits.address
        reg_byteenable := cmd_queue.bits.byteenable
        reg_write := true.B
        reg_writedata := cmd_queue.bits.writedata(31, 0)
        state := State.sWrite
      }
    }
    is(State.sWrite) {
      when(!io.avmm.waitrequest) {
        reg_write := false.B
        state := State.sIdle
      }
    }
  }

  // io.mem_cmd.ready := true.B

}
