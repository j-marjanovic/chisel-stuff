package lamports_bakery_algorithm

import chisel3.iotesters._

class DelayGenLfsrTest(c: DelayGenLfsr) extends PeekPokeTester(c) {

  poke(c.io.d, 0xaa12)
  poke(c.io.load, 1)
  step(1)
  poke(c.io.d, 0)
  poke(c.io.load, 0)
  step(1)

  for (_ <- 0 until 10) {
    poke(c.io.start, 1)
    step(1)
    poke(c.io.start, 0)
    step(math.pow(2, c.cntr_w).toInt + 5)
  }
}
