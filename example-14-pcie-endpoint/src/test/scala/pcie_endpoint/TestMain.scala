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
  it should "test the entire endpoint (with pre-prepared stimulus/response) - 32-bit" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointSimpleTest",
        "--top-name",
        "PcieEndpointSimpleTest"
      ),
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointSimpleTest(c)
    } should be(true)
  }

  it should "test the entire endpoint with 64-bit interface width" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpoint64SimpleTest",
        "--top-name",
        "PcieEndpoint64SimpleTest"
      ),
      () => new PcieEndpoint(64)
    ) { c =>
      new PcieEndpoint64bInterfaceSimpleTest(c)
    } should be(true)
  }

  it should "test the entire endpoint (with pre-prepared stimulus/response - 64-bit)" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointSimple64Test",
        "--top-name",
        "PcieEndpointSimple64Test"
      ),
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointSimple64Test(c)
    } should be(true)
  }

  it should "test the entire endpoint with PCIe BFM" in {
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
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointTest(c)
    } should be(true)
  }

  it should "test the Bus Mastering part of the endpoint" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointTestBM",
        "--top-name",
        "PcieEndpointTestBM"
      ),
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointTestBM(c)
    } should be(true)
  }

  it should "test the Bus Mastering part of the endpoint (64-bit interface)" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpoint64bInterfaceTestBM",
        "--top-name",
        "PcieEndpoint64bInterfaceTestBM"
      ),
      () => new PcieEndpoint(64)
    ) { c =>
      new PcieEndpoint64bInterfaceTestBM(c)
    } should be(true)
  }

  ignore should "test the Bus Mastering part of the endpoint, long reads" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointTestBMLongRead",
        "--top-name",
        "PcieEndpointTestBMLongRead"
      ),
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointTestBMLongRead(c)
    } should be(true)
  }

  it should "test the Bus Mastering part of the endpoint, long reads (64-bit if)" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpoint64TestBMLongRead",
        "--top-name",
        "PcieEndpoint64TestBMLongRead"
      ),
      () => new PcieEndpoint(64)
    ) { c =>
      new PcieEndpoint64TestBMLongRead(c)
    } should be(true)
  }

  it should "test the Completion handling of the endpoint" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/PcieEndpointComplTest",
        "--top-name",
        "PcieEndpointComplTest"
      ),
      () => new PcieEndpoint(256)
    ) { c =>
      new PcieEndpointComplTest(c)
    } should be(true)
  }

  it should "test the Bus Mastering alone" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/BusMasterTest",
        "--top-name",
        "BusMasterTest"
      ),
      () => new BusMaster(256)
    ) { c =>
      new BusMasterTest(c)
    } should be(true)
  }

}
