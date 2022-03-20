/*
MIT License

Copyright (c) 2018-2022 Jan Marjanovic

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

module gunzip_system_tb;

import axi_vip_pkg::*;
import system_axi_vip_ctrl_0_pkg::*;  // ctrl
import system_axi_vip_orig_0_pkg::*;  // source mem
import system_axi_vip_res_0_pkg::*;  // dest mem

//
system_wrapper DUT ();

system_axi_vip_ctrl_0_mst_t ctrl_agent;
system_axi_vip_orig_0_slv_mem_t agent_mem_src;
system_axi_vip_res_0_slv_mem_t agent_mem_dst;

//==============================================================================

class AxiAccessor;
    system_axi_vip_ctrl_0_mst_t agent;

    function new (ref system_axi_vip_ctrl_0_mst_t agent);
        this.agent = agent;
    endfunction

    task read_reg(input longint unsigned addr, output longint unsigned data);
        axi_transaction rd_trans;
        xil_axi_data_beat data_beat;
        rd_trans = agent.rd_driver.create_transaction();
        rd_trans.set_driver_return_item_policy(XIL_AXI_PAYLOAD_RETURN);
        rd_trans.set_read_cmd(addr);
        agent.rd_driver.send(rd_trans);
        agent.rd_driver.wait_rsp(rd_trans);
        data_beat = rd_trans.get_data_beat(0);
        data = data_beat[31:0];
    endtask

    task write_reg(input longint unsigned addr, input longint unsigned data);
        axi_transaction wr_trans;
        wr_trans = agent.wr_driver.create_transaction();
        wr_trans.set_write_cmd(addr);
        wr_trans.set_data_beat(0, data);
        agent.wr_driver.send(wr_trans);
        agent.wr_driver.wait_driver_idle();
    endtask
endclass

class AxiDmaS2MMController;
    AxiAccessor accessor;
    longint unsigned base_addr;
    string name;

    const int unsigned ADDR_S2MM_DMACR = 'h30;
    const int unsigned ADDR_S2MM_DMASR = 'h34;
    const int unsigned ADDR_S2MM_DA = 'h48;
    const int unsigned ADDR_S2MM_DA_MSB = 'h4C;
    const int unsigned ADDR_S2MM_LENGTH = 'h58;

    function new (longint unsigned base_addr, ref AxiAccessor accessor);
        this.name = "AxiDmaS2MMController";
        this.accessor = accessor;
        this.base_addr = base_addr;
    endfunction

    task start_tx(int unsigned byte_len, int unsigned dst_addr);
        $display("%t %s: start tx", name, $time);
        _check_halted();
        _start_channel();
        _set_dst_addr32(dst_addr);
        _set_length(byte_len);
    endtask

    task _check_halted();
        int unsigned status_reg;
        accessor.read_reg(base_addr + ADDR_S2MM_DMASR, status_reg);
        assert (status_reg[0] == 'b1)
            $display("%t %s: OK - controller is halted (status: %x)",
                $time, name, status_reg);
        else
            $display("%t %s: controller should be halted (status: %x)",
                $time, name, status_reg);
    endtask

    task _start_channel();
        int unsigned ctrl_reg;
        accessor.read_reg(base_addr + ADDR_S2MM_DMACR, ctrl_reg);
        ctrl_reg[0] = 'b1;
        accessor.write_reg(base_addr + ADDR_S2MM_DMACR, ctrl_reg);
    endtask

    task _set_dst_addr32(int unsigned dst_addr);
        accessor.write_reg(base_addr + ADDR_S2MM_DA_MSB, 0);
        accessor.write_reg(base_addr + ADDR_S2MM_DA, dst_addr);
    endtask

    task _set_length(int unsigned byte_len);
        accessor.write_reg(base_addr + ADDR_S2MM_LENGTH, byte_len);
    endtask
endclass


class AxiDmaMM2SController;
    AxiAccessor accessor;
    longint unsigned base_addr;
    string name;

    const int unsigned ADDR_MM2S_DMACR = 'h0;
    const int unsigned ADDR_MM2S_DMASR = 'h4;
    const int unsigned ADDR_MM2S_SA = 'h18;
    const int unsigned ADDR_MM2S_SA_MSB = 'h1C;
    const int unsigned ADDR_MM2S_LENGTH = 'h28;

    function new (longint unsigned base_addr, ref AxiAccessor accessor);
        this.name = "AxiDmaMM2SController";
        this.accessor = accessor;
        this.base_addr = base_addr;
    endfunction

    task start_tx(int unsigned byte_len, int unsigned src_addr);
        $display("%t %s: start tx", name, $time);
        _check_halted();
        _start_channel();
        _set_src_addr32(src_addr);
        _set_length(byte_len);
    endtask

    task _check_halted();
        int unsigned status_reg;
        accessor.read_reg(base_addr + ADDR_MM2S_DMASR, status_reg);
        assert (status_reg[0] == 'b1)
            $display("%t %s: OK - controller is halted (status: %x)",
                $time, name, status_reg);
        else
            $display("%t %s: controller should be halted (status: %x)",
                $time, name, status_reg);
    endtask

    task _start_channel();
        int unsigned ctrl_reg;
        accessor.read_reg(base_addr + ADDR_MM2S_DMACR, ctrl_reg);
        ctrl_reg[0] = 'b1;
        accessor.write_reg(base_addr + ADDR_MM2S_DMACR, ctrl_reg);
    endtask

    task _set_src_addr32(int unsigned src_addr);
        accessor.write_reg(base_addr + ADDR_MM2S_SA_MSB, 0);
        accessor.write_reg(base_addr + ADDR_MM2S_SA, src_addr);
    endtask

    task _set_length(int unsigned byte_len);
        accessor.write_reg(base_addr + ADDR_MM2S_LENGTH, byte_len);
    endtask

endclass

//==============================================================================
// main
function [31:0] swap_bytes32(input [31:0] data);
    for (int i = 0; i < 4; i++) begin
        swap_bytes32[(3-i)*8 +: 8] = data[i*8 +: 8];
    end
endfunction

initial begin: proc_main
    int fd, rc, addr = 0;
    bit [31:0] rd_buf;

    AxiAccessor axi_accessor;
    AxiDmaMM2SController dma_ctrl_src;
    AxiDmaS2MMController dma_ctrl_dst;
    $display("%t Gunzip testbench starting...", $time);

    // create agents
    ctrl_agent = new("ctrl agent", DUT.system_i.axi_vip_ctrl.inst.IF);
    agent_mem_src = new("src mem agent", DUT.system_i.axi_vip_orig.inst.IF);
    agent_mem_dst = new("dst mem agent", DUT.system_i.axi_vip_res.inst.IF);
    ctrl_agent.start_master();
    agent_mem_src.start_slave();
    agent_mem_dst.start_slave();
    axi_accessor = new(ctrl_agent);
    dma_ctrl_src = new(32'h41E1_0000, axi_accessor);
    dma_ctrl_dst = new(32'h41E0_0000, axi_accessor);

    // load mem
    fd = $fopen("in.tar.gz", "rb");
    $display("%t opened file, got fd: %x", $time, fd);

    do begin
        rc = $fread(rd_buf, fd);
        $display("%t read() = %d, data = %x", $time, rc, swap_bytes32(rd_buf));
        agent_mem_src.mem_model.backdoor_memory_write(addr, swap_bytes32(rd_buf));
        addr += 4;
    end while (rc == 4);

    // start clocks
    DUT.system_i.clk_vip_0.inst.IF.set_clk_frq(200_000_000);
    DUT.system_i.clk_vip_0.inst.IF.start_clock();

    // assert reset
    #(100ns);
    DUT.system_i.rst_vip_0.inst.IF.assert_reset();
    #(100ns);
    DUT.system_i.rst_vip_0.inst.IF.deassert_reset();
    #(500ns);

    dma_ctrl_dst.start_tx(102400, 0);
    dma_ctrl_src.start_tx(102400, 0);

    #(200us);

    $display("%t Gunzip testbench finished.", $time);
    $stop;
end

endmodule
