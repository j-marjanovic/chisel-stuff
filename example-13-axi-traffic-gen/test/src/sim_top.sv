`timescale 1ns / 1ps


module sim_top ();

  import axi_vip_pkg::*;
  import system_axi_vip_0_0_pkg::*;
  import system_axi_vip_1_0_pkg::*;

  system_wrapper DUT ();

  initial begin : proc_main
    bit [4*8-1:0] data;
    bit [127:0] data_slv;
    const bit [63:0] ADDR = 64'h0000_0012_ABCD_0000;
    const bit [31:0] LEN = 'h100;
    xil_axi_resp_t resp;
    system_axi_vip_0_0_mst_t mst_agent;
    system_axi_vip_1_0_slv_mem_t slv_mem_agent;

    mst_agent = new("master vip agent", sim_top.DUT.system_i.axi_vip_mst.inst.IF);
    slv_mem_agent = new("slave vip agent", sim_top.DUT.system_i.axi_vip_slv.inst.IF);
    // slv_mem_agent.set_verbosity(400);

    $display("%t simulation starting", $time);
    sim_top.DUT.system_i.clk_vip_0.inst.IF.start_clock();
    #(100ns);
    sim_top.DUT.system_i.rst_vip_0.inst.IF.assert_reset();
    #(1000ns);
    sim_top.DUT.system_i.rst_vip_0.inst.IF.deassert_reset();
    #(1000ns);

    mst_agent.start_master();
    slv_mem_agent.start_slave();

    // check id reg
    mst_agent.AXI4LITE_READ_BURST(0, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t ID reg = %x", $time, data);
    assert (data == 'ha8172a9e) $display("%t ID register successfully read", $time);
    else $error("%t Error reading ID reg, received = %x", $time, data);

    // get version register
    mst_agent.AXI4LITE_READ_BURST(4, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t version = %x", $time, data);

    // read status
    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t Status at the start = %x", $time, data);
    assert (data[8]) $display("%t Status indicates WR FSM idle", $time);
    assert (data[9]) $display("%t Status indicates RD FSM idle", $time);


    // configure AXI
    mst_agent.AXI4LITE_WRITE_BURST('h20, 0, 'h0, resp);
    mst_agent.wait_drivers_idle();

    // configure address
    mst_agent.AXI4LITE_WRITE_BURST('h40, 0, ADDR[31:0], resp);
    mst_agent.wait_drivers_idle();

    mst_agent.AXI4LITE_WRITE_BURST('h44, 0, ADDR[63:32], resp);
    mst_agent.wait_drivers_idle();

    // configure length
    mst_agent.AXI4LITE_WRITE_BURST('h30, 0, LEN, resp);
    mst_agent.wait_drivers_idle();

    // start write
    mst_agent.AXI4LITE_WRITE_BURST('h14, 0, 'h1, resp);
    mst_agent.wait_drivers_idle();

    // read status
    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t Status after WR start = %x", $time, data);
    assert (!data[8]) $display("%t Status indicates WR FSM not idle", $time);
    assert (data[9]) $display("%t Status indicates RD FSM idle", $time);

    // wait for WR FSM to finish
    for (int i = 0; i < 1000; i++) begin
      #(100ns);
      mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
      mst_agent.wait_drivers_idle();

      if (data[0]) begin
        $display("%t Status indicates WR FSM done", $time);
        break;
      end

      assert (i != 1000 - 1)
      else $error("%t Timeout reached while waiting for WR to finish", $time);
    end

    // start read
    mst_agent.AXI4LITE_WRITE_BURST('h14, 0, 'h2, resp);
    mst_agent.wait_drivers_idle();

    // read status
    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t Status after RD start = %x", $time, data);
    assert (!data[9]) $display("%t Status indicates RD FSM not idle", $time);


    // wait for WR FSM to finish
    for (int i = 0; i < 1000; i++) begin
      #(100ns);
      mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
      mst_agent.wait_drivers_idle();

      if (data[1]) begin
        $display("%t Status indicates RD FSM done", $time);
        break;
      end

      assert (i != 1000 - 1)
      else $error("%t Timeout reached while waiting for RD to finish", $time);
    end

    #(100ns);
    $finish;
  end

endmodule
