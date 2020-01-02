/*
MIT License

Copyright (c) 2019 Jan Marjanovic

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

package fft

import chisel3._
import chisel3.iotesters
import chisel3.iotesters.ChiselFlatSpec

class FftModuleTest extends ChiselFlatSpec {

  "butterfly tester" should "compare expected and obtained response" in {
    for (ni <- List(8, 16, 32)) {
      for (ki <- List(1, 2, 3)) {
        iotesters.Driver.execute(
          Array(
            "--backend-name",
            "verilator",
            "--fint-write-vcd",
            "--test-seed",
            "1234",
            "--target-dir",
            s"test_run_dir/FftButterflyTester_${ni}_${ki}",
            "--top-name",
            s"FftButterflyTester_${ni}_${ki}",
          ),
          () => new FftButterfly(18.W, 16.BP, ki, ni)
        ) { c =>
          new FftButterflyTester(c, ki, ni)
        } should be(true)
      }
    }
  }

  "input shuffle tester" should "compare expected and obtained response" in {
    val size = 8
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/InputShuffleTester",
        "--top-name",
        "InputShuffleTester"
      ),
      () => new InputShuffle(size)
    ) { c =>
      new InputShuffleTester(c)
    } should be(true)
  }

  "module tester" should "compare expected and obtained response" in {
    iotesters.Driver.execute(
      Array(
        "--backend-name",
        "verilator",
        "--fint-write-vcd",
        "--test-seed",
        "1234",
        "--target-dir",
        "test_run_dir/FftModuleTester",
        "--top-name",
        "FftModuleTester",
      ),
      () => new FftModule
    ) { c =>
      new FftModuleTester(c)
    } should be(true)
  }
}
