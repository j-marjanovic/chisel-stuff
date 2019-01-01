package chisel_to_vhdl

import org.scalatest.FlatSpec

class TranslatorTest extends FlatSpec {

  "Translator" should "translate input/output vector ports" in {
    val t = new Translator
    val cl = "val S_AXI_AWADDR = Input(UInt(ADDR_W.W))"
    val vl = "S_AXI_AWADDR : in std_logic_vector(ADDR_W-1 downto 0);"
    val tr = t.translate(cl)
    assert(vl == tr)
  }

  it should "translate input/output boolean ports" in {
    val t = new Translator
    val cl = "val S_AXI_AWREADY = Output(Bool())"
    val vl = "S_AXI_AWREADY : out std_logic;"
    val tr = t.translate(cl)
    assert(vl == tr)
  }

  it should "preserve leading whitespace" in {
    val t = new Translator
    val cl = "  \tval S_AXI_AWREADY = Output(Bool())"
    val vl = "  \tS_AXI_AWREADY : out std_logic;"
    val tr = t.translate(cl)
    assert(vl == tr)
  }
}
