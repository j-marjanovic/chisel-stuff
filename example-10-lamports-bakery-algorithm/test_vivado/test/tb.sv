`timescale 1ns / 1ps

module tb;

  system_wrapper DUT ();

  import axi_vip_pkg::*;
  import system_axi_vip_0_0_pkg::*;


  system_axi_vip_0_0_mst_t axi_agent;

  //============================================================================
  // tasks

  task read_reg(input longint unsigned addr, output longint unsigned data);
    axi_transaction   rd_trans;
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

  //============================================================================
  // high-level task

  task config_and_start(input longint unsigned base_offset, int n);
    $display("%6t Config and start at address 0x%x", $time, base_offset);

    write_reg(base_offset | 'h24, 'h0);
    write_reg(base_offset | 'h20, 'hc0001008);
    write_reg(base_offset | 'h2c, 'h0);
    write_reg(base_offset | 'h28, 'hc0001040);
    write_reg(base_offset | 'h34, 'h0);
    write_reg(base_offset | 'h30, 'hc0001080);

    // config instance (n of 4)
    write_reg(base_offset | 'h40, n);
    write_reg(base_offset | 'h44, 3);

    // config nr loops
    write_reg(base_offset | 'h50, 19);

    // start
    write_reg(base_offset | 'h14, 1);

  endtask


  task wait_for_done(input longint unsigned base_offset);
    longint unsigned status;

    do begin
      read_reg(base_offset | 'h10, status);
    end while (!(status & 1));
    $display("%6t Seen done at address 0x%x", $time, base_offset);

    // clear
    write_reg(base_offset | 'h14, 2);

  endtask

  //============================================================================
  // main

  initial begin : proc_main
    longint unsigned read_data;

    $display("%6t Testbench starting...", $time);

    axi_agent = new("master vip agent", DUT.system_i.axi_vip_0.inst.IF);
    axi_agent.start_master();

    // start clock, reset components
    DUT.system_i.clk_vip_0.inst.IF.start_clock();
    DUT.system_i.rst_vip_0.inst.IF.assert_reset();
    #(100ns);
    DUT.system_i.rst_vip_0.inst.IF.deassert_reset();

    // wait for everything to come out of reset
    #(1us);
    $display("%6t Reset done", $time);

    read_reg('h44A0_0000, read_data);
    read_reg('h44A1_0000, read_data);
    read_reg('h44A2_0000, read_data);
    read_reg('h44A3_0000, read_data);

    write_reg('hC000_1008, 'd0);

    write_reg('h44A0_0054, 'ha116);
    write_reg('h44A1_0054, 'hb227);
    write_reg('h44A2_0054, 'hc338);
    write_reg('h44A3_0054, 'hd449);

    config_and_start('h44A0_0000, 0);
    config_and_start('h44A1_0000, 1);
    config_and_start('h44A2_0000, 2);
    config_and_start('h44A3_0000, 3);

    wait_for_done('h44A0_0000);
    wait_for_done('h44A1_0000);
    wait_for_done('h44A2_0000);
    wait_for_done('h44A3_0000);

    read_reg('hC000_1008, read_data);
    $display("%6t counter val at the end = %d", $time, read_data);

    #(100ns);
    $display("%6t Testbench done.", $time);
    $stop;
  end
endmodule
