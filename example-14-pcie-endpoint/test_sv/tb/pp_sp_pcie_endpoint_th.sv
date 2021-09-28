/*
Copyright (c) 2021 Jan Marjanovic

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

`timescale 1ps / 1ps

module pp_sp_pcie_endpoint_th;

  //============================================================================
  // wires

  // clock, reset, locked
  wire                                   coreclkout_hip;
  wire                                   reset_status;
  wire serdes_pll_locked = 1'b1;
  wire                                   pld_clk_inuse;
  wire                                   pld_core_ready;
  wire                                   testin_zero;
  wire                                   pld_clk_hip;
  wire                                   reset_out;

  // RX
  wire                           [255:0] rx_st_data;
  wire                                   rx_st_sop;
  wire                                   rx_st_eop;
  wire                           [  1:0] rx_st_empty;
  wire                                   rx_st_ready;
  wire                                   rx_st_valid;
  wire                                   rx_st_err;
  wire                                   rx_st_mask;
  wire                           [  7:0] rx_st_bar;
  wire                           [ 31:0] rx_st_be;

  // TX
  wire                           [255:0] tx_st_data;
  wire                                   tx_st_sop;
  wire                                   tx_st_eop;
  wire                                   tx_st_ready;
  wire                                   tx_st_valid;
  wire                           [  1:0] tx_st_empty;
  wire                                   tx_st_err;

  // cfg
  wire                           [ 31:0] tl_cfg_ctl;
  wire                           [  3:0] tl_cfg_add;
  wire                           [ 52:0] tl_cfg_sts;

  // config
  wire                           [  6:0] cpl_err;
  wire                                   cpl_pending;
  wire                           [  4:0] hpg_ctrler;

  // interrupt
  wire                                   app_int_msi_req;
  wire                                   app_int_msi_ack = 1'b0;
  wire                           [  2:0] app_int_msi_tc;
  wire                           [  4:0] app_int_msi_num;
  wire                                   app_int_sts;
  wire                                   app_int_ack = 1'b0;

  // Avalon ST data out
  wire                           [255:0] data_out_data;
  wire                                   data_out_valid;
  wire                           [  7:0] data_out_empty;

  // Avalon MM
  wire                           [ 31:0] avmm_bar0_address;
  wire                           [  3:0] avmm_bar0_byteenable;
  wire                                   avmm_bar0_read;
  wire                           [ 31:0] avmm_bar0_readdata;
  wire                           [  1:0] avmm_bar0_response;
  wire                                   avmm_bar0_write;
  wire                           [ 31:0] avmm_bar0_writedata;
  wire                                   avmm_bar0_waitrequest;
  wire                                   avmm_bar0_readdatavalid;
  wire                                   avmm_bar0_writeresponsevalid;
  wire                                   avmm_bar0_burstcount;

  //============================================================================
  // module instances

  altera_avalon_clock_reset_source clk_rst_source (
      .clk(coreclkout_hip),
      .reset(reset_status),
      .dummy_src(),
      .dummy_snk()
  );

  altera_avalon_st_source_bfm #(
      .ST_SYMBOL_W     (8),
      .ST_NUMSYMBOLS   (256 / 8),
      .ST_CHANNEL_W    (8),
      .ST_ERROR_W      (1),
      .ST_EMPTY_W      (2),
      .ST_READY_LATENCY(3),
      .ST_MAX_CHANNELS (8),
      .USE_PACKET      (1),
      .USE_CHANNEL     (1),
      .USE_ERROR       (1),
      .USE_READY       (1),
      .USE_VALID       (1),
      .USE_EMPTY       (1),
      .ST_BEATSPERCYCLE(1)
  ) st_source_rx (
      .clk(coreclkout_hip),
      .reset(reset_status),
      .src_data(rx_st_data),
      .src_channel(rx_st_bar),
      .src_valid(rx_st_valid),
      .src_startofpacket(rx_st_sop),
      .src_endofpacket(rx_st_eop),
      .src_error(rx_st_err),
      .src_empty(rx_st_empty),
      .src_ready(rx_st_ready)
  );
  // rx_st_mask and rx_st_be not connected

  altera_avalon_st_sink_bfm #(
      .ST_SYMBOL_W     (8),
      .ST_NUMSYMBOLS   (256 / 8),
      .ST_CHANNEL_W    (0),
      .ST_ERROR_W      (1),
      .ST_EMPTY_W      (2),
      .ST_READY_LATENCY(3),
      .ST_MAX_CHANNELS (1),
      .USE_PACKET      (1),
      .USE_CHANNEL     (0),
      .USE_ERROR       (1),
      .USE_READY       (1),
      .USE_VALID       (1),
      .USE_EMPTY       (1),
      .ST_BEATSPERCYCLE(1)
  ) st_sink_tx (
      .clk(coreclkout_hip),
      .reset(reset_status),
      .sink_data(tx_st_data),
      .sink_channel(),
      .sink_valid(tx_st_valid),
      .sink_startofpacket(tx_st_sop),
      .sink_endofpacket(tx_st_eop),
      .sink_error(tx_st_err),
      .sink_empty(tx_st_empty),
      .sink_ready(tx_st_ready)
  );

  altera_avalon_st_sink_bfm #(
      .ST_SYMBOL_W     (8),
      .ST_NUMSYMBOLS   (256 / 8),
      .ST_CHANNEL_W    (1),
      .ST_ERROR_W      (1),
      .ST_EMPTY_W      (8),
      .ST_READY_LATENCY(0),
      .ST_MAX_CHANNELS (1),
      .USE_PACKET      (0),
      .USE_CHANNEL     (0),
      .USE_ERROR       (0),
      .USE_READY       (0),
      .USE_VALID       (1),
      .USE_EMPTY       (1),
      .ST_BEATSPERCYCLE(1)
  ) st_sink_data (
      .clk(coreclkout_hip),
      .reset(reset_status),
      .sink_data(data_out_data),
      .sink_channel(),
      .sink_valid(data_out_valid),
      .sink_startofpacket(),
      .sink_endofpacket(),
      .sink_error(),
      .sink_empty(data_out_empty),
      .sink_ready()
  );

  //============================================================================
  // DUT

  PcieEndpointWrapper DUT (
      .coreclkout_hip,
      .reset_status,
      .serdes_pll_locked,
      .pld_clk_inuse,
      .pld_core_ready,
      .testin_zero,
      .pld_clk_hip,
      .rx_st_data,
      .rx_st_sop,
      .rx_st_eop,
      .rx_st_empty,
      .rx_st_ready,
      .rx_st_valid,
      .rx_st_err,
      .rx_st_mask,
      .rx_st_bar,
      .rx_st_be,
      .tx_st_data,
      .tx_st_sop,
      .tx_st_eop,
      .tx_st_ready,
      .tx_st_valid,
      .tx_st_empty,
      .tx_st_err,
      .tl_cfg_ctl,
      .tl_cfg_add,
      .tl_cfg_sts,
      .cpl_err,
      .cpl_pending,
      .hpg_ctrler,
      .app_int_msi_req,
      .app_int_msi_ack,
      .app_int_msi_tc,
      .app_int_msi_num,
      .app_int_sts,
      .app_int_ack,
      .data_out_data,
      .data_out_valid,
      .data_out_empty,
      .avmm_bar0_address,
      .avmm_bar0_byteenable,
      .avmm_bar0_read,
      .avmm_bar0_readdata,
      .avmm_bar0_response,
      .avmm_bar0_write,
      .avmm_bar0_writedata,
      .avmm_bar0_waitrequest,
      .avmm_bar0_readdatavalid,
      .avmm_bar0_writeresponsevalid,
      .avmm_bar0_burstcount,
      .reset_out
  );

endmodule
