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
import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class PoorMansSystemILATest extends ChiselFlatSpec {

  ignore should "check the generated AXI4-Lite module" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PoorMansSystemILATester",
        "--top-name",
        "PoorMansSystemILATester"
      ),
      () => new PoorMansSystemILA(256)
    ) { c =>
      new PoorMansSystemILATester(c)
    } should be(true)
  }

  ignore should "check the filter trigger behavior" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/FilterTriggerTester",
        "--top-name",
        "FilterTriggerTester"
      ),
      () => new FilterTrigger
    ) { c =>
      new FilterTriggerTester(c)
    } should be(true)
  }

  ignore should "check the filter mode integration in ILA" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PoorMansSystemILAFilterTester",
        "--top-name",
        "PoorMansSystemILAFilterTester"
      ),
      () => new PoorMansSystemILA(256)
    ) { c =>
      new PoorMansSystemILAFilterTester(c)
    } should be(true)
  }

  val pattern = BigInt("3ff00000003ff00000003ff", 16).U

  it should "check the correlator channel behavior" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/CorrelatorChannelTester",
        "--top-name",
        "CorrelatorChannelTester"
      ),
      () => new CorrelatorChannel(pattern) // 32.U(20.W))
    ) { c =>
      new CorrelatorChannelTester(c)
    } should be(true)
  }


}
