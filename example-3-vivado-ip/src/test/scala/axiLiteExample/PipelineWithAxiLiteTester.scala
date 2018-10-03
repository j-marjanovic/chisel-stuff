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
  // tasks
  def stepUntilAxiMasterDone(at_done: axi_master.Resp => Unit = _ => Unit): Unit = {
    var resp: Option[axi_master.Resp] = None

    while({resp = axi_master.getResponse(); resp.isEmpty}) {
      step(1)
    }

    at_done(resp.get)
  }

  def getModuleInfo(): Unit = {
    axi_master.readPush(0)
    axi_master.readPush(4)
    axi_master.readPush(8)

    stepUntilAxiMasterDone(resp => println(f"${t}%5d read ID reg: ${resp.rd_data}%08x"))
    stepUntilAxiMasterDone(resp => println(f"${t}%5d read VERSION reg: ${resp.rd_data}%08x"))
    stepUntilAxiMasterDone(resp => println(f"${t}%5d read NR SAMP reg: ${resp.rd_data}%08x"))
  }

  def testWriteStrobe(): Unit = {
    println(f"${t}%5d testWriteStrobe: write 0xABCD in two steps into addr 0xC")

    val EXPECTED = 0xABCD
    val DUMMY = 0x1234
    val STRB_0 = 0x2
    val STRB_1 = 0x1
    val DATA_0 = (EXPECTED & 0xFF00) | (DUMMY & 0x00FF)
    val DATA_1 = (EXPECTED & 0x00FF) | (DUMMY & 0xFF00)

    axi_master.writePush(0xc, DATA_0, STRB_0)
    axi_master.writePush(0xc, DATA_1, STRB_1)

    stepUntilAxiMasterDone()
    stepUntilAxiMasterDone()

    axi_master.readPush(0xc)
    var resp_0xc : axi_master.Resp = new axi_master.Resp(false, 0)
    stepUntilAxiMasterDone(resp => resp_0xc = resp)
    println(f"${t}%5d testWriteStrobe: expected ${EXPECTED}%04x, got ${resp_0xc.rd_data}%04x")
    assert(EXPECTED == resp_0xc.rd_data)
  }

  def setCoefsTo1(): Unit = {
    axi_master.writePush(0xc, 1)
    stepUntilAxiMasterDone()
  }

  //==========================================================================
  // main

  val driver = new ValidIfDriver(c.io.in_data, c.io.in_valid, peek, poke, println)
  val monitor = new ValidIfMonitor(c.io.out_data, c.io.out_valid, peek, poke, println)
  val axi_master = new AxiLiteMasterBfm(c.io.ctrl, peek, poke, println)

  println(f"${t}%5d Test starting...")
  step(5)

  getModuleInfo()
  testWriteStrobe()
  setCoefsTo1()

  step(5)

  driver.stimAppend(STIMULI)
  step(STIMULI.length + 10)

  val expected = STIMULI.map(model)
  val received = monitor.respGet()
  assert(expected == received)

  axi_master.readPush(8)
  var resp_recv_samples: axi_master.Resp = new axi_master.Resp(false, 0)
  stepUntilAxiMasterDone(resp => resp_recv_samples = resp)

  println(f"${t}%5d Number of samples sent: ${STIMULI.length}, core reports: ${resp_recv_samples.rd_data}")
  assert(STIMULI.length == resp_recv_samples.rd_data)

  println(f"${t}%5d Test finished.")
}
