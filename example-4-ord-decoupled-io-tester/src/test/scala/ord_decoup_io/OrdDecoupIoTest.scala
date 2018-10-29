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

package ord_decoup_io

import chisel3.iotesters.ChiselFlatSpec

class OrdDecoupIoTest extends ChiselFlatSpec {

  val c_pkg = chisel3.Module.getClass.getPackage()
  println(s"${c_pkg.getSpecificationTitle} version ${c_pkg.getSpecificationVersion}")

  val t_pkg = chisel3.iotesters.Driver.getClass.getPackage()
  println(s"${t_pkg.getSpecificationTitle} version ${t_pkg.getSpecificationVersion}")

  "Ord Decoup Io tester" should "compare expected and obtained response" in {
    assertTesterPasses { new OrdDecoupIoTester }
  }
}
