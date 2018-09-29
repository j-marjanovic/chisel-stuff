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

package overrideStepExample

import scala.collection.mutable.ListBuffer
import chisel3.iotesters.PeekPokeTester

class OverrideStepExampleTester(c: ExamplePipeline) extends PeekPokeTester(c) {

  //==========================================================================
  // constants

  //noinspection ScalaStyle
  val STIMULI: List[BigInt] = List(0, 1, 2, 10, 99, 100,
    Math.pow(2, 16).toInt - 2, Math.pow(2, 16).toInt - 1)

  // high-level model of the DUT
  def model(x: BigInt): BigInt = (x + 1) & 0xFFFF

  //==========================================================================
  // BFMs

  class ValidIfDriver {
    private var stim = ListBuffer[BigInt]()

    def stimAppend(x: BigInt): Unit = {
      stim += x
    }

    def stimAppend(ls: List[BigInt]): Unit = {
      stim ++= ls
    }

    def update(): Unit = {
      if (stim.nonEmpty) {
        poke(c.io.in_valid, 1)
        val data = stim.remove(0)
        poke(c.io.in_data, data)
        println(f"${t}%5d Driver: sent ${data}")
      } else {
        poke(c.io.in_valid, 0)
      }
    }
  }

  class ValidIfMonitor {
    private var resp = ListBuffer[BigInt]()

    def respGet(): List[BigInt] = {
      val ls = resp.result()
      resp.clear()
      ls
    }

    def update(): Unit = {
      val vld = peek(c.io.out_valid)
      if (vld != 0) {
        val data = peek(c.io.out_data)
        resp += data
        println(f"${t}%5d Monitor: received ${data}")
      }
    }
  }

  //==========================================================================
  // step

  def stepSingle(): Unit = {
    driver.update()
    monitor.update()
    super.step(1)
  }

  override def step(n: Int): Unit = {
    for(_ <- 0 until n) {
      stepSingle
    }
  }

  //==========================================================================
  // main

  val driver = new ValidIfDriver()
  val monitor = new ValidIfMonitor()

  println(f"${t}%5d Test starting...")
  step(5)

  driver.stimAppend(STIMULI)
  step(STIMULI.length + 10)

  val expected = STIMULI.map(model)
  val received = monitor.respGet()

  assert(expected == received)

  println(f"${t}%5d Test finished.")
}
