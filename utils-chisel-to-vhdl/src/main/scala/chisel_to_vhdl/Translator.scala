package chisel_to_vhdl

import scala.language.postfixOps
import scala.util.matching.Regex


class Translator {
  class TrRegex(val in: Regex, val out: Regex.Match => String)

  val RE_WS = "([ \\t]*)"
  val RE_ID = "([A-Za-z_]+[A-Za-z_0-9]*)"
  val RE_ID_NR = "([A-Za-z_0-9]+)"
  val RE_IO = "(In|Out)put"

  val TRANS_REGEXES: List[TrRegex] = List(
    // port, vector
    new TrRegex(
      in = s"${RE_WS}val $RE_ID = ${RE_IO}\\(UInt\\(${RE_ID_NR}.W\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : ${m.group(3).toLowerCase} std_logic_vector(${m.group(4)}-1 downto 0);"
    ),
    // port, bool
    new TrRegex(
      in = s"${RE_WS}val $RE_ID = ${RE_IO}\\(Bool\\(\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : ${m.group(3).toLowerCase} std_logic;"
    ),
    // instantiations
    new TrRegex(
      in = s"${RE_WS}val $RE_ID = Module\\(new $RE_ID\\)".r,
      m => {
        val ws = m.group(1)
        val in = m.group(2)
        val mn = m.group(3)
        s"$ws$in: entity work.$mn\n${ws}port map(\n${ws});"
      }
    ),
    // comments
    new TrRegex(
      in = s"${RE_WS}//(.*)".r,
      m => s"${m.group(1)}--${m.group(2)}"
    ),
    // emtpy string
    new TrRegex(
      in = "".r,
      m => ""
    ),
  )

  def translate(line: String): String = {

    TRANS_REGEXES.foreach(tr => {
      val m = tr.in.findFirstMatchIn(line)
      if (m.isDefined) {
        return tr.out(m.get)
      }
    })

    s"-- could not translate: $line"
  }
}

