package chisel_to_vhdl

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

object VhdlTokenizer {
  private val RESERVED_KEYWORDS: List[String] = List(
    "abs",
    "access",
    "after",
    "alias",
    "all",
    "and",
    "architecture",
    "array",
    "assert",
    "attribute",
    "begin",
    "block",
    "body",
    "buffer",
    "bus",
    "case",
    "component",
    "configuration",
    "constant",
    "disconnect",
    "downto",
    "else",
    "elsif",
    "end",
    "entity",
    "exit",
    "file",
    "for",
    "function",
    "generate",
    "generic",
    "group",
    "guarded",
    "if",
    "impure",
    "in",
    "inertial",
    "inout",
    "is",
    "label",
    "library",
    "linkage",
    "literal",
    "loop",
    "map",
    "mod",
    "nand",
    "new",
    "next",
    "nor",
    "not",
    "null",
    "of",
    "on",
    "open",
    "or",
    "others",
    "out",
    "package",
    "port",
    "postponed",
    "procedure",
    "process",
    "pure",
    "range",
    "record",
    "register",
    "reject",
    "rem",
    "report",
    "return",
    "rol",
    "ror",
    "select",
    "severity",
    "shared",
    "signal",
    "sla",
    "sll",
    "sra",
    "srl",
    "subtype",
    "then",
    "to",
    "transport",
    "type",
    "unaffected",
    "units",
    "until",
    "use",
    "variable",
    "wait",
    "when",
    "while",
    "with",
    "xnor",
    "xor"
  )

  private val STD_LIB_IDS: List[String] = List(
    "integer",
    "std_logic_vector",
    "std_logic",
  )

  val TOKENS: List[String => VhdlTokens.Token] = List(
    VhdlTokens.OpLiteral,
    VhdlTokens.Id,
    // Keyword is excluded on purpose
    // StdLibId is excluded on purpose
    VhdlTokens.IntLit,
    VhdlTokens.CommentLit,
    VhdlTokens.WhiteSpace,
    VhdlTokens.NewLine,
    VhdlTokens.Semicol,
    VhdlTokens.ParensOpen,
    VhdlTokens.ParensClose,
  )

  def tokenize(s: String): List[VhdlTokens.Token] = {
    val tokens: ListBuffer[VhdlTokens.Token] = ListBuffer()
    var cur_pos: Int = 0

    while (cur_pos < s.length) {
      val substr = s.substring(cur_pos)

      val all_matches: List[(Option[Regex.Match], String => VhdlTokens.Token)] =
        TOKENS.map(t => Tuple2(t("").m.findFirstMatchIn(substr), t))

      val ok_matches: List[(Regex.Match, String => VhdlTokens.Token)] =
        all_matches
        .filter(mt => mt._1.isDefined && mt._1.get.start == 0)
        .map(mt => (mt._1.get, mt._2))

      val m = ok_matches.maxBy(_._1.end)

      val token = m._2(m._1.toString())

      token match {
        case id: VhdlTokens.Id      =>
          if (RESERVED_KEYWORDS contains id.s) {
            tokens += VhdlTokens.Keyword(id.s)
          } else if (STD_LIB_IDS contains id.s) {
            tokens += VhdlTokens.StdLibId(id.s)
          } else {
            tokens += id
          }
        case VhdlTokens.WhiteSpace(_)  =>
          null
        case VhdlTokens.NewLine(_)     =>
          null
        case c: VhdlTokens.Token =>
          tokens += c
      }
      cur_pos += m._1.end
    }

    tokens.toList
  }

  def tokens_to_string(l: List[VhdlTokens.Token]): String = {
    l.map(_.s).reduce((a, b) => a + " " + b)
  }
}
