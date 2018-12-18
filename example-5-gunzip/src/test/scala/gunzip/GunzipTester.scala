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

package gunzip

import java.io.BufferedReader

import chisel3._
import chisel3.core.Data
import chisel3.iotesters.PeekPokeTester
import chisel3.util.DecoupledIO

import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._



object AllDone extends Exception { }



class GunzipTester(c: GunzipEngine, val filename: java.net.URL) extends PeekPokeTester(c) {

  // stim
  val bits = GunzipHeaderParser.get_bitstream(filename)
  var inp_buffer = ListBuffer[BigInt]()

  try {
    while (true) {
      inp_buffer += bits.getBits(1)
    }
  } catch {
    case _: java.util.NoSuchElementException => null
  }

  val inp = inp_buffer.toList

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

  val monitor = new DecoupledMonitor(c.io.data_out, peek, poke, println)
  val driver = new DecoupledDriver(c.io.data_in, peek, poke, println)

  driver.stimAppend(inp)

  println(f"${t}%5d Test starting...")
  try {
    step(30000)
  } catch {
    case AllDone => println(f"${t}%5d Seen LAST, finishing sim")
  }

  println(f"${t}%5d Test done, checking results...")


  val recv_bytes: Array[Byte] = monitor.respGet().map(_.toByte).toArray

  val in = getClass.getResourceAsStream("/out.tar")
  val bis = new BufferedReader(new java.io.InputStreamReader(in))
  val golden_bytes = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

  println(f"${t}%5d Diff in format received / golden")


  Hexdump.diff(recv_bytes, golden_bytes)
}
