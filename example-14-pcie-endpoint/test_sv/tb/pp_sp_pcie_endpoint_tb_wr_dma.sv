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

module pp_sp_pcie_endpoint_tb_wr_dma;

  //============================================================================
  // test harness

  pp_sp_pcie_endpoint_th th ();

  //============================================================================
  // tasks

  import PciePackets::*;

  int ready_backpressure = 100;
  always @(posedge th.coreclkout_hip) begin : proc_ready_ctrl
    automatic int val = $urandom % 100;
    th.st_sink_tx.set_ready(val < ready_backpressure);
    // $display("val = %d", val);
  end

  task read_bar2(input bit [31:0] addr, output logic [31:0] data, input bit ignore_resp = 0);
    automatic t_mwr_mrd mrd;
    automatic bit [255:0] resp_data;
    automatic bit [1:0] resp_empty;
    automatic bit resp_sop, resp_eop;
    automatic t_cpld resp_pkt;

    addr = addr | 32'hdf8c_0000;
    addr = addr >> 2;
    mrd = '{Fmt: FMT_THREEDW, Type:0, Length: 1, FirstBE: 'hf, ReqID: 'h18, Addr30_2: addr, default: 0};

    th.st_source_rx.set_transaction_data(mrd);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h4);
    th.st_source_rx.push_transaction();

    if (!ignore_resp) begin
      @th.st_sink_tx.signal_transaction_received;
      th.st_sink_tx.pop_transaction();
      resp_data  = th.st_sink_tx.get_transaction_data();
      resp_empty = th.st_sink_tx.get_transaction_empty();
      resp_sop   = th.st_sink_tx.get_transaction_sop();
      resp_eop   = th.st_sink_tx.get_transaction_eop();
      resp_pkt   = resp_data;

      $display("%5t Packet received:", $time);
      $display("        data  = %x", resp_data);
      $display("        empty = %x", resp_empty);
      $display("        sop   = %x", resp_sop);
      $display("        eop   = %x", resp_eop);
      $displayh("        pkt   = %p", resp_pkt);

      // address was alredy shifted before, now it is a DW address
      if (addr[0]) begin
        data = resp_pkt.Dw0_unalign;
      end else begin
        data = resp_pkt.Dw0;
      end
    end else begin
      data = '1;
    end
  endtask

  task write_bar2(input bit [31:0] addr, input logic [31:0] data);
    automatic t_mwr_mrd mwr;

    addr = addr | 32'hdf8c_0000;
    addr = addr >> 2;
    mwr = '{Fmt: FMT_THREEDWDATA, Type:0, Length: 1, FirstBE: 'hf, ReqID: 'h18, Addr30_2: addr, default: 0};

    // address was alredy shifted before, now it is a DW address
    if (addr[0]) begin
      mwr.Dw0_unalign = data;
    end else begin
      mwr.Dw0 = data;
    end

    th.st_source_rx.set_transaction_data(mwr);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h4);
    th.st_source_rx.push_transaction();

    @th.st_source_rx.signal_src_transaction_complete;

  endtask

  typedef struct {
    t_mwr_mrd hdr;
    byte unsigned payload[];
  } DataPacket;

  DataPacket pkts[$];

  task get_single_packet;
    const int MAX_PKT_LEN = 32;
    automatic DataPacket pkt;
    automatic bit [255:0] resp_data;
    automatic bit [1:0] resp_empty;
    automatic bit resp_sop, resp_eop;
    automatic t_mwr_mrd resp_pkt;
    byte unsigned payload[];

    for (int i = 0; i < MAX_PKT_LEN; i++) begin
      while (th.st_sink_tx.get_transaction_queue_size() == 0) begin
        #(10ns);
      end
      if (i != 0) begin
        th.st_sink_tx.pop_transaction();
      end
      resp_data  = th.st_sink_tx.get_transaction_data();
      resp_empty = th.st_sink_tx.get_transaction_empty();
      resp_sop   = th.st_sink_tx.get_transaction_sop();
      resp_eop   = th.st_sink_tx.get_transaction_eop();
      resp_pkt   = resp_data;

      $display(" get single packet, resp_data = %x %x %x", resp_data, resp_sop, resp_eop);

      if (i == 0) begin
        automatic bit [0:15][7:0] beat_bytes;

        pkt.hdr = resp_pkt;

        beat_bytes = resp_data[255:128];
        payload = {<<8{beat_bytes}};
      end else begin
        automatic bit [0:31][7:0] beat_bytes = resp_data;
        automatic bit [7:0] beat_bytes_unpack[32] = {<<8{beat_bytes}};

        case (resp_empty)
          2'b00: payload = {payload, beat_bytes_unpack[0:32-1]};
          2'b01: payload = {payload, beat_bytes_unpack[0:24-1]};
          2'b10: payload = {payload, beat_bytes_unpack[0:16-1]};
          2'b11: payload = {payload, beat_bytes_unpack[0:8-1]};
        endcase
      end

      if (resp_eop) begin
        pkt.payload = payload;
        pkts.push_back(pkt);
        break;
      end

      assert (i < MAX_PKT_LEN - 1)
      else $display("%5t ERROR: EOP not received", $time);
    end
  endtask

  task _check_payload(input DataPacket pkt, inout bit [15:0] start_value);
    automatic bit [15:0] arr[] = {<<16{{<<8{pkt.payload}}}};
    for (int i = 0; i < arr.size(); i++) begin
      assert (arr[i] == start_value)
      else begin
        $display("ERROR: missmatch at addr %d, got %x, expected %x", i, arr[i], start_value);
        $finish;
      end
      start_value += 1;
    end
  endtask

  task check_data();
    automatic bit [15:0] start_value = 0;

    while (pkts.size() > 0) begin
      automatic DataPacket pkt = pkts.pop_front();
      _check_payload(pkt, start_value);
    end
    $display("%5t start value at the end = %x", $time, start_value);
    $display("%5t OK: data check completed without errors", $time);
  endtask

  //============================================================================
  // data handler

  bit handler_stop = 0;

  task send_read_status;
    logic [31:0] read_data;

    while (!handler_stop) begin
      read_bar2('h10, read_data, 1);
      #(1000ns);
    end
  endtask

  task recv_read_status;
    automatic bit [255:0] resp_data;
    automatic bit [  1:0] resp_empty;
    automatic bit resp_sop, resp_eop;
    automatic t_mwr_mrd resp_pkt;
    byte unsigned payload[];

    resp_data  = th.st_sink_tx.get_transaction_data();
    resp_empty = th.st_sink_tx.get_transaction_empty();
    resp_sop   = th.st_sink_tx.get_transaction_sop();
    resp_eop   = th.st_sink_tx.get_transaction_eop();
    resp_pkt   = resp_data;

    $display("  completion, %x", resp_pkt.Dw0);
    if (!resp_pkt.Dw0[0]) begin
      handler_stop = 1;
    end
  endtask

  task handle_recv_data;
    automatic t_mwr_mrd recv_pkt;

    while (!handler_stop) begin
      while (th.st_sink_tx.get_transaction_queue_size() > 0) begin
        th.st_sink_tx.pop_transaction();
        recv_pkt = th.st_sink_tx.get_transaction_data();
        $display("data = %x, %x, %x", recv_pkt, recv_pkt.Fmt, recv_pkt.Type);
        if ((recv_pkt.Fmt == FMT_THREEDWDATA) && (recv_pkt.Type == 'hA)) begin
          $display("  completion");
          recv_read_status();
        end else if ((recv_pkt.Fmt == FMT_FOURDWDATA) && (recv_pkt.Type == 0)) begin
          $display("  mwr");
          get_single_packet();
        end else begin
          assert (0)
          else $display("%5t unsupported packet header (%x)", $time, recv_pkt);
          $finish();
        end
      end
      #(100ns);
    end
  endtask

  task prepare_dma_data;
    const int N = 16;
    bit [255:0] tmp;
    for (int j = 0; j < N; j++) begin
      tmp[j*16 +: 16] = j;
    end

    for (int i = 0; i < 'h4000 / 32; i++) begin
      th.st_source_data.set_transaction_data(tmp);
      th.st_source_data.push_transaction();
      for (int j = 0; j < N; j++) begin
        tmp[j*16 +: 16] += N;
      end
    end
  endtask

  //============================================================================
  // main

  initial begin : proc_main
    logic [31:0] read_data;
    @(negedge th.reset_status);
    th.st_sink_tx.set_ready(1);
    #(100ns);

    read_bar2('h0, read_data);
    assert (read_data == 32'h0d3a01a2)
    else $display("%5t ERROR: ID reg = %x", $time, read_data);
    read_bar2('h4, read_data);
    assert ((read_data >> 16) < 10);  // version less than 10.x.x


    $display("==============================================================");
    prepare_dma_data();
    write_bar2('h20, 32'h3000_0000);
    write_bar2('h24, 32'h0000_0007);
    write_bar2('h28, 32'h0000_4000);
    write_bar2('h2c, 32'h8000_0001);
    write_bar2('h14, 32'h1);

    handler_stop = 0;
    fork
      handle_recv_data();
      send_read_status();
    join

    check_data();


    $display("==============================================================");
    ready_backpressure = 70;
    prepare_dma_data();
    write_bar2('h20, 32'h3000_0000);
    write_bar2('h24, 32'h0000_0007);
    write_bar2('h28, 32'h0000_4000);
    write_bar2('h2c, 32'h8000_0002);
    write_bar2('h14, 32'h1);

    handler_stop = 0;
    fork
      handle_recv_data();
      send_read_status();
    join

    check_data();

    $finish();
  end

endmodule
