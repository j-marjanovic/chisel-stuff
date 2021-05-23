/*
Copyright (c) 2020-2021 Jan Marjanovic

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

package lamports_bakery_algorithm

import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class LamportsBakeryAlgorithmTester extends ChiselFlatSpec {

  it should "check the AXI4-Lite manager" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/Axi4LiteManagerTest",
        "--top-name",
        "Axi4LiteManagerTest"
      ),
      () => new Axi4LiteManager(40)
    ) { c =>
      new Axi4LiteManagerTest(c)
    } should be(true)
  }

  it should "check the delay generation module" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/DelayGenLfsrTest",
        "--top-name",
        "DelayGenLfsrTest"
      ),
      () => new DelayGenLfsr(6)
    ) { c =>
      new DelayGenLfsrTest(c)
    } should be(true)
  }

  it should "check the algorithm" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/LamportsBakeryAlgorithmTest",
        "--top-name",
        "LamportsBakeryAlgorithmTest"
      ),
      () => new LamportsBakeryAlgorithm()
    ) { c =>
      new LamportsBakeryAlgorithmTest(c)
    } should be(true)
  }

}
