package chisel_to_vhdl

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object ChiselTokenizer {
  private val RESERVED_KEYWORDS: List[String] = List(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "true",
    "try",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield",
    "_"
  )

  private val CHISEL_KEYWORDS: List[String] = List(
    "Enum",
    "Bundle",
    "Int",
    "U",
    "S",
    "W",
    "Input",
    "Output",
    "UInt"
  )

  val TOKENS: List[String => ChiselTokens.Token] = List(
    ChiselTokens.OpLiteral,
    ChiselTokens.Id,
    // Keyword is excluded on purpose
    // ChiselKeyword is excluded on purpose
    ChiselTokens.IntLit,
    ChiselTokens.HexIntLit,
    ChiselTokens.FloatLit,
    ChiselTokens.BoolLit,
    ChiselTokens.StringLit,
    ChiselTokens.CommentLit,
    ChiselTokens.WhiteSpace,
    ChiselTokens.NewLine,
    ChiselTokens.ParensOpen,
    ChiselTokens.ParensClose,
    ChiselTokens.CurlyOpen,
    ChiselTokens.CurlyClose,
    ChiselTokens.SquareOpen,
    ChiselTokens.SquareClose,
    ChiselTokens.Comma
  )

  var in_comments = false

  /** Splits strings in Chisel HDL into tokens
    *
    * @param in_str a line from Chisel HDL
    * @return list of tokens
    */
  def tokenize(s: String): List[ChiselTokens.Token] = {
    val tokens: ListBuffer[ChiselTokens.Token] = ListBuffer()
    var cur_pos: Int = 0

    while (cur_pos < s.length) {
      val substr = s.substring(cur_pos)

      val all_matches: List[(Option[Regex.Match], String => ChiselTokens.Token)] =
        TOKENS.map(t => Tuple2(t("").m.findFirstMatchIn(substr), t))

      val ok_matches: List[(Regex.Match, String => ChiselTokens.Token)] =
        all_matches
          .filter(mt => mt._1.isDefined && mt._1.get.start == 0)
          .map(mt => (mt._1.get, mt._2))

      // handle comments
      if (in_comments && !s.contains("*/")) {
        return List(ChiselTokens.CommentLit(s))
      }

      // get the longest token
      val m = ok_matches.maxBy(_._1.end)
      val token = m._2(m._1.toString())

      token match {
        case id: ChiselTokens.Id      =>
          if (RESERVED_KEYWORDS contains id.s) {
            tokens += ChiselTokens.Keyword(id.s)
          } else if (CHISEL_KEYWORDS contains id.s) {
            tokens += ChiselTokens.ChiselKeyword(id.s)
          } else {
            tokens += id
          }
        case c: ChiselTokens.CommentLit =>
          if (c.s.contains("/*") && !c.s.contains("*/")) {
            in_comments = true
          } else if (c.s.contains("*/")) {
            in_comments = false
          }
          tokens += c
        case ChiselTokens.WhiteSpace(_)  =>
          null
        case ChiselTokens.NewLine(_)     =>
          null
        case c: ChiselTokens.Token =>
          tokens += c
      }
      cur_pos += m._1.end
    }

    tokens.toList
  }

  def erase_identifiers(input: ChiselTokens.Token): ChiselTokens.Token = {
    input match {
      case _: ChiselTokens.Id =>
        ChiselTokens.Id("")
      case _: ChiselTokens.IntLit =>
        ChiselTokens.IntLit("")
      case _: ChiselTokens.FloatLit =>
        ChiselTokens.FloatLit("")
      case _: ChiselTokens.BoolLit =>
        ChiselTokens.BoolLit("")
      case _: ChiselTokens.StringLit =>
        ChiselTokens.StringLit("")
      case _: ChiselTokens.CommentLit =>
        ChiselTokens.CommentLit("")
      case c: ChiselTokens.Token =>
        c
    }
  }

  def erase_identifiers(input: List[ChiselTokens.Token]): List[ChiselTokens.Token] = {
    input.map(erase_identifiers)
  }
}
