package chisel_to_vhdl

import org.scalatest.FlatSpec

import chisel_to_vhdl.{VhdlTokens => VT}

class VhdlTokenizerTest extends FlatSpec {
  "VHDL tokenizer" should "return empty list for empty string" in {
    assert(VhdlTokenizer.tokenize("") == List())
  }

  it should "remove empty spaces" in {
    assert(VhdlTokenizer.tokenize("    ") == List())
  }

  it should "match `Smth` as an identifier" in {
    assert(VhdlTokenizer.tokenize("Smth") ==
      List(
        VT.Id("Smth"),
      )
    )
  }

  it should "match `entity` as a keyword" in {
    assert(VhdlTokenizer.tokenize("entity") ==
      List(
        VT.Keyword("entity"),
      )
    )
  }

  it should "split `entity Smth is` correctly (split on whitespaces)" in {
    assert(VhdlTokenizer.tokenize("entity Smth is") ==
      List(
        VT.Keyword("entity"),
        VT.Id("Smth"),
        VT.Keyword("is")
      )
    )
  }

  it should "split `  ADDR_W: integer := 8` correctly (split on colons)" in {
    assert(VhdlTokenizer.tokenize("  ADDR_W: integer := 8") ==
      List(
        VT.Id("ADDR_W"),
        VT.OpLiteral(":"),
        VT.StdLibId("integer"),
        VT.OpLiteral(":="),
        VT.IntLit("8")
      )
    )
  }

  it should "split `    awready_wire <= awready_reg;` correctly (multi-char op)" in {
    assert(VhdlTokenizer.tokenize("    awready_wire <= awready_reg;") ==
      List(
        VT.Id("awready_wire"),
        VT.OpLiteral("<="),
        VT.Id("awready_reg"),
        VT.Semicol(";"),
      )
    )
  }

  it should "split `  S_AXI_AWADDR  : in std_logic_vector(ADDR_W-1 downto 0);` correctly (split on parens)" in {
    assert(VhdlTokenizer.tokenize("S_AXI_AWADDR  : in std_logic_vector(ADDR_W-1 downto 0);") ==
      List(
        VT.Id("S_AXI_AWADDR"),
        VT.OpLiteral(":"),
        VT.Keyword("in"),
        VT.StdLibId("std_logic_vector"),
        VT.ParensOpen("("),
        VT.Id("ADDR_W"),
        VT.OpLiteral("-"),
        VT.IntLit("1"),
        VT.Keyword("downto"),
        VT.IntLit("0"),
        VT.ParensClose(")"),
        VT.Semicol(";"),
      )
    )
  }

  it should "split `type t_state_read is (sReadIdle, sReadValid);` correctly (multi-char sep)" in {
    assert(VhdlTokenizer.tokenize("type t_state_read is (sReadIdle, sReadValid);") ==
      List(
        VT.Keyword("type"),
        VT.Id("t_state_read"),
        VT.Keyword("is"),
        VT.ParensOpen("("),
        VT.Id("sReadIdle"),
        VT.OpLiteral(","),
        VT.Id("sReadValid"),
        VT.ParensClose(")"),
        VT.Semicol(";"),
      )
    )
  }

  "String generator" should "generate string from tokens" in {
    val ts = List(
      VT.Keyword("type"),
      VT.Id("t_state_read"),
      VT.Keyword("is"),
      VT.ParensOpen("("),
      VT.Id("sReadIdle"),
      VT.OpLiteral(","),
      VT.Id("sReadValid"),
      VT.ParensClose(")"),
      VT.Semicol(";"),
    )
    assert(VhdlTokenizer.tokens_to_string(ts) == "type t_state_read is ( sReadIdle , sReadValid ) ;")
  }
}
