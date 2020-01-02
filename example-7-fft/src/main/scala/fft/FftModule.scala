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

import bfmtester.AxiStreamIf
import chisel3._
import chisel3.util._

/**
  * bits 17: 0 = real part
  * bits 49:32 = imag part
  */
class FftModule extends Module {
  val io = IO(new Bundle {
    val data_in = new AxiStreamIf(64.W)
    val data_out = Flipped(new AxiStreamIf(64.W))
  })

  val N: Int = 16

  val in_reg = RegInit(VecInit(Seq.fill(N) { Reg(new ComplexBundle(18.W, 16.BP)) }))
  val in_reg_cntr = RegInit(UInt(log2Ceil(N + 1).W), 0.U)
  val in_reg_vld = Wire(Bool())

  // load data in input regs
  when(io.data_in.tvalid && io.data_in.tready) {
    in_reg(in_reg_cntr).re := io.data_in.tdata(17, 0).asFixedPoint(16.BP)
    in_reg(in_reg_cntr).im := io.data_in.tdata(49, 32).asFixedPoint(16.BP)
  }

  // detect input shift reg full
  when(in_reg_vld) {
    in_reg_cntr := 0.U
  }.elsewhen(io.data_in.tready && io.data_in.tvalid) {
    in_reg_cntr := in_reg_cntr + 1.U
  }
  in_reg_vld := in_reg_cntr === (N - 1).U

  // shuffle input regs
  val in_shuffled = Wire(Vec(N, new ComplexBundle(18.W, 16.BP)))
  val m_shuffle = Module(new InputShuffle(N))
  m_shuffle.io.inp := in_reg
  in_shuffled := m_shuffle.io.out

  // fft stages
  val N_STAGES: Int = log2Ceil(N)
  println(s"FftModule: N_STAGES = ${N_STAGES}")

  val fft_reg_stage = Wire(Vec(N_STAGES + 1, Vec(N, new ComplexBundle(18.W, 16.BP))))
  val fft_reg_stage_vld = Wire(Vec(N_STAGES + 1, Bool()))

  fft_reg_stage(0) := in_shuffled
  fft_reg_stage_vld(0) := in_reg_vld

  for (i <- 0 until N_STAGES) {
    val fft_size: Int = Math.pow(2, i + 1).toInt
    println(s"FftModule: stage = ${i}")

    for (j <- 0 until N / fft_size) {
      println(s"FftModule:   fft = ${j}")
      for (k <- 0 until fft_size / 2) {
        println(s"FftModule:     butterfly = ${k}")
        val sel0 = j * fft_size + k
        val sel1 = j * fft_size + k + fft_size / 2
        println(s"FftModule:       sel = ${sel0}, ${sel1}")
        val butterfly = Module(new FftButterfly(18.W, 16.BP, k, N))
        butterfly.io.in0 := fft_reg_stage(i)(sel0)
        butterfly.io.in1 := fft_reg_stage(i)(sel1)
        butterfly.io.in_vld := fft_reg_stage_vld(i)
        fft_reg_stage(i + 1)(sel0) := butterfly.io.out0
        fft_reg_stage(i + 1)(sel1) := butterfly.io.out1

        if (j == 0 && k == 0) {
          fft_reg_stage_vld(i + 1) := butterfly.io.out_vld
        }
      }
    }

    /*
    when (fft_reg_stage_vld(i+1)) {
      printf(s"stage ${i+1} valid\n")
      for (j <- 0 until N) {
        printf(p"  out ${j} = ${fft_reg_stage(i+1)(j)}")
      }
    }
   */
  }

  // output register (counter will "park" itself at N at the end
  val out_reg_vld = RegInit(Bool(), false.B)
  val out_reg_cntr = RegInit(UInt(log2Ceil(N + 1 + 1).W), 0.U)

  when(fft_reg_stage_vld.last) {
    out_reg_cntr := 0.U
  }.elsewhen(io.data_out.tvalid && io.data_out.tready && out_reg_cntr <= N.U) {
    out_reg_cntr := out_reg_cntr + 1.U
  }

  when(fft_reg_stage_vld.last) {
    out_reg_vld := true.B
  }.elsewhen(io.data_out.tready && io.data_out.tvalid && io.data_out.tlast) {
    out_reg_vld := false.B
  }

  // output
  io.data_out.tdata := Cat(
    Cat(0.U(14.W), fft_reg_stage.last(out_reg_cntr).im.asUInt()),
    Cat(0.U(14.W), fft_reg_stage.last(out_reg_cntr).re.asUInt())
  )

  val in_tready = RegNext(io.data_out.tready)
  io.data_in.tready := in_tready
  io.data_out.tvalid := (out_reg_cntr < N.U) && out_reg_vld
  io.data_out.tlast := out_reg_cntr === (N - 1).U
  io.data_out.tuser := 0.U

}
