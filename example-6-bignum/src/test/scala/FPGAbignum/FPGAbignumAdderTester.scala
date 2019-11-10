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

  def testAddition(a: BigInt, b: BigInt, input_length: Int): Unit = {
    for (i <- 0 until input_length) {
      mst_a.stimAppend((a >> (8*i)) & 0xFF, if (i == input_length-1) 1 else 0)
      mst_b.stimAppend((b >> (8*i)) & 0xFF, if (i == input_length-1) 1 else 0)
    }

    println(f"${t}%5d ======================================================")

    try {
      step(TIMEOUT_STEPS)
      expect(false, "Timeout while waiting for TLAST")
      return
    } catch  {
      case e: AxiStreamSlaveLastRecv => println(f"${t}%5d tb: Got TLAST.")
    }

    val resp = slv_q.respGet()
    val resp_len = resp.length
    val resp_num_u : BigInt = resp.map(_._1).foldRight(BigInt(0))((a, b) => b *256 + a)
    val msb_mask: BigInt = BigInt(0x80) << ((resp_len-1)*8)

    println(f"${t}%5d Received response (unsign) = ${resp_num_u} (0x${resp_num_u}%x), length = ${resp_len}")

    val resp_num : BigInt = if ((resp_num_u & msb_mask) != 0) {
      val mask_xor : BigInt = (msb_mask << 1) - 1
      -((resp_num_u ^ mask_xor) + 1)
    } else {
      resp_num_u
    }

    println(f"${t}%5d Received response = ${resp_num} (0x${resp_num}%x), length = ${resp_len}")

    expect(resp_num == a + b, s"${a} + ${b} = exp ${a + b}, recv ${resp_num}")
  }


  //==========================================================================
  // modules

  val mst_a = new AxiStreamMaster(c.io.a, peek, poke, println, "Master A")
  val mst_b = new AxiStreamMaster(c.io.b, peek, poke, println, "Master B")

  val slv_q = new AxiStreamSlave(c.io.q, peek, poke, println)

  //==========================================================================
  // main
  val TIMEOUT_STEPS = 20

  println(f"${t}%5d Test starting...")


  testAddition(1, 1, 2); step(5)
  testAddition(2, 3, 2); step(5)
  testAddition(123, 54, 2); step(5)
  testAddition(0, 0, 3); step(5)
  testAddition(199, 211, 2); step(5)
  testAddition(-1, -2, 3); step(5)
  testAddition(1, 1, 2); step(5)
  testAddition(-5, -6, 2); step(5)
  testAddition(123456789, 123456789, 5); step(5)
  testAddition(-12345, 0, 5); step(5)
  testAddition(0, 0, 2); step(5)

  println(f"${t}%5d Test finished.")
}
