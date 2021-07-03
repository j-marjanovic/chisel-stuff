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

import bfmtester.util
import bfmtester.util.AxiLiteSubordinateGenerator
import bfmtester.util.AxiLiteSubordinateGenerator.{Reg, AreaMap}
import chisel3.stage.ChiselStage

object AxiTrafficGenMain extends App {
  new ChiselStage().emitSystemVerilog(
    new AxiTrafficGen(40, 128, 2),
    Array[String]("--target-dir", "ip_cores/axi_traffic_gen/hdl") ++ args
  )

  /*
  def gen_python(area_map: AreaMap, out_filename : String) = {
    val regs_sorted = area_map.els.filter(_.isInstanceOf[Reg])
      .sortBy(_.asInstanceOf[Reg].addr)

    val reg_addr_last = regs_sorted.last.asInstanceOf[AxiLiteSubordinateGenerator.Reg].addr

    var reg_dict: Map[Int, Reg] = Map[Int, Reg]()
    for (el <- area_map.els.filter(_.isInstanceOf[Reg])) {
      val reg = el.asInstanceOf[Reg]
      reg_dict += (reg.addr -> reg)
    }

    for (i <- 0 to reg_addr_last by 4) {
      val s: String = reg_dict.get(i).map(_.name).getOrElse(f"rsvd0x${i}%x")
      println(s"""("${s}", ctypes.c_uint32),""")
    }
  }



  gen_python(AxiTrafficGen.area_map, "test1.py")
   */
}
