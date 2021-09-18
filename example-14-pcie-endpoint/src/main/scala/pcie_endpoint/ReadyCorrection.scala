package pcie_endpoint

import chisel3._

/**
  *                       -1     0      1      2      3      4
  *                       |      |      |      |      |      |
  *                         +------+
  * ready at the core       |      |
  *                     ----+      +--------------------------------
  *                                              +------+
  * ready at the app                             |      |
  *                     -------------------------+      +-----------
  *                         +---------------------------+
  * valid at the app        |                           |
  *                     ----+                           +-----------
  *                                              +------+
  * valid at the core                            |      |
  *                     -------------------------+      +-----------
  */

class ReadyCorrection extends MultiIOModule {
  val core_ready = IO(Input(Bool()))
  val app_ready = IO(Output(Bool()))
  val core_valid = IO(Output(Bool()))
  val app_valid = IO(Input(Bool()))

  val ready_ppp = RegNext(RegNext(RegNext(core_ready)))

  app_ready := ready_ppp
  core_valid := ready_ppp && app_valid
}
