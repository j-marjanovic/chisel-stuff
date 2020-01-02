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

import chisel3.iotesters._

class InputShuffleTester(c: InputShuffle) extends PeekPokeTester(c) {

  assert(c.n == 8, "very simple test, only for 1 size")

  for (i <- 0 until c.n) {
    poke(c.io.inp(i).re, i)
    poke(c.io.inp(i).im, i)
  }

  step(1)

  val expect_vec_8: List[Int] = List(
    0, 4, 2, 6, 1, 5, 3, 7
  )

  for (i <- 0 until c.n) {
    expect(c.io.out(i).re, expect_vec_8(i))
    expect(c.io.out(i).im, expect_vec_8(i))
  }
}
