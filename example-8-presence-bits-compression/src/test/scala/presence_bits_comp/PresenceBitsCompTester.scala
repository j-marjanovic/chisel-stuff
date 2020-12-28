/*
Copyright (c) 2020 Jan Marjanovic

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

package presence_bits_comp

import chisel3.{Module, iotesters}
import chisel3.iotesters.ChiselFlatSpec

class PresenceBitsCompTester extends ChiselFlatSpec {

  it should "check the decompressor kernel" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        s"test_run_dir/DecompressorKernelTest",
        "--top-name",
        s"DecompressorKernelTest"
      ),
      () => new DecompressorKernel(4)
    ) { c =>
      new DecompressorKernelTest(c)
    } should be(true)
  }

  it should "check the AXI master core" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        s"test_run_dir/AxiMasterCoreTest",
        "--top-name",
        s"AxiMasterCoreTest"
      ),
      () => new AxiMasterCoreReg
    ) { c =>
      new AxiMasterCoreTest(c)
    } should be(true)
  }

  it should "check the decompressor input adapter" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        s"test_run_dir/DecompressorInputAdapterTest",
        "--top-name",
        s"DecompressorInputAdapterTest"
      ),
      () => new DecompressorInputAdapter(8, 48, 128)
    ) { c =>
      new DecompressorInputAdapterTest(c)
    } should be(true)
  }

  it should "check the decompressor output adapter" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        s"test_run_dir/DecompressorOutputAdapterTest",
        "--top-name",
        s"DecompressorOutputAdapterTest"
      ),
      () => new DecompressorOutputAdapter(8, 48, 128)
    ) { c =>
      new DecompressorOutputAdapterTest(c)
    } should be(true)
  }
}
