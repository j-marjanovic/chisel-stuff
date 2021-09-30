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

module pp_sp_pcie_endpoint_tb_cpld2;

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

    `include "cpld_stim.h"


    #(100ns);


    $finish();
  end

endmodule
