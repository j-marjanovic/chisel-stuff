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

import chisel3.util.DecoupledIO
import chisel3.{Bits, UInt}

import scala.collection.mutable.ListBuffer

/**
  * Driver for data+valid interface - drives everything which gets appended
  * to stimulus on the interface
  */
class DecoupledDriver(val iface: DecoupledIO[UInt],
                      val peek: Bits => BigInt,
                      val poke: (Bits, BigInt) => Unit,
                      val println: String => Unit) extends DecoupledBfm{
  private var stim = ListBuffer[BigInt]()
  private var driving : Boolean = false

  def stimAppend(x: BigInt): Unit = {
    stim += x
  }

  def stimAppend(ls: List[BigInt]): Unit = {
    stim ++= ls
  }

  private def printWithBg(s: String): Unit = {
    // black on orange
    println("\u001b[30;48;5;166m" + s + "\u001b[39;49m")
  }

  private object State extends Enumeration {
    type State = Value
    val Idle, Drive = Value
  }
  private var state = State.Idle

  private var ready : Boolean = peek(iface.ready) > 0

  def update(t: Long): Unit = {
    def driveFromStimList(): Unit = {
      poke(iface.valid, 1)
      val d = stim.remove(0)
      poke(iface.bits, d)
    }

    def release(): Unit = {
      poke(iface.valid, 0)
      poke(iface.bits, 0)
    }

    state match {
      case State.Idle => {
        if (stim.nonEmpty) {
          driveFromStimList
          state = State.Drive
        }
      }
      case State.Drive => {
        if (ready) {
          val data = peek(iface.bits)
          printWithBg(f"${t}%5d Driver: sent ${data}")

          if (stim.nonEmpty) {
            driveFromStimList
            state = State.Drive
          } else {
            release
            state = State.Idle
          }
        } else {
          //printWithBg(f"${t}%5d Driver: backpressure")
        }
      }
    }

    ready = peek(iface.ready) > 0
  }
}
