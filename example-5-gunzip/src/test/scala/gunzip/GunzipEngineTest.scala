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

import chisel3.iotesters._

class GunzipEngineTest extends ChiselFlatSpec {

  val c_pkg = chisel3.Module.getClass.getPackage()
  println(s"${c_pkg.getSpecificationTitle} v ${c_pkg.getSpecificationVersion}")

  val t_pkg = chisel3.iotesters.Driver.getClass.getPackage()
  println(s"${t_pkg.getSpecificationTitle} v ${t_pkg.getSpecificationVersion}")

  "Gunzip tester" should "un-gzip a file" in {
    Driver.execute(Array("--backend-name", "verilator", "--fint-write-vcd"),
                   () => new GunzipEngine(getClass.getResource("/in.tar.gz"))) { c =>
      new GunzipTester(c, getClass.getResource("/in.tar.gz"))
    } should be(true)
  }

}
