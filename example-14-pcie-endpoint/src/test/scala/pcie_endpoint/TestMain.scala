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

import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class TestMain extends ChiselFlatSpec {

  it should "handle MWr packets" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/MWrBar0Test",
        "--top-name",
        "MWrBar0Test"
      ),
      () => new PcieEndpoint
    ) { c =>
      new MWrBar0Test(c)
    } should be(true)
  }

  it should "handle MRd packets" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/MRdBar0Test",
        "--top-name",
        "MRdBar0Test"
      ),
      () => new PcieEndpoint
    ) { c =>
      new MRdBar0Test(
        c,
        BigInt("00000000000000D90F0018000100000000000000D90001000018000F00000001", 16),
        BigInt("56F7C9CC639A671FE7C8C416000001000000000000180000040000044A000001", 16)
        //                               10203040000000000180000040000044a000001
      )
    } should be(true)
  }

  it should "test the entire endpoint" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointTest",
        "--top-name",
        "PcieEndpointTest"
      ),
      () => new PcieEndpoint
    ) { c =>
      new PcieEndpointTest(c)
    } should be(true)
  }

}
