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
use bitvis_vip_axilite.vvc_methods_pkg.all;
use bitvis_vip_axilite.td_vvc_framework_common_methods_pkg.all;

entity pipelinewithaxilite_tb is
end entity;

architecture sim of pipelinewithaxilite_tb is
begin

  i_test_harness: entity work.pipelinewithaxilite_th;

  proc_main: process
    subtype  t_vvc_result is std_logic_vector(256-1 downto 0);

    variable v_idx : integer;
    variable v_result : t_vvc_result;
  begin
    -- init
    await_uvvm_initialization(VOID);
    disable_log_msg(ALL_MESSAGES);
    enable_log_msg(ID_LOG_HDR);
    disable_log_msg(AXILITE_VVCT, 1, ALL_MESSAGES);
    log(ID_LOG_HDR, "Start simulation");

    wait for 50 ns;

    -- read ID reg
    axilite_read(AXILITE_VVCT, 1, x"00", "read ID reg");
    v_idx := get_last_received_cmd_idx(AXILITE_VVCT, 1);
    await_completion(AXILITE_VVCT, 1, 100 ns, "wait for read to complete");
    fetch_result(AXILITE_VVCT, 1, v_idx, v_result);
    log(ID_LOG_HDR, "ID reg: " & to_hstring(v_result(31 downto 0)));

    wait for 100 ns;

    report_alert_counters(FINAL);
    log(ID_LOG_HDR, "Simulation finished");
    std.env.stop;
  end process;


end architecture;
