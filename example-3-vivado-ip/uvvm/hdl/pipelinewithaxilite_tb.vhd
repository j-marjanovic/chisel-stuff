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
use bitvis_vip_axilite.vvc_cmd_pkg.all;
use bitvis_vip_axilite.vvc_methods_pkg.all;
use bitvis_vip_axilite.td_vvc_framework_common_methods_pkg.all;

library bitvis_vip_axistream;
use bitvis_vip_axistream.vvc_cmd_pkg.all;
use bitvis_vip_axistream.vvc_methods_pkg.all;
use bitvis_vip_axistream.td_vvc_framework_common_methods_pkg.all;

entity pipelinewithaxilite_tb is
end entity;

architecture sim of pipelinewithaxilite_tb is
  constant C_ADDR_ID_REG : unsigned(3 downto 0) := to_unsigned(0, 4);
  constant C_ADDR_VER_REG : unsigned(3 downto 0) := to_unsigned(4, 4);
  constant C_ADDR_STATS_REG : unsigned(3 downto 0) := to_unsigned(8, 4);
  constant C_ADDR_COEF_REG : unsigned(3 downto 0) := to_unsigned(12, 4);
begin

  i_test_harness: entity work.pipelinewithaxilite_th;

  proc_main: process
    ----------------------------------------------------------------------------
    -- AXI4-Lite procedures

    -- read from AXI register
    procedure read_reg(addr: in unsigned; data: out unsigned) is
      variable v_idx : integer;
      variable v_lite_result : bitvis_vip_axilite.vvc_cmd_pkg.t_vvc_result;
    begin
      axilite_read(AXILITE_VVCT, 1, addr, "read reg");
      v_idx := get_last_received_cmd_idx(AXILITE_VVCT, 1);
      await_completion(AXILITE_VVCT, 1, 100 ns, "wait for read to complete");
      fetch_result(AXILITE_VVCT, 1, v_idx, v_lite_result);
      data := unsigned(v_lite_result(31 downto 0));
    end procedure;

    -- write from AXI register
    procedure write_reg(addr: in unsigned; data: in std_logic_vector) is
      variable v_idx : integer;
      variable v_lite_result : bitvis_vip_axilite.vvc_cmd_pkg.t_vvc_result;
    begin
      axilite_write(AXILITE_VVCT, 1, addr, data, "write reg");
      await_completion(AXILITE_VVCT, 1, 100 ns, "wait for read to complete");
    end procedure;

    procedure read_id is
      variable tmp: unsigned(31 downto 0);
    begin
      read_reg(C_ADDR_ID_REG, tmp);
      log(ID_LOG_HDR, "ID reg: " & to_hstring(tmp));
    end procedure;

    procedure read_ver is
      variable tmp: unsigned(31 downto 0);
    begin
      read_reg(C_ADDR_VER_REG, tmp);
      log(ID_LOG_HDR, "version reg: " & to_hstring(tmp));
    end procedure;

    procedure read_stats(stats: out unsigned) is
      variable tmp: unsigned(31 downto 0);
    begin
      read_reg(C_ADDR_STATS_REG, tmp);
      log(ID_LOG_HDR, "stats reg: " & to_hstring(tmp));
      stats := tmp;
    end procedure;

    procedure set_coef(coef: in unsigned) is
    begin
      write_reg(C_ADDR_COEF_REG, std_logic_vector(coef));
    end procedure;

    ----------------------------------------------------------------------------
    -- variables for TB
    variable v_idx : integer;
    variable v_st_result : bitvis_vip_axistream.vvc_cmd_pkg.t_vvc_result;
    variable v_tmp_slv16 : std_logic_vector(15 downto 0);
    variable v_tmp_u32 : unsigned(31 downto 0);

    alias TB_AXIS_OUT_TLAST is
      <<signal i_test_harness.TB_AXIS_OUT_TLAST : std_logic>>;

    constant C_ST_MEM_SIZE : natural :=
      bitvis_vip_axistream.vvc_cmd_pkg.C_VVC_CMD_DATA_MAX_BYTES;

    ----------------------------------------------------------------------------
    -- stimuli and expected values
    type t_stimuli is array(natural range<>) of std_logic_vector(15 downto 0);

    function f_model(x: in t_stimuli) return t_stimuli is
      variable tmp : t_stimuli(x'range);
    begin
      for i in x'range loop
        tmp(i) := std_logic_vector(unsigned(x(i)) + 1);
      end loop;
      return tmp;
    end function;

    constant C_STIM : t_stimuli(0 to 5) := (
      0 => X"0000",
      1 => X"0001",
      2 => 16D"99",
      3 => 16D"100",
      4 => 16D"65534",
      5 => 16D"65535"
    );
    constant C_EXP : t_stimuli(0 to 5) := f_model(C_STIM);
  begin
    -- init
    await_uvvm_initialization(VOID);
    set_alert_stop_limit(ERROR, 0); -- do not stop on ERRORs
    disable_log_msg(ALL_MESSAGES);
    enable_log_msg(ID_LOG_HDR);
    disable_log_msg(AXILITE_VVCT, 1, ALL_MESSAGES);
    disable_log_msg(AXISTREAM_VVCT, 1, ALL_MESSAGES);
    disable_log_msg(AXISTREAM_VVCT, 2, ALL_MESSAGES);

    log(ID_LOG_HDR, "Start simulation");

    wait for 50 ns;

    -- read ID reg
    read_id;
    read_ver;
    read_stats(v_tmp_u32);
    set_coef(to_unsigned(1, 32));

    -- set receiver
    axistream_receive(AXISTREAM_VVCT, 2, "recveive data");
    v_idx := get_last_received_cmd_idx(AXISTREAM_VVCT, 2);
    log(ID_LOG_HDR, "v_idx: " & to_string(v_idx));

    -- send data on AXI stream (force TLAST on last cycle)
    for i in C_STIM'range loop
      axistream_transmit(AXISTREAM_VVCT, 1, C_STIM(i), "write data");
    end loop;
    await_completion(AXISTREAM_VVCT, 1, 100 ns, "wait for pkts to be transmitted");

    -- dummy data, just to stop the AXI4-Stream BFM
    wait for 20 ns;
    TB_AXIS_OUT_TLAST <= '1';
    axistream_transmit(AXISTREAM_VVCT, 1, X"0000", "write data");
    await_completion(AXISTREAM_VVCT, 2, 100 ns, "wait for pkts to be received");

    wait for 100 ns;
    fetch_result(AXISTREAM_VVCT, 2, v_idx, v_st_result);
    for i in C_STIM'range loop
      v_tmp_slv16 := v_st_result.data_array(C_ST_MEM_SIZE-2-i*2) &
          v_st_result.data_array(C_ST_MEM_SIZE-1-i*2);
      log(ID_LOG_HDR, "Stream data " & to_string(i) & ": recv = " &
          to_hstring(v_tmp_slv16) & ", exp = " & to_hstring(C_EXP(i)));
      check_value(v_tmp_slv16, C_EXP(i), ERROR, "check between output and model");
    end loop;

    wait for 100 ns;

    -- check counter operation (len of C_STIM + 1 for dummy expected)
    read_stats(v_tmp_u32);
    check_value(to_integer(v_tmp_u32), C_STIM'length + 1, ERROR, "check internal counter");

    -- done
    report_alert_counters(FINAL);
    log(ID_LOG_HDR, "Simulation finished");
    std.env.stop;
  end process;

end architecture;
