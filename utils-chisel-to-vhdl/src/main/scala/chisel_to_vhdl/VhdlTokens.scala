package chisel_to_vhdl

import scala.util.matching.Regex

object VhdlTokens {
  sealed abstract class Token {
    var s: String
    val m: Regex
  }

  case class OpLiteral(var s: String) extends Token {
    override val m: Regex = "(===)|(=/=)|(:=)|(<=)|([:=\\.\\+\\-\\,])".r
  }

  case class Id(var s: String) extends Token {
    override val m: Regex = "[a-zA-Z_][a-zA-Z0-9_]*".r
  }

  /** reserved keywords do not have their own regex, they are reassigned from
    * Id's after regex matching
    */
  case class Keyword(var s: String) extends Token {
    override val m: Regex = "---DO_NOT_MATCH---rnd number: 34509215869798602196".r
  }

  case class StdLibId(var s: String) extends Token {
    override val m: Regex = "---DO_NOT_MATCH---rnd number: 34509215869798602196".r
  }

  case class IntLit(var s: String) extends Token {
    override val m: Regex = "([0-9]+)|(0x[0-9a-fA-F]+)".r
  }

  case class CommentLit(var s: String) extends Token {
    override val m: Regex = "--.*".r
  }

  case class WhiteSpace(var s: String) extends Token {
    override val m: Regex = "[ \t]+".r
  }

  case class NewLine(var s: String) extends Token {
    override val m: Regex = "\n".r
  }

  case class ParensOpen(var s: String) extends Token {
    override val m: Regex = "\\(".r
  }

  case class ParensClose(var s: String) extends Token {
    override val m: Regex = "\\)".r
  }

  case class Semicol(var s: String) extends Token {
    override val m: Regex = ";".r
  }
}
