package chisel_to_vhdl


import chisel_to_vhdl.{ChiselTokens => CT}
import org.scalatest.FlatSpec


class ChiselTokenizerTest extends FlatSpec {

  "Chisel tokenizer" should "return empty list for empty string" in {
    assert(ChiselTokenizer.tokenize("") == List())
  }

  it should "remove empty spaces" in {
    assert(ChiselTokenizer.tokenize("    ") == List())
  }

  it should "match `Smth` as an identifier" in {
    assert(ChiselTokenizer.tokenize("Smth") ==
      List(
        CT.Id("Smth"),
      )
    )
  }

  it should "match `class` as a keyword" in {
    assert(ChiselTokenizer.tokenize("class") ==
      List(
        CT.Keyword("class"),
      )
    )
  }

  it should "split `class Smth {` correctly (split on whitespaces)" in {
    assert(ChiselTokenizer.tokenize("class Smth {") ==
      List(
        CT.Keyword("class"),
        CT.Id("Smth"),
        CT.CurlyOpen("{")
      )
    )
  }

  it should "split `  val ADDR_W: Int = 8` correctly (split on colons)" in {
    assert(ChiselTokenizer.tokenize("  val ADDR_W: Int = 8") ==
      List(
        CT.Keyword("val"),
        CT.Id("ADDR_W"),
        CT.OpLiteral(":"),
        CT.ChiselKeyword("Int"),
        CT.OpLiteral("="),
        CT.IntLit("8")
      )
    )
  }

  it should "split `    awready_wire := awready_reg` correctly (multi-char op)" in {
    assert(ChiselTokenizer.tokenize("    awready_wire := awready_reg") ==
      List(
        CT.Id("awready_wire"),
        CT.OpLiteral(":="),
        CT.Id("awready_reg")
      )
    )
  }

  it should "split `val S_AXI_AWADDR = Input(UInt(ADDR_W.W))` correctly (split on parens)" in {
    assert(ChiselTokenizer.tokenize("val S_AXI_AWADDR = Input(UInt(ADDR_W.W))") ==
      List(
        CT.Keyword("val"),
        CT.Id("S_AXI_AWADDR"),
        CT.OpLiteral("="),
        CT.ChiselKeyword("Input"),
        CT.ParensOpen("("),
        CT.ChiselKeyword("UInt"),
        CT.ParensOpen("("),
        CT.Id("ADDR_W"),
        CT.OpLiteral("."),
        CT.ChiselKeyword("W"),
        CT.ParensClose(")"),
        CT.ParensClose(")")
      )
    )
  }

  it should "split `val sReadIdle :: sReadValid :: Nil = Enum(2)` correctly (multi-char sep)" in {
    assert(ChiselTokenizer.tokenize("val sReadIdle :: sReadValid :: Nil = Enum(2)") ==
      List(
        CT.Keyword("val"),
        CT.Id("sReadIdle"),
        CT.OpLiteral("::"),
        CT.Id("sReadValid"),
        CT.OpLiteral("::"),
        CT.Id("Nil"),
        CT.OpLiteral("="),
        CT.ChiselKeyword("Enum"),
        CT.ParensOpen("("),
        CT.IntLit("2"),
        CT.ParensClose(")")
      )
    )
  }

  it should "parse single-line comment correctly" in {
    assert(ChiselTokenizer.tokenize("/* comment */") == List(CT.CommentLit("/* comment */")))
    // second test to check if we are not stuck in comments
    assert(ChiselTokenizer.tokenize("    awready_wire := awready_reg") ==
      List(
        CT.Id("awready_wire"),
        CT.OpLiteral(":="),
        CT.Id("awready_reg")
      )
    )
  }

  it should "parse multi-line comment correctly" in {
    assert(ChiselTokenizer.tokenize("/* comment") == List(CT.CommentLit("/* comment")))
    assert(ChiselTokenizer.tokenize("in comment") == List(CT.CommentLit("in comment")))
    assert(ChiselTokenizer.tokenize("*/") == List(CT.CommentLit("*/")))
  }

  it should "parse hex numbers correctly" in {
    assert(ChiselTokenizer.tokenize("val REG_ID      = 0x71711123.U(32.W) // ~ PIPELINE") ==
      List(
        CT.Keyword("val"),
        CT.Id("REG_ID"),
        CT.OpLiteral("="),
        CT.HexIntLit("0x71711123"),
        CT.OpLiteral("."),
        CT.ChiselKeyword("U"),
        CT.ParensOpen("("),
        CT.IntLit("32"),
        CT.OpLiteral("."),
        CT.ChiselKeyword("W"),
        CT.ParensClose(")"),
        CT.CommentLit("// ~ PIPELINE")
      )
    )
  }

  it should "parse long ints correctly" in {
    assert(ChiselTokenizer.tokenize("val C_ADDR_DEC_ERR = 0xdeadbeefL.U") ==
      List(
        CT.Keyword("val"),
        CT.Id("C_ADDR_DEC_ERR"),
        CT.OpLiteral("="),
        CT.HexIntLit("0xdeadbeefL"),
        CT.OpLiteral("."),
        CT.ChiselKeyword("U")
      )
    )
  }

  // https://www.scala-lang.org/files/archive/spec/2.12/01-lexical-syntax.html#identifiers
  it should "parse underscore as an keyword" in {
    assert(ChiselTokenizer.tokenize("import chisel3._") ==
      List(
        CT.Keyword("import"),
        CT.Id("chisel3"),
        CT.OpLiteral("."),
        CT.Keyword("_")
      )
    )
  }

  it should "parse pipe chars as operator literal" in {
    assert(ChiselTokenizer.tokenize("val REG_VERSION = (0 << 8) | 2") ==
      List(
        CT.Keyword("val"),
        CT.Id("REG_VERSION"),
        CT.OpLiteral("="),
        CT.ParensOpen("("),
        CT.IntLit("0"),
        CT.OpLiteral("<<"),
        CT.IntLit("8"),
        CT.ParensClose(")"),
        CT.OpLiteral("|"),
        CT.IntLit("2")
      )
    )
  }

  it should "erase identifiers and leave keywords and op intact" in {
    val in_list = List(CT.Keyword("val"), CT.Id("awready_wire"), CT.OpLiteral(":="), CT.Id("awready_reg"))
    val exp_list = List(CT.Keyword("val"), CT.Id(""), CT.OpLiteral(":="), CT.Id(""))
    assert(ChiselTokenizer.erase_identifiers(in_list) == exp_list)
  }
}
