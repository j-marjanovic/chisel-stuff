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

package axi_proxy

import bfmtester.util.AxiLiteSubordinateGenerator
import chisel3.stage.ChiselStage

object AxiProxyMain extends App {
  new ChiselStage().emitSystemVerilog(
    new AxiProxy,
    Array[String]("--target-dir", "ip_cores/axi_proxy/hdl") ++ args
  )

  new java.io.File("ip_cores/axi_proxy/drivers").mkdirs
  AxiLiteSubordinateGenerator.gen_python_header(
    AxiProxy.area_map,
    "_AxiProxyMap",
    "ip_cores/axi_proxy/drivers/_AxiProxyMap.py"
  )
  AxiLiteSubordinateGenerator.gen_c_header(
    AxiProxy.area_map,
    "AxiProxy",
    "ip_cores/axi_proxy/drivers/AxiProxy_regs.hpp",
    use_cpp_header = true
  )
}
