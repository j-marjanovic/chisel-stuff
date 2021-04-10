package poor_mans_system_ila

import bfmtester._
import chisel3._

import scala.collection.mutable.ListBuffer



class FilterTriggerTester(c: FilterTrigger) extends BfmTester(c) {

  def step_until_empty(): Unit = {
    while (true) {
      if (mod_slow_sig_gen.is_empty) {
        return
      }
      step(1)
    }
  }

  val mod_slow_sig_gen =
    new SlowSignalGen(c.io.data_in, bfm_peek, bfm_poke, println)

  mod_slow_sig_gen.append(0x00000007, 32)

  mod_slow_sig_gen.append(0x584d4443, 32)
  //mod_slow_sig_gen.append(0xFFFFFFFFL, 32)
  //mod_slow_sig_gen.append(0xFFFFFFFFL, 32)
  //mod_slow_sig_gen.append(0xFFFFFFFFL, 32)
  //mod_slow_sig_gen.append(0xFFFFFFFFL, 32)
  step_until_empty()

  /*
  mod_slow_sig_gen.append(0x55555555L, 32)
  mod_slow_sig_gen.append(0x55555555L, 32)
  mod_slow_sig_gen.append(0x55555555L, 32)
  mod_slow_sig_gen.append(0x55555555L, 32)
  mod_slow_sig_gen.append(0x55555555L, 32)
  step_until_empty()

  mod_slow_sig_gen.append(0x0, 32)
  mod_slow_sig_gen.append(0x0, 32)
  mod_slow_sig_gen.append(0x0, 32)
  mod_slow_sig_gen.append(0x0, 32)
  mod_slow_sig_gen.append(0x0, 32)
  step_until_empty()
  */
  mod_slow_sig_gen.append(0, 100)
  mod_slow_sig_gen.append(0xFFFFFFFFL, 20)
  mod_slow_sig_gen.append(0, 100)
  mod_slow_sig_gen.append(0xFFFFFFFFL, 20)
  mod_slow_sig_gen.append(0, 100)
  step_until_empty()


  //0x584d4443
}
