/*
Copyright (c) 2021 Jan Marjanovic

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

package axi_traffic_gen

import bfmtester.util.AxiLiteSubordinateGenerator
import chisel3.stage.ChiselStage

import java.io.File

object AxiTrafficGenMain extends App {
  new ChiselStage().emitSystemVerilog(
    new AxiTrafficGen(40, 128, 2),
    Array[String]("--target-dir", "ip_cores/axi_traffic_gen/hdl") ++ args
  )

  val directory: File = new File("ip_cores/axi_traffic_gen/drivers");
  if (!directory.exists()) {
    directory.mkdirs()
  }

  AxiLiteSubordinateGenerator.gen_python_header(
    AxiTrafficGen.area_map,
    "_AxiTrafficGenMap",
    "ip_cores/axi_traffic_gen/drivers/_AxiTrafficGenMap.py"
  )

}
