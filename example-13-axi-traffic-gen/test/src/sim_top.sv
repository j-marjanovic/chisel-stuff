`timescale 1ns / 1ps


module sim_top ();

  import axi_vip_pkg::*;
  import system_axi_vip_0_0_pkg::*;
  import system_axi_vip_1_0_pkg::*;

  system_wrapper DUT ();

  initial begin : proc_main
    bit [4*8-1:0] data;
    bit [127:0] data_slv;
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


    mst_agent.AXI4LITE_READ_BURST(0, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t ID reg = %x", $time, data);

    mst_agent.AXI4LITE_READ_BURST(4, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t version = %x", $time, data);


    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t status before = %x", $time, data);

    mst_agent.AXI4LITE_WRITE_BURST('h20, 0, 'h0, resp);
    mst_agent.wait_drivers_idle();

    mst_agent.AXI4LITE_WRITE_BURST('h40, 0, 'h0, resp);
    mst_agent.wait_drivers_idle();

    mst_agent.AXI4LITE_WRITE_BURST('h44, 0, 'h0, resp);
    mst_agent.wait_drivers_idle();

    mst_agent.AXI4LITE_WRITE_BURST('h14, 0, 'h1, resp);
    mst_agent.wait_drivers_idle();

    #(100ns);

    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t status after = %x", $time, data);

    #(50000ns);  // TODO: read status reg

    /*
        for (int i = 0; i < 64; i++) begin
            //data = slv_mem_agent.mem_model.backdoor_memory_read_4byte('h12abcd0000 + 4*i);
            //$display("data at %x = %x", 'h12abcd0000 + 4*i, data);
            data_slv = slv_mem_agent.mem_model.backdoor_memory_read(0 + i * 16);
            $display("data at %x = %x", 0 + i * 16, data_slv);
        end
        */

    mst_agent.AXI4LITE_WRITE_BURST('h14, 0, 'h2, resp);
    mst_agent.wait_drivers_idle();

    #(100ns);

    mst_agent.AXI4LITE_READ_BURST('h10, 0, data, resp);
    mst_agent.wait_drivers_idle();
    $display("%t status after = %x", $time, data);

    #(50000ns);  // TODO: read status reg

    #(100ns);
    $finish;
  end

endmodule
