/*
MIT License

Copyright (c) 2018 Jan Marjanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

`timescale 1ns / 1ps

module pipelinewithaxilite_tb;

//==============================================================================
// imports

import axi_vip_pkg::*;
import example_pipeline_with_axi_axi_vip_0_0_pkg::*;

import axi4stream_vip_pkg::*;
import example_pipeline_with_axi_axi4stream_vip_master_0_pkg::*;
import example_pipeline_with_axi_axi4stream_vip_slave_0_pkg::*;


//==============================================================================
// modules

// device under test

example_pipeline_with_axi DUT ();

// agents

example_pipeline_with_axi_axi_vip_0_0_mst_t axi_agent;
example_pipeline_with_axi_axi4stream_vip_master_0_mst_t axis_m_agent;
example_pipeline_with_axi_axi4stream_vip_slave_0_slv_t axis_s_agent;


//==============================================================================
// tasks

task read_reg(input longint unsigned addr, output longint unsigned data);
    axi_transaction rd_trans;
    xil_axi_data_beat data_beat;
    rd_trans = axi_agent.rd_driver.create_transaction();
    rd_trans.set_driver_return_item_policy(XIL_AXI_PAYLOAD_RETURN);
    rd_trans.set_read_cmd(addr);
    axi_agent.rd_driver.send(rd_trans);
    axi_agent.rd_driver.wait_rsp(rd_trans);
    data_beat = rd_trans.get_data_beat(0);
    data = data_beat[31:0];
endtask

task write_reg(input longint unsigned addr, input longint unsigned data);
    axi_transaction wr_trans;
    wr_trans = axi_agent.wr_driver.create_transaction();
    wr_trans.set_write_cmd(addr);
    wr_trans.set_data_beat(0, data);
    axi_agent.wr_driver.send(wr_trans);
    axi_agent.wr_driver.wait_driver_idle();
endtask

task read_id();
    longint unsigned data;
    $display("%t read ID", $time);
    read_reg(0, data);
    $display("%t   reg value: %08x", $time, data);
endtask

task read_ver();
    longint unsigned data;
    $display("%t read VER", $time);
    read_reg(4, data);
    $display("%t   reg value: %08x", $time, data);
endtask

task read_stats();
    longint unsigned data;
    $display("%t read STATS", $time);
    read_reg(8, data);
    $display("%t   reg value: %08x", $time, data);
endtask

task set_coef_to_1();
    write_reg('hc, 'd1);
endtask

//==============================================================================
// driver

task send_data(input bit [15:0] data);
    axi4stream_transaction axis_trans;

    $display("%t driver: send %x", $time, data);
    axis_trans = axis_m_agent.driver.create_transaction();
    axis_trans.set_data_beat(data);
    axis_trans.set_delay(0);
    axis_m_agent.driver.send(axis_trans);
endtask

//==============================================================================
// monitor

bit [15:0] received[$];

initial begin
    bit [15:0] tmp;
    axi4stream_monitor_transaction slv_monitor_transaction;

    axis_s_agent = new("slave axis agent", pipelinewithaxilite_tb.DUT.axi4stream_vip_slave.inst.IF);
    axis_s_agent.start_monitor();

    forever begin
        axis_s_agent.monitor.item_collected_port.get(slv_monitor_transaction);
        tmp = slv_monitor_transaction.get_data_beat();
        received.push_back(tmp);
        $display("%t monitor: received %x", $time, tmp);
    end
end

//==============================================================================
// misc

function [15:0] model(input [15:0] x);
    model = x + 'd1;
endfunction

//==============================================================================
// main

initial begin: proc_main
    static bit [15:0] STIMULI[] = {0, 1, 2, 99, 100, 65534, 65535};
    static bit [15:0] expected[$];
    for (int i = 0; i < $size(STIMULI); i++) begin
        expected.push_back(model(STIMULI[i]));
    end

    // init agents
    axi_agent = new("master vip agent", pipelinewithaxilite_tb.DUT.axi_vip_0.inst.IF);
    axi_agent.start_master();

    axis_m_agent = new("master axis agent", pipelinewithaxilite_tb.DUT.axi4stream_vip_master.inst.IF);
    axis_m_agent.start_master();
    axis_m_agent.vif_proxy.set_dummy_drive_type(XIL_AXI4STREAM_VIF_DRIVE_NONE);

    // start test
    $display("%t PipelineWithAxiLite testbench starting...", $time);
    pipelinewithaxilite_tb.DUT.clk_vip_0.inst.IF.set_clk_frq(100_000_000);
    pipelinewithaxilite_tb.DUT.clk_vip_0.inst.IF.start_clock();

    // wait for everything to come out of reset
    #(1us);

    // read regs
    read_id();
    read_ver();
    read_stats();
    set_coef_to_1();

    // drive stimuli into DUT
    foreach(STIMULI[i]) send_data(STIMULI[i]);

    // wait for data to be processed
    #(1us);

    read_stats();

    // final checks
    assert ($size(STIMULI) == $size(received))
        $display("%t check: received expected number of samples", $time);
    else
        $display("%t check: received number of samples does not much expected", $time);

    for (int i = 0; i < $size(STIMULI); i++) begin
        assert (received[i] == expected[i])
           $display("%t check: sample %2d matches expected", $time, i);
        else
           $display("%t check: sample %2d does not match (expected = %x, received = %x)",
                    $time, i, expected[i], received[i]);
    end

    $display("%t PipelineWithAxiLite testbench done", $time);
    $finish;
end

endmodule
