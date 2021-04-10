package poor_mans_system_ila

import bfmtester._
import chisel3._

import scala.collection.mutable.ListBuffer



class CorrelatorChannelTester(c: CorrelatorChannel) extends BfmTester(c) {
    def step_until_empty(): Unit = {
    while (true) {
      if (mod_slow_sig_gen.is_empty()) {
        return
      }
      step(1)
    }
  }

  val mod_slow_sig_gen = new SlowSignalGen(c.io.inp, bfm_peek, bfm_poke, println)

  mod_slow_sig_gen.append(0xFFFFFFFF, 32)
  mod_slow_sig_gen.append(0xFFFFFFFF, 32)
  mod_slow_sig_gen.append(0xFFFFFFFF, 32)
  step_until_empty()

  mod_slow_sig_gen.append(0x00000000, 32)
  mod_slow_sig_gen.append(0x00000000, 32)
  mod_slow_sig_gen.append(0x00000000, 32)
  mod_slow_sig_gen.append(0x00000000, 32)

  step_until_empty()

  mod_slow_sig_gen.append(0x584d4443, 32)
  mod_slow_sig_gen.append(0x584d4443, 32)
  step_until_empty()

  for (_ <- 0 to 7) {
    mod_slow_sig_gen.append(0, 1)
    mod_slow_sig_gen.append(0x584d4443, 32)
    mod_slow_sig_gen.append(0x584d4443, 32)
    step_until_empty()
  }

}
