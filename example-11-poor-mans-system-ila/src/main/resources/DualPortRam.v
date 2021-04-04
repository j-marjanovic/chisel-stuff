/*
Copyright (c) 2018-2021 Jan Marjanovic

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

module DualPortRam #(
    parameter RAM_WIDTH = 64,
    parameter RAM_DEPTH = 512
) (
    input                                clk,
    input      [$clog2(RAM_DEPTH-1)-1:0] addra,
    input      [          RAM_WIDTH-1:0] dina,
    output reg [          RAM_WIDTH-1:0] douta,
    input                                wea,

    input      [$clog2(RAM_DEPTH-1)-1:0] addrb,
    input      [          RAM_WIDTH-1:0] dinb,
    output reg [          RAM_WIDTH-1:0] doutb,
    input                                web
);

  reg [RAM_WIDTH-1:0] BRAM[RAM_DEPTH-1:0];


  always @(posedge clk) begin : proc_porta
    if (wea) begin
      BRAM[addra] <= dina;
    end

    douta <= BRAM[addra];
  end

  always @(posedge clk) begin : proc_portb
    if (web) begin
      BRAM[addrb] <= dinb;
    end

    doutb <= BRAM[addrb];
  end

endmodule

