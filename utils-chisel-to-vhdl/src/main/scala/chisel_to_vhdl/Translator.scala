package chisel_to_vhdl

import scala.language.postfixOps
import scala.util.matching.Regex


class Translator {
  class TrRegex(val in: Regex, val out: Regex.Match => String)

  val TRANS_REGEXES: List[TrRegex] = List(
    // port, input, vector
    new TrRegex(
      in = "([ \\t]*)val ([A-Za-z_0-9]+) = Input\\(UInt\\(([A-Za-z_0-9]+).W\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : in std_logic_vector(${m.group(2)}-1 downto 0);"
    ),
    // port, output, vector
    new TrRegex(
      in = "([ \\t]*)val ([A-Za-z_0-9]+) = Output\\(UInt\\(([A-Za-z_0-9]+).W\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : out std_logic_vector(${m.group(2)}-1 downto 0);"
    ),
    // port, input, bool
    new TrRegex(
      in = "([ \\t]*)val ([A-Za-z_0-9]+) = Input\\(Bool\\(\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : in std_logic;"
    ),
    // port, output, bool
    new TrRegex(
      in = "([ \\t]*)val ([A-Za-z_0-9]+) = Output\\(Bool\\(\\)\\)".r,
      m => s"${m.group(1)}${m.group(2)} : out std_logic;"
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

