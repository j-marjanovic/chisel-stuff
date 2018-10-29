/*
MIT License

Copyright (c) 2018 Jan Marjanovic

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

package ord_decoup_io

import chisel3._
import chisel3.iotesters.{OrderedDecoupledHWIOTester}


class OrdDecoupIoTester extends OrderedDecoupledHWIOTester {
  val device_under_test = Module(new OrdDecoupIo)
  val c = device_under_test
  enable_all_debug = true

  OrderedDecoupledHWIOTester.max_tick_count = 10

  inputEvent(c.io.data_in.bits -> 1)
  outputEvent(c.io.data_out.bits -> 2)
  outputEvent(c.io.data_out.bits -> 3)

  inputEvent(c.io.data_in.bits -> 5)
  outputEvent(c.io.data_out.bits -> 6)
  outputEvent(c.io.data_out.bits -> 7)

  inputEvent(c.io.data_in.bits -> 10)
  outputEvent(c.io.data_out.bits -> 11)
  outputEvent(c.io.data_out.bits -> 12)
}
