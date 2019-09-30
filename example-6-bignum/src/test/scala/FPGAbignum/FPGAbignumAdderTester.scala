/*
MIT License

Copyright (c) 2018-2019 Jan Marjanovic

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

package FPGAbignum

import scala.reflect.runtime.universe._
import chisel3.iotesters.PeekPokeTester

import scala.reflect.runtime.universe


class FPGAbignumAdderTester(c: FPGAbignumAdder) extends PeekPokeTester(c) {


  //==========================================================================
  // step

  val rm = runtimeMirror(getClass.getClassLoader)
  val im = rm.reflect(this)
  val members = im.symbol.typeSignature.members
  def bfms: Iterable[universe.Symbol] = members.filter(_.typeSignature <:< typeOf[ChiselBfm])

  def stepSingle(): Unit = {
    for (bfm <- bfms) {
      im.reflectField(bfm.asTerm).get.asInstanceOf[ChiselBfm].update(t)
    }
    super.step(1)
  }

  override def step(n: Int): Unit = {
    for(_ <- 0 until n) {
      stepSingle()
    }
  }

  //==========================================================================
  // helper functions

  //==========================================================================
  // modules

  val mst_a = new AxiStreamMaster(c.io.a, peek, poke, println, "Master A")
  val mst_b = new AxiStreamMaster(c.io.b, peek, poke, println, "Master B")

  val slv_q = new AxiStreamSlave(c.io.q, peek, poke, println)

  //==========================================================================
  // main

  println(f"${t}%5d Test starting...")

  val TIMEOUT_STEPS = 20

  mst_a.stimAppend(1, 0)
  mst_b.stimAppend(1, 0)

  mst_a.stimAppend(0, 1)
  mst_b.stimAppend(0, 1)

  try {
    step(TIMEOUT_STEPS)
    expect(false, "Timeout while waiting for TLAST")
  } catch  {
    case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Got TLAST.")
  }

  val resp1 = slv_q.respGet()
  val resp_nr1 = resp1.map(_._1).foldRight(BigInt(0))((a, b) => b*256 + a)
  println(f"${t}%5d Response = ${resp_nr1}.")
  expect(resp_nr1 == 2, "1 + 1 = 2") // TODO: move to helper function

  step(10)


  // case 2 (TODO: turn this into helper function)
  mst_a.stimAppend(123, 0)
  mst_b.stimAppend(45, 0)

  mst_a.stimAppend(0, 1)
  mst_b.stimAppend(0, 1)

  try {
    step(TIMEOUT_STEPS)
    expect(false, "Timeout while waiting for TLAST")
  } catch  {
    case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Seen TLAST.")
  }

  val resp2 = slv_q.respGet()
  val resp_nr2 = resp2.map(_._1).foldRight(BigInt(0))((a, b) => b*256 + a)
  println(f"${t}%5d Response = ${resp_nr2}.")
  expect(resp_nr2 == 123 + 45, "calc2") // TODO: move to helper function

  try {
    step(10)
  } catch  {
    case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Seen TLAST.")
  }


  println(f"${t}%5d Test finished.")
}
