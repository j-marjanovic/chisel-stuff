module ByteStreamToBitStream( // @[:@3.2]
  input        clock, // @[:@4.4]
  input        reset, // @[:@5.4]
  output       io_data_in_ready, // @[:@6.4]
  input        io_data_in_valid, // @[:@6.4]
  input  [7:0] io_data_in_bits, // @[:@6.4]
  input        io_data_out_ready, // @[:@6.4]
  output       io_data_out_valid, // @[:@6.4]
  output [7:0] io_data_out_bits // @[:@6.4]
);
  reg [3:0] sel; // @[ByteStreamToBitStream.scala 36:20:@8.4]
  reg [31:0] _RAND_0;
  wire  _T_26; // @[ByteStreamToBitStream.scala 38:27:@9.4]
  wire  _T_28; // @[ByteStreamToBitStream.scala 39:28:@11.4]
  wire  _T_31; // @[ByteStreamToBitStream.scala 41:21:@14.4]
  wire  _T_33; // @[ByteStreamToBitStream.scala 42:15:@16.6]
  wire [4:0] _T_36; // @[ByteStreamToBitStream.scala 45:18:@21.8]
  wire [3:0] _T_37; // @[ByteStreamToBitStream.scala 45:18:@22.8]
  wire [3:0] _GEN_0; // @[ByteStreamToBitStream.scala 42:24:@17.6]
  wire  _T_40; // @[ByteStreamToBitStream.scala 47:28:@28.6]
  wire [3:0] _GEN_1; // @[ByteStreamToBitStream.scala 47:49:@29.6]
  wire [3:0] _GEN_2; // @[ByteStreamToBitStream.scala 41:43:@15.4]
  reg [7:0] data_reg; // @[ByteStreamToBitStream.scala 51:21:@32.4]
  reg [31:0] _RAND_1;
  wire [4:0] _T_47; // @[ByteStreamToBitStream.scala 57:36:@38.4]
  wire [4:0] _T_48; // @[ByteStreamToBitStream.scala 57:36:@39.4]
  wire [3:0] _T_49; // @[ByteStreamToBitStream.scala 57:36:@40.4]
  wire [7:0] _T_50; // @[ByteStreamToBitStream.scala 57:31:@41.4]
  wire  _T_51; // @[ByteStreamToBitStream.scala 57:31:@42.4]
  assign _T_26 = sel == 4'h0; // @[ByteStreamToBitStream.scala 38:27:@9.4]
  assign _T_28 = sel != 4'h0; // @[ByteStreamToBitStream.scala 39:28:@11.4]
  assign _T_31 = _T_28 & io_data_out_ready; // @[ByteStreamToBitStream.scala 41:21:@14.4]
  assign _T_33 = sel == 4'h8; // @[ByteStreamToBitStream.scala 42:15:@16.6]
  assign _T_36 = sel + 4'h1; // @[ByteStreamToBitStream.scala 45:18:@21.8]
  assign _T_37 = sel + 4'h1; // @[ByteStreamToBitStream.scala 45:18:@22.8]
  assign _GEN_0 = _T_33 ? 4'h0 : _T_37; // @[ByteStreamToBitStream.scala 42:24:@17.6]
  assign _T_40 = _T_26 & io_data_in_valid; // @[ByteStreamToBitStream.scala 47:28:@28.6]
  assign _GEN_1 = _T_40 ? 4'h1 : sel; // @[ByteStreamToBitStream.scala 47:49:@29.6]
  assign _GEN_2 = _T_31 ? _GEN_0 : _GEN_1; // @[ByteStreamToBitStream.scala 41:43:@15.4]
  assign _T_47 = sel - 4'h1; // @[ByteStreamToBitStream.scala 57:36:@38.4]
  assign _T_48 = $unsigned(_T_47); // @[ByteStreamToBitStream.scala 57:36:@39.4]
  assign _T_49 = _T_48[3:0]; // @[ByteStreamToBitStream.scala 57:36:@40.4]
  assign _T_50 = data_reg >> _T_49; // @[ByteStreamToBitStream.scala 57:31:@41.4]
  assign _T_51 = _T_50[0]; // @[ByteStreamToBitStream.scala 57:31:@42.4]
  assign io_data_in_ready = sel == 4'h0; // @[ByteStreamToBitStream.scala 38:20:@10.4]
  assign io_data_out_valid = sel != 4'h0; // @[ByteStreamToBitStream.scala 39:21:@12.4]
  assign io_data_out_bits = {{7'd0}, _T_51}; // @[ByteStreamToBitStream.scala 57:20:@43.4]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  sel = _RAND_0[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  data_reg = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      sel <= 4'h0;
    end else begin
      if (_T_31) begin
        if (_T_33) begin
          sel <= 4'h0;
        end else begin
          sel <= _T_37;
        end
      end else begin
        if (_T_40) begin
          sel <= 4'h1;
        end
      end
    end
    if (_T_40) begin
      data_reg <= io_data_in_bits;
    end
  end
endmodule
