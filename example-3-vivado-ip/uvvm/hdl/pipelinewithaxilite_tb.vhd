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
begin

  i_test_harness: entity work.pipelinewithaxilite_th;

  proc_main: process
    variable v_idx : integer;
    variable v_lite_result : bitvis_vip_axilite.vvc_cmd_pkg.t_vvc_result;
    variable v_st_result : bitvis_vip_axistream.vvc_cmd_pkg.t_vvc_result;
    alias TB_AXIS_OUT_TLAST is
      <<signal i_test_harness.TB_AXIS_OUT_TLAST : std_logic>>;
  begin
    -- init
    await_uvvm_initialization(VOID);
    disable_log_msg(ALL_MESSAGES);
    enable_log_msg(ID_LOG_HDR);
    disable_log_msg(AXILITE_VVCT, 1, ALL_MESSAGES);
    enable_log_msg(AXISTREAM_VVCT, 1, ALL_MESSAGES);
    enable_log_msg(AXISTREAM_VVCT, 2, ALL_MESSAGES);
    log(ID_LOG_HDR, "Start simulation");

    wait for 50 ns;

    -- read ID reg
    axilite_read(AXILITE_VVCT, 1, x"00", "read ID reg");
    v_idx := get_last_received_cmd_idx(AXILITE_VVCT, 1);
    await_completion(AXILITE_VVCT, 1, 100 ns, "wait for read to complete");
    fetch_result(AXILITE_VVCT, 1, v_idx, v_lite_result);
    log(ID_LOG_HDR, "ID reg: " & to_hstring(v_lite_result(31 downto 0)));

    -- recv
    axistream_receive(AXISTREAM_VVCT, 2, "recveive data");
    v_idx := get_last_received_cmd_idx(AXISTREAM_VVCT, 2);
    log(ID_LOG_HDR, "v_idx: " & to_string(v_idx));

    -- send data on AXI stream (force TLAST on last cycle)
    axistream_transmit(AXISTREAM_VVCT, 1, X"0123", "write data");
    axistream_transmit(AXISTREAM_VVCT, 1, X"ABCD", "write data");
    axistream_transmit(AXISTREAM_VVCT, 1, X"4567", "write data");
    axistream_transmit(AXISTREAM_VVCT, 1, X"89EF", "write data");
    await_completion(AXISTREAM_VVCT, 1, 100 ns, "wait for pkts to be transmitted");
    wait for 20 ns;

    TB_AXIS_OUT_TLAST <= '1';
    axistream_transmit(AXISTREAM_VVCT, 1, X"0000", "write data");
    await_completion(AXISTREAM_VVCT, 2, 100 ns, "wait for pkts to be received");

    wait for 100 ns;

    fetch_result(AXISTREAM_VVCT, 2, v_idx, v_st_result);
    log(ID_LOG_HDR, "Stream data: " & to_string(v_st_result.data_array(16*1024-10 to 16*1024-1)));

    wait for 100 ns;

    report_alert_counters(FINAL);
    log(ID_LOG_HDR, "Simulation finished");
    std.env.stop;
  end process;


end architecture;
