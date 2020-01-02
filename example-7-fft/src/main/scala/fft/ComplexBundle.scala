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
import chisel3.core.FixedPoint
import chisel3.internal.firrtl.{BinaryPoint, Width}

class ComplexBundle(val w: Width, val bp: BinaryPoint) extends Bundle {
  val re = FixedPoint(w, bp)
  val im = FixedPoint(w, bp)

  override def toPrintable: Printable = {
    val re_dbl: String = Binary(re.asUInt()).toString
    p"ComplexBundle:\n" +
      p"  re : 0x${Hexadecimal(re.asUInt())}\n" +
      p"  im : 0x${Hexadecimal(im.asUInt())}\n"
  }

  def *(b_in: ComplexBundle): ComplexBundle = {
    // a * b = (a.re + j a.im) * (b.re + j b.im)
    //       = a.re * b.re - a.im * b.im + ...

    val a = RegNext(this)
    val b = RegNext(b_in)

    val a_re_x_b_re = RegNext(a.re * b.re)
    val a_im_x_b_im = RegNext(a.im * b.im)
    val a_re_x_b_im = RegNext(a.re * b.im)
    val a_im_x_b_re = RegNext(a.im * b.re)

    // TODO: here we likely have one reg too much, check the inferred DSP
    val out_re = RegNext(a_re_x_b_re - a_im_x_b_im)
    val out_im = RegNext(a_re_x_b_im + a_im_x_b_re)

    val tmp = Wire(new ComplexBundle(w, bp))
    tmp.re := out_re
    tmp.im := out_im

    tmp
  }

  def +(b: ComplexBundle): ComplexBundle = {
    val a = this

    val out_re = RegNext(a.re + b.re)
    val out_im = RegNext(a.im + b.im)

    val tmp = Wire(new ComplexBundle(w, bp))
    tmp.re := out_re
    tmp.im := out_im

    tmp
  }

  def -(b: ComplexBundle): ComplexBundle = {
    val a = this

    val out_re = RegNext(a.re - b.re)
    val out_im = RegNext(a.im - b.im)

    val tmp = Wire(new ComplexBundle(w, bp))
    tmp.re := out_re
    tmp.im := out_im

    tmp
  }
}
