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

package axiLiteExample

import chisel3._

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._
import chisel3.iotesters.PeekPokeTester


class PipelineWithAxiLiteTester(c: PipelineWithAxiLite) extends PeekPokeTester(c) {

  //==========================================================================
  // constants

  //noinspection ScalaStyle
  val STIMULI: List[BigInt] = List(0, 1, 2, 10, 99, 100,
    Math.pow(2, 16).toInt - 2, Math.pow(2, 16).toInt - 1)

  // high-level model of the DUT
  def model(x: BigInt): BigInt = (x + 1) & 0xFFFF


  //==========================================================================
  // step

  val rm = runtimeMirror(getClass.getClassLoader)
  val im = rm.reflect(this)
  val members = im.symbol.typeSignature.members
  def bfms = members.filter(_.typeSignature <:< typeOf[ChiselBfm])

  def stepSingle(): Unit = {
    for (bfm <- bfms) {
      im.reflectField(bfm.asTerm).get.asInstanceOf[ChiselBfm].update(t)
    }
    super.step(1)
  }

  override def step(n: Int): Unit = {
    for(_ <- 0 until n) {
      stepSingle
    }
  }

  //==========================================================================
  // main

  val driver = new ValidIfDriver(c.io.in_data, c.io.in_valid, peek, poke, println)
  val monitor = new ValidIfMonitor(c.io.out_data, c.io.out_valid, peek, poke, println)
  val axi_master = new AxiLiteMasterBfm(c.io.ctrl, peek, poke, println)

  println(f"${t}%5d Test starting...")
  axi_master.readPush(0)
  axi_master.readPush(4)
  axi_master.readPush(8)
  axi_master.writePush(0xc, 1)

  step(20)
  axi_master.readPush(8)
  axi_master.readPush(8)
  axi_master.readPush(8)
  axi_master.readPush(8)

  step(5)

  driver.stimAppend(STIMULI)
  step(STIMULI.length + 10)

  val expected = STIMULI.map(model)
  val received = monitor.respGet()

  assert(expected == received)

  println(f"${t}%5d Test finished.")
}
