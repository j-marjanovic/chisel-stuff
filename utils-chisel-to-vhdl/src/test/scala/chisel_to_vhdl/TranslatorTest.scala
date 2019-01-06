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

  it should "translate module instantations" in {
    val t = new Translator
    val cl = "  val axi_slave = Module(new StatusAxiSlave)"
    val vl = "  axi_slave: entity work.StatusAxiSlave\n  port map(\n  );"
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

  it should "translate single-line comments" in {
    val t = new Translator
    val cl = "  // hello"
    val vl = "  -- hello"
    val tr = t.translate(cl)
    assert(vl == tr)
  }

  it should "translate empty line" in {
    val t = new Translator
    val cl = ""
    val vl = ""
    val tr = t.translate(cl)
    assert(vl == tr)
  }
}
