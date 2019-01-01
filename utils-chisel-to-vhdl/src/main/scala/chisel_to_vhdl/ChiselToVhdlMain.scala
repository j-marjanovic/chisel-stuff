package chisel_to_vhdl

object ChiselToVhdlMain extends App {
  val translator = new Translator
  Iterator.continually(scala.io.StdIn.readLine)
    .takeWhile(_ != null)
    .foreach { l =>
      val s = translator.translate(l)
      println(s)
    }
}
