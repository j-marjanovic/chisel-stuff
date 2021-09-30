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

module pp_sp_pcie_endpoint_tb_cpld;

  //============================================================================
  // test harness

  pp_sp_pcie_endpoint_th th ();

  //============================================================================
  // tasks


  //============================================================================
  // main

  initial begin : proc_main
    automatic bit [255:0] resp_data;
    automatic bit [  7:0] resp_empty;

    @(negedge th.reset_status);
    th.st_sink_tx.set_ready(1);
    #(100ns);


    $display("%t ### 4 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'hB1819574B68BBE653F6CF79F00010000000000000400000000F900044A000001);
    th.st_source_rx.set_transaction_empty('h1);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();

    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 8 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'hD3598A69000000000003000200010000000000000400000000F900084A000002);
    th.st_source_rx.set_transaction_empty('h1);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();

    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 16 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'h00070006000500040003000200010000000000000400000000F900104A000004);
    th.st_source_rx.set_transaction_empty('h1);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();

    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 32 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'h00070006000500040003000200010000000000000400000000F900204A000008);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h9AEBF45A1809E6EBB6F827ED00000000000F000E000D000C000B000A00090008);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();

    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 64 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'h00070006000500040003000200010000000000000400000000F900404A000010);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h00170016001500140013001200110010000F000E000D000C000B000A00090008);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'hE300332A0B3C03B6F98E03BD00000000001F001E001D001C001B001A00190018);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 72 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'h00070006000500040003000200010000000000000400000000F900484A000010);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h00170016001500140013001200110010000F000E000D000C000B000A00090008);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h400000040800F9000200004A00000000001F001E001D001C001B001A00190018);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'hB47F985F000000000023002200210020000000000400004000F900084A000002);
    th.st_source_rx.set_transaction_empty('h1);
    th.st_source_rx.set_transaction_sop('h1);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    #(100ns);


    $display("%t ### 128 bytes", $time);
    th.st_source_rx.set_transaction_data(
        'h00070006000500040003000200010000000000000400000000F900804A000020);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h00170016001500140013001200110010000F000E000D000C000B000A00090008);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h00270026002500240023002200210020001F001E001D001C001B001A00190018);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h00370036003500340033003200310030002F002E002D002C002B002A00290028);
    th.st_source_rx.set_transaction_empty('h0);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h0);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();
    th.st_source_rx.set_transaction_data(
        'h149FC27789E74046C7430EBA00000000003F003E003D003C003B003A00390038);
    th.st_source_rx.set_transaction_empty('h2);
    th.st_source_rx.set_transaction_sop('h0);
    th.st_source_rx.set_transaction_eop('h1);
    th.st_source_rx.set_transaction_error('h0);
    th.st_source_rx.set_transaction_channel('h0);
    th.st_source_rx.push_transaction();

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    @th.st_sink_data.signal_transaction_received;
    th.st_sink_data.pop_transaction();
    resp_data  = th.st_sink_data.get_transaction_data();
    resp_empty = th.st_sink_data.get_transaction_empty();
    $display("%t data = %x, empty = %x", $time, resp_data, resp_empty);

    $finish();
  end

endmodule
