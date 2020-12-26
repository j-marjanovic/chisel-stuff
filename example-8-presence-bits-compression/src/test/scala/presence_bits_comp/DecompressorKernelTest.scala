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

import bfmtester._

class DecompressorKernelTest(c: DecompressorKernel) extends BfmTester(c) {

  val STIM1: List[BigInt] = List[BigInt](
// format: off
    0xf, 0x1, 0x2, 0x3, 0x4,
    0x1, 0x5,
    0x2, 0x6,
    0x4, 0x7,
    0x8, 0x8,
    0x3, 0x9, 0xa,
    0xc, 0xb, 0xc
// format:on
  )

  val EXP1: List[BigInt] = List[BigInt](
// format: off
    0x1, 0x2, 0x3, 0x4,
    0x5, 0x0, 0x0, 0x0,
    0x0, 0x6, 0x0, 0x0,
    0x0, 0x0, 0x7, 0x0,
    0x0, 0x0, 0x0, 0x8,
    0x9, 0xa, 0x0, 0x0,
    0x0, 0x0, 0xb, 0xc
// format:on
  )

  val driver = new DecompressorKernelDriver(c.io.in, this.rnd, peek, poke, println)
  val monitor = new DecompressorKernelMonitor(c.io.out, peek, poke, println)

  driver.stimAppend(STIM1)
  step(100)
  val resp = monitor.respGet()
  expect(resp.size == EXP1.size, "Response size should match expected size")
  for ((r, e) <- resp.zip(EXP1)) {
    expect(r == e, f"Data should match (recv = ${r}%02x, exp = ${e}%02x)")
  }
}
