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

package PciePackets;
  enum bit [2:0] {
    FMT_THREEDW     = 0,
    FMT_FOURDW      = 1,
    FMT_THREEDWDATA = 2,
    FMT_FOURDWDATA  = 3,
    FMT_PREFIX      = 4
  } e_Fmt;

  typedef struct packed {
    bit [31:0] Dw1;
    bit [31:0] Dw0;
    bit [31:0] Dw0_unalign;

    bit [29:0] Addr30_2;
    bit [1:0] ProcHint;

    bit [15:0] ReqID;
    bit [7:0] Tag;
    bit [3:0] LastBE;
    bit [3:0] FirstBE;

    // byte 3-0
    bit [2:0] Fmt;
    bit [4:0] Type;
    bit rsvd2;
    bit [2:0] TrClass;
    bit rsvd1;
    bit Attr2;
    bit rsvd0;
    bit TH;
    bit TD;
    bit EP;
    bit [1:0] Attr1_0;
    bit [1:0] AddrType;
    bit [9:0] Length;
  } t_mwr_mrd;

  typedef struct packed {
    bit [31:0] Dw1;
    bit [31:0] Dw0;
    bit [31:0] Dw0_unalign;

    // bit 11-8
    bit [15:0] ReqID;
    bit [7:0] Tag;
    bit rsvd;
    bit [6:0] LoAddr;

    // bit 7-4
    bit [15:0] ComplID;
    bit [2:0] ComplStatus;
    bit BCM;
    bit [11:0] ByteCount;

    // byte 3-0
    bit [2:0] Fmt;
    bit [4:0] Type;
    bit rsvd2;
    bit [2:0] TrClass;
    bit rsvd1;
    bit Attr2;
    bit rsvd0;
    bit TH;
    bit TD;
    bit EP;
    bit [1:0] Attr1_0;
    bit [1:0] AddrType;
    bit [9:0] Length;
  } t_cpld;

endpackage
