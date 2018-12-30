-- MIT License
--
-- Copyright (c) 2018 Jan Marjanovic
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
-- SOFTWARE.

library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

library STD;
use std.env.all;

library uvvm_util;
context uvvm_util.uvvm_util_context;

library uvvm_vvc_framework;
use uvvm_vvc_framework.ti_vvc_framework_support_pkg.all;

library bitvis_vip_axilite;

library bitvis_vip_axistream;
use bitvis_vip_axistream.axistream_bfm_pkg.t_axistream_if;
use bitvis_vip_axistream.axistream_bfm_pkg.t_axistream_bfm_config;
use bitvis_vip_axistream.axistream_bfm_pkg.C_AXISTREAM_BFM_CONFIG_DEFAULT;

entity pipelinewithaxilite_th is
end entity;


architecture behav of pipelinewithaxilite_th is
  -- components (needed for using Verilog modules) in VHDL
  component PipelineWithAxiLite is
    port (
      clock : in std_logic;
      reset : in std_logic;
      io_ctrl_AW_ready : out std_logic;
      io_ctrl_AW_valid : in std_logic;
      io_ctrl_AW_bits : in std_logic_vector(3 downto 0);
      io_ctrl_W_ready : out std_logic;
      io_ctrl_W_valid : in std_logic;
      io_ctrl_W_bits_wdata : in std_logic_vector(31 downto 0);
      io_ctrl_W_bits_wstrb : in std_logic_vector(3 downto 0);
      io_ctrl_B_ready : in std_logic;
      io_ctrl_B_valid : out std_logic;
      io_ctrl_B_bits : out std_logic_vector(1 downto 0);
      io_ctrl_AR_ready : out std_logic;
      io_ctrl_AR_valid : in std_logic;
      io_ctrl_AR_bits : in std_logic_vector(3 downto 0);
      io_ctrl_R_ready : in std_logic;
      io_ctrl_R_valid : out std_logic;
      io_ctrl_R_bits_rdata : out std_logic_vector(31 downto 0);
      io_ctrl_R_bits_rresp : out std_logic_vector(1 downto 0);
      io_in_data : in std_logic_vector(15 downto 0);
      io_in_valid : in std_logic;
      io_out_data : out std_logic_vector(15 downto 0);
      io_out_valid : out std_logic
    );
  end component;

  -- constants
  constant C_CLK_PERIOD : time := 10 ns;

  -- signal definitions
  signal clock : std_logic;
  signal reset : std_logic;

  -- AXI4-Lite slave
  signal S_AXI_AWREADY : std_logic;
  signal S_AXI_AWVALID : std_logic;
  signal S_AXI_AWPROT : std_logic_vector(2 downto 0);
  signal S_AXI_AWADDR : std_logic_vector(3 downto 0);
  signal S_AXI_WREADY : std_logic;
  signal S_AXI_WVALID : std_logic;
  signal S_AXI_WDATA : std_logic_vector(31 downto 0);
  signal S_AXI_WSTRB : std_logic_vector(3 downto 0);
  signal S_AXI_BREADY : std_logic;
  signal S_AXI_BVALID : std_logic;
  signal S_AXI_BRESP : std_logic_vector(1 downto 0);
  signal S_AXI_ARREADY : std_logic;
  signal S_AXI_ARVALID : std_logic;
  signal S_AXI_ARPROT : std_logic_vector(2 downto 0);
  signal S_AXI_ARADDR : std_logic_vector(3 downto 0);
  signal S_AXI_RREADY : std_logic;
  signal S_AXI_RVALID : std_logic;
  signal S_AXI_RDATA : std_logic_vector(31 downto 0);
  signal S_AXI_RRESP : std_logic_vector(1 downto 0);

  -- AXI4-Stream input (DUT is slave)
  signal S_AXIS_IN_TDATA : std_logic_vector(15 downto 0);
  signal S_AXIS_IN_TVALID : std_logic;
  signal axistream_if_m : t_axistream_if(tdata(16 -1 downto 0),
                                          tkeep(16/8-1 downto 0),
                                          tuser(0 downto 0),
                                          tstrb(18/8-1 downto 0),
                                          tid  (0 downto 0),
                                          tdest(0 downto 0)
                                        );
  constant C_AXISTREAM_M_CONF : t_axistream_bfm_config := (
    max_wait_cycles             => 100,
    max_wait_cycles_severity    => ERROR,
    clock_period                => C_CLK_PERIOD,
    clock_period_margin         => 0 ns,
    clock_margin_severity       => TB_ERROR,
    setup_time                  => C_CLK_PERIOD / 4,
    hold_time                   => C_CLK_PERIOD / 4,
    byte_endianness             => FIRST_BYTE_RIGHT,
    check_packet_length         => false,
    protocol_error_severity     => ERROR,
    ready_low_at_word_num       => 0,
    ready_low_duration          => 0,
    ready_default_value         => '0',
    id_for_bfm                  => ID_BFM,
    id_for_bfm_wait             => ID_BFM_WAIT,
    id_for_bfm_poll             => ID_BFM_POLL
  );

  -- AXI4-Stream output (DUT is master)
  signal M_AXIS_OUT_TDATA : std_logic_vector(15 downto 0);
  signal M_AXIS_OUT_TVALID : std_logic;
  signal axistream_if_s : t_axistream_if(tdata(16 -1 downto 0),
                                          tkeep(16/8-1 downto 0),
                                          tuser(0 downto 0),
                                          tstrb(18/8-1 downto 0),
                                          tid  (0 downto 0),
                                          tdest(0 downto 0)
                                        );

  -- used by the TB to signal to BFM to stop (DUT does not have TLAST output)
  signal TB_AXIS_OUT_TLAST : std_logic := '0';
begin


  proc_clock: process
  begin
    clock <= '0', '1' after C_CLK_PERIOD / 2;
    wait for C_CLK_PERIOD;
  end process;

  proc_reset: process
  begin
    reset <= '1';
    wait until rising_edge(clock);
    reset <= '0';
    wait;
  end process;

  i_ti_uvvm_engine : entity uvvm_vvc_framework.ti_uvvm_engine;

  dut: PipelineWithAxiLite port map (
    clock => clock,
    reset => reset,
    io_ctrl_AW_ready => S_AXI_AWREADY,
    io_ctrl_AW_valid => S_AXI_AWVALID,
    io_ctrl_AW_bits => S_AXI_AWADDR,
    io_ctrl_W_ready => S_AXI_WREADY,
    io_ctrl_W_valid => S_AXI_WVALID,
    io_ctrl_W_bits_wdata => S_AXI_WDATA,
    io_ctrl_W_bits_wstrb => S_AXI_WSTRB,
    io_ctrl_B_ready => S_AXI_BREADY,
    io_ctrl_B_valid => S_AXI_BVALID,
    io_ctrl_B_bits => S_AXI_BRESP,
    io_ctrl_AR_ready => S_AXI_ARREADY,
    io_ctrl_AR_valid => S_AXI_ARVALID,
    io_ctrl_AR_bits => S_AXI_ARADDR,
    io_ctrl_R_ready => S_AXI_RREADY,
    io_ctrl_R_valid => S_AXI_RVALID,
    io_ctrl_R_bits_rdata => S_AXI_RDATA,
    io_ctrl_R_bits_rresp => S_AXI_RRESP,
    io_in_data => S_AXIS_IN_TDATA,
    io_in_valid => S_AXIS_IN_TVALID,
    io_out_data => M_AXIS_OUT_TDATA,
    io_out_valid => M_AXIS_OUT_TVALID
  );


  i1_axilite_vvc: entity bitvis_vip_axilite.axilite_vvc
  generic map (
    GC_ADDR_WIDTH => 4,
    GC_DATA_WIDTH => 32,
    GC_INSTANCE_IDX => 1
  )
  port map (
    clk => clock,
    axilite_vvc_master_if.write_address_channel.awaddr => S_AXI_AWADDR,
    axilite_vvc_master_if.write_address_channel.awvalid => S_AXI_AWVALID,
    axilite_vvc_master_if.write_address_channel.awprot => S_AXI_AWPROT,
    axilite_vvc_master_if.write_address_channel.awready => S_AXI_AWREADY,

    axilite_vvc_master_if.write_data_channel.wdata  => S_AXI_WDATA  ,
    axilite_vvc_master_if.write_data_channel.wstrb  => S_AXI_WSTRB  ,
    axilite_vvc_master_if.write_data_channel.wvalid => S_AXI_WVALID ,
    axilite_vvc_master_if.write_data_channel.wready => S_AXI_WREADY ,

    axilite_vvc_master_if.write_response_channel.bready   => S_AXI_BREADY  ,
    axilite_vvc_master_if.write_response_channel.bresp    => S_AXI_BRESP   ,
    axilite_vvc_master_if.write_response_channel.bvalid   => S_AXI_BVALID  ,

    axilite_vvc_master_if.read_address_channel.araddr  => S_AXI_ARADDR  ,
    axilite_vvc_master_if.read_address_channel.arvalid => S_AXI_ARVALID ,
    axilite_vvc_master_if.read_address_channel.arprot  => S_AXI_ARPROT  ,
    axilite_vvc_master_if.read_address_channel.arready => S_AXI_ARREADY ,

    axilite_vvc_master_if.read_data_channel.rready  => S_AXI_RREADY  ,
    axilite_vvc_master_if.read_data_channel.rdata   => S_AXI_RDATA   ,
    axilite_vvc_master_if.read_data_channel.rresp   => S_AXI_RRESP   ,
    axilite_vvc_master_if.read_data_channel.rvalid  => S_AXI_RVALID
  );

  -- master -> generates data
  i1_axistream_vvc: entity bitvis_vip_axistream.axistream_vvc
  generic map (
    GC_VVC_IS_MASTER => true,
    GC_DATA_WIDTH => 16,
    GC_USER_WIDTH => 0,
    GC_INSTANCE_IDX => 1,
    GC_AXISTREAM_BFM_CONFIG => C_AXISTREAM_M_CONF
  )
  port map (
    clk => clock,
    axistream_vvc_if => axistream_if_m
  );

  S_AXIS_IN_TDATA <= axistream_if_m.tdata;
  S_AXIS_IN_TVALID <= axistream_if_m.tvalid;
  axistream_if_m.tready <= '1';

  -- slave -> receives data
  i2_axistream_vvc: entity bitvis_vip_axistream.axistream_vvc
  generic map (
    GC_VVC_IS_MASTER => false,
    GC_DATA_WIDTH => 16,
    GC_USER_WIDTH => 0,
    GC_INSTANCE_IDX => 2,
    GC_AXISTREAM_BFM_CONFIG => C_AXISTREAM_M_CONF
  )
  port map (
    clk => clock,
    axistream_vvc_if => axistream_if_s
  );

  axistream_if_s.tdata <= M_AXIS_OUT_TDATA;
  axistream_if_s.tvalid <= M_AXIS_OUT_TVALID;
  axistream_if_s.tlast <= TB_AXIS_OUT_TLAST;
  axistream_if_s.tkeep <= (others => '1');
  axistream_if_s.tstrb <= (others => '1');

end architecture;
