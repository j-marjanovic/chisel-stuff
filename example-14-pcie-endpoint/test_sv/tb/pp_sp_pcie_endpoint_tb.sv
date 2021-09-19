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

module pp_sp_pcie_endpoint_tb;

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

  task read_bar2(input bit [31:0] addr, output logic [31:0] data);
    automatic t_mwr_mrd mrd;
    automatic bit [255:0] resp_data;
    automatic bit [1:0] resp_empty;
    automatic bit resp_sop, resp_eop;
    automatic t_cpld resp_pkt;

    addr = addr | 32'hdf8c_0000;
    addr = addr >> 2;
    mrd = '{Fmt: FMT_THREEDW, Type:0, Length: 1, FirstBE: 'hf, ReqID: 'h18, Addr30_2: addr, default: 0};

    th.st_sink_tx.set_ready(1);

    th.st_source_rx.set_transaction_data(mrd);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h4);
    th.st_source_rx.push_transaction();

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

    // th.st_sink_tx.set_ready(1);

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

  task _get_single_packet(output DataPacket pkt);
    automatic bit [255:0] resp_data;
    automatic bit [  1:0] resp_empty;
    automatic bit resp_sop, resp_eop;
    automatic t_mwr_mrd resp_pkt;
    byte unsigned payload[];

    for (int i = 0; i < 100; i++) begin
      th.st_sink_tx.pop_transaction();
      resp_data  = th.st_sink_tx.get_transaction_data();
      resp_empty = th.st_sink_tx.get_transaction_empty();
      resp_sop   = th.st_sink_tx.get_transaction_sop();
      resp_eop   = th.st_sink_tx.get_transaction_eop();
      resp_pkt   = resp_data;

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
        break;
      end

      assert (i < 99)
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
    DataPacket pkt;
    while (th.st_sink_tx.get_transaction_queue_size() > 0) begin
      _get_single_packet(pkt);
      _check_payload(pkt, start_value);
    end
    $display("%5t start value at the end = %x", $time, start_value);
    $display("%5t OK: data check completed without errors", $time);
  endtask

  //============================================================================
  // main

  initial begin : proc_main
    logic [31:0] read_data;
    @(negedge th.reset_status);
    #(100ns);

    read_bar2('h0, read_data);
    assert (read_data == 32'h0d3a01a2)
    else $display("%5t ERROR: ID reg = %x", $time, read_data);
    read_bar2('h4, read_data);
    assert ((read_data >> 16) < 10);  // version less than 10.x.x

    write_bar2('h20, 32'h3000_0000);
    write_bar2('h24, 32'h0000_0007);
    write_bar2('h28, 32'h0000_4000);
    write_bar2('h14, 32'h1);
    #(10000ns);
    check_data();

    ready_backpressure = 70;
    write_bar2('h20, 32'h3000_0000);
    write_bar2('h24, 32'h0000_0007);
    write_bar2('h28, 32'h0000_4000);
    write_bar2('h14, 32'h1);
    #(15000ns);
    check_data();

    $finish();
  end

endmodule
