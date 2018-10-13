module PipelineWithAxiLiteSlave( // @[:@3.2]
  input         clock, // @[:@4.4]
  input         reset, // @[:@5.4]
  output        io_ctrl_AW_ready, // @[:@6.4]
  input         io_ctrl_AW_valid, // @[:@6.4]
  input  [3:0]  io_ctrl_AW_bits, // @[:@6.4]
  output        io_ctrl_W_ready, // @[:@6.4]
  input         io_ctrl_W_valid, // @[:@6.4]
  input  [31:0] io_ctrl_W_bits_wdata, // @[:@6.4]
  input  [3:0]  io_ctrl_W_bits_wstrb, // @[:@6.4]
  input         io_ctrl_B_ready, // @[:@6.4]
  output        io_ctrl_B_valid, // @[:@6.4]
  output        io_ctrl_AR_ready, // @[:@6.4]
  input         io_ctrl_AR_valid, // @[:@6.4]
  input  [3:0]  io_ctrl_AR_bits, // @[:@6.4]
  input         io_ctrl_R_ready, // @[:@6.4]
  output        io_ctrl_R_valid, // @[:@6.4]
  output [31:0] io_ctrl_R_bits_rdata, // @[:@6.4]
  output [15:0] io_coef, // @[:@6.4]
  input  [31:0] io_stats_nr_samp // @[:@6.4]
);
  reg [15:0] REG_COEF; // @[PipelineWithAxiLiteSlave.scala 48:28:@8.4]
  reg [31:0] _RAND_0;
  reg [1:0] state_wr; // @[PipelineWithAxiLiteSlave.scala 56:25:@10.4]
  reg [31:0] _RAND_1;
  reg  wr_en; // @[PipelineWithAxiLiteSlave.scala 58:20:@11.4]
  reg [31:0] _RAND_2;
  reg [1:0] wr_addr; // @[PipelineWithAxiLiteSlave.scala 59:20:@12.4]
  reg [31:0] _RAND_3;
  reg [31:0] wr_data; // @[PipelineWithAxiLiteSlave.scala 60:20:@13.4]
  reg [31:0] _RAND_4;
  reg [3:0] wr_strb; // @[PipelineWithAxiLiteSlave.scala 61:20:@14.4]
  reg [31:0] _RAND_5;
  wire  _T_65; // @[Conditional.scala 37:30:@16.4]
  wire  _T_66; // @[PipelineWithAxiLiteSlave.scala 69:30:@18.6]
  wire [1:0] _T_68; // @[PipelineWithAxiLiteSlave.scala 71:36:@21.8]
  wire [31:0] _GEN_0; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  wire [3:0] _GEN_1; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  wire [1:0] _GEN_2; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  wire [1:0] _GEN_3; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  wire [1:0] _GEN_4; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  wire [31:0] _GEN_5; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  wire [3:0] _GEN_6; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  wire [1:0] _GEN_8; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  wire [31:0] _GEN_9; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  wire [3:0] _GEN_10; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  wire [1:0] _GEN_11; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  wire  _T_70; // @[Conditional.scala 37:30:@41.6]
  wire [1:0] _GEN_13; // @[PipelineWithAxiLiteSlave.scala 85:30:@43.8]
  wire  _T_72; // @[Conditional.scala 37:30:@51.8]
  wire [1:0] _GEN_16; // @[PipelineWithAxiLiteSlave.scala 93:31:@53.10]
  wire  _T_75; // @[Conditional.scala 37:30:@61.10]
  wire [1:0] _GEN_17; // @[PipelineWithAxiLiteSlave.scala 100:30:@63.12]
  wire [1:0] _GEN_18; // @[Conditional.scala 39:67:@62.10]
  wire  _GEN_19; // @[Conditional.scala 39:67:@52.8]
  wire [1:0] _GEN_20; // @[Conditional.scala 39:67:@52.8]
  wire [1:0] _GEN_21; // @[Conditional.scala 39:67:@52.8]
  wire  _GEN_22; // @[Conditional.scala 39:67:@42.6]
  wire [31:0] _GEN_23; // @[Conditional.scala 39:67:@42.6]
  wire [3:0] _GEN_24; // @[Conditional.scala 39:67:@42.6]
  wire [1:0] _GEN_25; // @[Conditional.scala 39:67:@42.6]
  wire [1:0] _GEN_26; // @[Conditional.scala 39:67:@42.6]
  wire [1:0] _GEN_31; // @[Conditional.scala 40:58:@17.4]
  wire  _GEN_36; // @[Conditional.scala 39:67:@85.8]
  wire  _GEN_38; // @[Conditional.scala 39:67:@78.6]
  wire  _GEN_39; // @[Conditional.scala 39:67:@78.6]
  wire  _T_97; // @[Conditional.scala 37:30:@100.6]
  wire [3:0] _T_107; // @[PipelineWithAxiLiteSlave.scala 145:21:@103.8]
  wire  _T_109; // @[PipelineWithAxiLiteSlave.scala 145:35:@104.8]
  wire [7:0] _T_110; // @[PipelineWithAxiLiteSlave.scala 146:23:@106.10]
  wire [7:0] _T_111; // @[PipelineWithAxiLiteSlave.scala 148:23:@110.10]
  wire [7:0] _GEN_43; // @[PipelineWithAxiLiteSlave.scala 145:44:@105.8]
  wire [3:0] _T_113; // @[PipelineWithAxiLiteSlave.scala 145:21:@113.8]
  wire  _T_115; // @[PipelineWithAxiLiteSlave.scala 145:35:@114.8]
  wire [7:0] _T_116; // @[PipelineWithAxiLiteSlave.scala 146:23:@116.10]
  wire [7:0] _T_117; // @[PipelineWithAxiLiteSlave.scala 148:23:@120.10]
  wire [7:0] _GEN_44; // @[PipelineWithAxiLiteSlave.scala 145:44:@115.8]
  wire [15:0] _T_118; // @[PipelineWithAxiLiteSlave.scala 152:15:@123.8]
  wire [15:0] _GEN_45; // @[Conditional.scala 40:58:@101.6]
  wire [15:0] _GEN_46; // @[PipelineWithAxiLiteSlave.scala 155:16:@99.4]
  reg [1:0] state_rd; // @[PipelineWithAxiLiteSlave.scala 165:25:@127.4]
  reg [31:0] _RAND_6;
  reg  rd_en; // @[PipelineWithAxiLiteSlave.scala 167:20:@128.4]
  reg [31:0] _RAND_7;
  reg [1:0] rd_addr; // @[PipelineWithAxiLiteSlave.scala 168:20:@129.4]
  reg [31:0] _RAND_8;
  reg [33:0] rd_data; // @[PipelineWithAxiLiteSlave.scala 169:20:@130.4]
  reg [63:0] _RAND_9;
  wire  _T_124; // @[Conditional.scala 37:30:@132.4]
  wire [1:0] _T_126; // @[PipelineWithAxiLiteSlave.scala 178:36:@136.8]
  wire [1:0] _GEN_48; // @[PipelineWithAxiLiteSlave.scala 176:31:@134.6]
  wire [1:0] _GEN_49; // @[PipelineWithAxiLiteSlave.scala 176:31:@134.6]
  wire  _T_127; // @[Conditional.scala 37:30:@142.6]
  wire  _T_128; // @[Conditional.scala 37:30:@147.8]
  wire [1:0] _GEN_50; // @[PipelineWithAxiLiteSlave.scala 186:30:@149.10]
  wire [1:0] _GEN_51; // @[Conditional.scala 39:67:@148.8]
  wire [1:0] _GEN_52; // @[Conditional.scala 39:67:@143.6]
  wire [1:0] _GEN_55; // @[Conditional.scala 40:58:@133.4]
  wire [33:0] _GEN_58; // @[Conditional.scala 39:67:@171.8]
  wire  _GEN_60; // @[Conditional.scala 39:67:@164.6]
  wire [33:0] _GEN_61; // @[Conditional.scala 39:67:@164.6]
  wire [33:0] _GEN_64; // @[Conditional.scala 40:58:@157.4]
  wire  _T_143; // @[Conditional.scala 37:30:@177.6]
  wire  _T_144; // @[Conditional.scala 37:30:@182.8]
  wire  _T_145; // @[Conditional.scala 37:30:@187.10]
  wire  _T_146; // @[Conditional.scala 37:30:@192.12]
  wire [33:0] _GEN_65; // @[Conditional.scala 39:67:@193.12]
  wire [33:0] _GEN_66; // @[Conditional.scala 39:67:@188.10]
  wire [33:0] _GEN_67; // @[Conditional.scala 39:67:@183.8]
  wire [33:0] _GEN_68; // @[Conditional.scala 40:58:@178.6]
  assign _T_65 = 2'h0 == state_wr; // @[Conditional.scala 37:30:@16.4]
  assign _T_66 = io_ctrl_AW_valid & io_ctrl_W_valid; // @[PipelineWithAxiLiteSlave.scala 69:30:@18.6]
  assign _T_68 = io_ctrl_AW_bits[3:2]; // @[PipelineWithAxiLiteSlave.scala 71:36:@21.8]
  assign _GEN_0 = io_ctrl_W_valid ? io_ctrl_W_bits_wdata : wr_data; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  assign _GEN_1 = io_ctrl_W_valid ? io_ctrl_W_bits_wstrb : wr_strb; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  assign _GEN_2 = io_ctrl_W_valid ? 2'h2 : state_wr; // @[PipelineWithAxiLiteSlave.scala 78:37:@34.10]
  assign _GEN_3 = io_ctrl_AW_valid ? _T_68 : wr_addr; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  assign _GEN_4 = io_ctrl_AW_valid ? 2'h1 : _GEN_2; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  assign _GEN_5 = io_ctrl_AW_valid ? wr_data : _GEN_0; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  assign _GEN_6 = io_ctrl_AW_valid ? wr_strb : _GEN_1; // @[PipelineWithAxiLiteSlave.scala 75:38:@28.8]
  assign _GEN_8 = _T_66 ? _T_68 : _GEN_3; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  assign _GEN_9 = _T_66 ? io_ctrl_W_bits_wdata : _GEN_5; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  assign _GEN_10 = _T_66 ? io_ctrl_W_bits_wstrb : _GEN_6; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  assign _GEN_11 = _T_66 ? 2'h3 : _GEN_4; // @[PipelineWithAxiLiteSlave.scala 69:50:@19.6]
  assign _T_70 = 2'h1 == state_wr; // @[Conditional.scala 37:30:@41.6]
  assign _GEN_13 = io_ctrl_W_valid ? 2'h3 : state_wr; // @[PipelineWithAxiLiteSlave.scala 85:30:@43.8]
  assign _T_72 = 2'h2 == state_wr; // @[Conditional.scala 37:30:@51.8]
  assign _GEN_16 = io_ctrl_AW_valid ? 2'h3 : state_wr; // @[PipelineWithAxiLiteSlave.scala 93:31:@53.10]
  assign _T_75 = 2'h3 == state_wr; // @[Conditional.scala 37:30:@61.10]
  assign _GEN_17 = io_ctrl_B_ready ? 2'h0 : state_wr; // @[PipelineWithAxiLiteSlave.scala 100:30:@63.12]
  assign _GEN_18 = _T_75 ? _GEN_17 : state_wr; // @[Conditional.scala 39:67:@62.10]
  assign _GEN_19 = _T_72 ? io_ctrl_AW_valid : 1'h0; // @[Conditional.scala 39:67:@52.8]
  assign _GEN_20 = _T_72 ? _GEN_3 : wr_addr; // @[Conditional.scala 39:67:@52.8]
  assign _GEN_21 = _T_72 ? _GEN_16 : _GEN_18; // @[Conditional.scala 39:67:@52.8]
  assign _GEN_22 = _T_70 ? io_ctrl_W_valid : _GEN_19; // @[Conditional.scala 39:67:@42.6]
  assign _GEN_23 = _T_70 ? _GEN_0 : wr_data; // @[Conditional.scala 39:67:@42.6]
  assign _GEN_24 = _T_70 ? _GEN_1 : wr_strb; // @[Conditional.scala 39:67:@42.6]
  assign _GEN_25 = _T_70 ? _GEN_13 : _GEN_21; // @[Conditional.scala 39:67:@42.6]
  assign _GEN_26 = _T_70 ? wr_addr : _GEN_20; // @[Conditional.scala 39:67:@42.6]
  assign _GEN_31 = _T_65 ? _GEN_11 : _GEN_25; // @[Conditional.scala 40:58:@17.4]
  assign _GEN_36 = _T_70 ? 1'h0 : _T_75; // @[Conditional.scala 39:67:@85.8]
  assign _GEN_38 = _T_72 ? 1'h0 : _T_70; // @[Conditional.scala 39:67:@78.6]
  assign _GEN_39 = _T_72 ? 1'h0 : _GEN_36; // @[Conditional.scala 39:67:@78.6]
  assign _T_97 = 2'h3 == wr_addr; // @[Conditional.scala 37:30:@100.6]
  assign _T_107 = wr_strb & 4'h1; // @[PipelineWithAxiLiteSlave.scala 145:21:@103.8]
  assign _T_109 = _T_107 != 4'h0; // @[PipelineWithAxiLiteSlave.scala 145:35:@104.8]
  assign _T_110 = wr_data[7:0]; // @[PipelineWithAxiLiteSlave.scala 146:23:@106.10]
  assign _T_111 = REG_COEF[7:0]; // @[PipelineWithAxiLiteSlave.scala 148:23:@110.10]
  assign _GEN_43 = _T_109 ? _T_110 : _T_111; // @[PipelineWithAxiLiteSlave.scala 145:44:@105.8]
  assign _T_113 = wr_strb & 4'h2; // @[PipelineWithAxiLiteSlave.scala 145:21:@113.8]
  assign _T_115 = _T_113 != 4'h0; // @[PipelineWithAxiLiteSlave.scala 145:35:@114.8]
  assign _T_116 = wr_data[15:8]; // @[PipelineWithAxiLiteSlave.scala 146:23:@116.10]
  assign _T_117 = REG_COEF[15:8]; // @[PipelineWithAxiLiteSlave.scala 148:23:@120.10]
  assign _GEN_44 = _T_115 ? _T_116 : _T_117; // @[PipelineWithAxiLiteSlave.scala 145:44:@115.8]
  assign _T_118 = {_GEN_44,_GEN_43}; // @[PipelineWithAxiLiteSlave.scala 152:15:@123.8]
  assign _GEN_45 = _T_97 ? _T_118 : REG_COEF; // @[Conditional.scala 40:58:@101.6]
  assign _GEN_46 = wr_en ? _GEN_45 : REG_COEF; // @[PipelineWithAxiLiteSlave.scala 155:16:@99.4]
  assign _T_124 = 2'h0 == state_rd; // @[Conditional.scala 37:30:@132.4]
  assign _T_126 = io_ctrl_AR_bits[3:2]; // @[PipelineWithAxiLiteSlave.scala 178:36:@136.8]
  assign _GEN_48 = io_ctrl_AR_valid ? _T_126 : rd_addr; // @[PipelineWithAxiLiteSlave.scala 176:31:@134.6]
  assign _GEN_49 = io_ctrl_AR_valid ? 2'h1 : state_rd; // @[PipelineWithAxiLiteSlave.scala 176:31:@134.6]
  assign _T_127 = 2'h1 == state_rd; // @[Conditional.scala 37:30:@142.6]
  assign _T_128 = 2'h2 == state_rd; // @[Conditional.scala 37:30:@147.8]
  assign _GEN_50 = io_ctrl_R_ready ? 2'h0 : state_rd; // @[PipelineWithAxiLiteSlave.scala 186:30:@149.10]
  assign _GEN_51 = _T_128 ? _GEN_50 : state_rd; // @[Conditional.scala 39:67:@148.8]
  assign _GEN_52 = _T_127 ? 2'h2 : _GEN_51; // @[Conditional.scala 39:67:@143.6]
  assign _GEN_55 = _T_124 ? _GEN_49 : _GEN_52; // @[Conditional.scala 40:58:@133.4]
  assign _GEN_58 = _T_128 ? rd_data : 34'h0; // @[Conditional.scala 39:67:@171.8]
  assign _GEN_60 = _T_127 ? 1'h0 : _T_128; // @[Conditional.scala 39:67:@164.6]
  assign _GEN_61 = _T_127 ? 34'h0 : _GEN_58; // @[Conditional.scala 39:67:@164.6]
  assign _GEN_64 = _T_124 ? 34'h0 : _GEN_61; // @[Conditional.scala 40:58:@157.4]
  assign _T_143 = 2'h0 == rd_addr; // @[Conditional.scala 37:30:@177.6]
  assign _T_144 = 2'h1 == rd_addr; // @[Conditional.scala 37:30:@182.8]
  assign _T_145 = 2'h2 == rd_addr; // @[Conditional.scala 37:30:@187.10]
  assign _T_146 = 2'h3 == rd_addr; // @[Conditional.scala 37:30:@192.12]
  assign _GEN_65 = _T_146 ? {{18'd0}, REG_COEF} : rd_data; // @[Conditional.scala 39:67:@193.12]
  assign _GEN_66 = _T_145 ? {{2'd0}, io_stats_nr_samp} : _GEN_65; // @[Conditional.scala 39:67:@188.10]
  assign _GEN_67 = _T_144 ? 34'h10100 : _GEN_66; // @[Conditional.scala 39:67:@183.8]
  assign _GEN_68 = _T_143 ? 34'h71711123 : _GEN_67; // @[Conditional.scala 40:58:@178.6]
  assign io_ctrl_AW_ready = _T_65 ? 1'h1 : _T_72; // @[PipelineWithAxiLiteSlave.scala 108:20:@67.4 PipelineWithAxiLiteSlave.scala 114:24:@72.6 PipelineWithAxiLiteSlave.scala 119:24:@79.8 PipelineWithAxiLiteSlave.scala 124:24:@86.10 PipelineWithAxiLiteSlave.scala 129:24:@93.12]
  assign io_ctrl_W_ready = _T_65 ? 1'h1 : _GEN_38; // @[PipelineWithAxiLiteSlave.scala 109:20:@68.4 PipelineWithAxiLiteSlave.scala 115:24:@73.6 PipelineWithAxiLiteSlave.scala 120:24:@80.8 PipelineWithAxiLiteSlave.scala 125:24:@87.10 PipelineWithAxiLiteSlave.scala 130:24:@94.12]
  assign io_ctrl_B_valid = _T_65 ? 1'h0 : _GEN_39; // @[PipelineWithAxiLiteSlave.scala 110:20:@69.4 PipelineWithAxiLiteSlave.scala 116:24:@74.6 PipelineWithAxiLiteSlave.scala 121:24:@81.8 PipelineWithAxiLiteSlave.scala 126:24:@88.10 PipelineWithAxiLiteSlave.scala 131:24:@95.12]
  assign io_ctrl_AR_ready = 2'h0 == state_rd; // @[PipelineWithAxiLiteSlave.scala 192:24:@153.4 PipelineWithAxiLiteSlave.scala 198:28:@158.6 PipelineWithAxiLiteSlave.scala 203:28:@165.8 PipelineWithAxiLiteSlave.scala 208:28:@172.10]
  assign io_ctrl_R_valid = _T_124 ? 1'h0 : _GEN_60; // @[PipelineWithAxiLiteSlave.scala 193:24:@154.4 PipelineWithAxiLiteSlave.scala 199:28:@159.6 PipelineWithAxiLiteSlave.scala 204:28:@166.8 PipelineWithAxiLiteSlave.scala 209:28:@173.10]
  assign io_ctrl_R_bits_rdata = _GEN_64[31:0]; // @[PipelineWithAxiLiteSlave.scala 194:24:@155.4 PipelineWithAxiLiteSlave.scala 200:28:@160.6 PipelineWithAxiLiteSlave.scala 205:28:@167.8 PipelineWithAxiLiteSlave.scala 210:28:@174.10]
  assign io_coef = REG_COEF; // @[PipelineWithAxiLiteSlave.scala 50:11:@9.4]
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
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  REG_COEF = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  state_wr = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_en = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  wr_addr = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_data = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  wr_strb = _RAND_5[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  state_rd = _RAND_6[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  rd_en = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  rd_addr = _RAND_8[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {2{`RANDOM}};
  rd_data = _RAND_9[33:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      REG_COEF <= 16'h0;
    end else begin
      if (wr_en) begin
        if (_T_97) begin
          REG_COEF <= _T_118;
        end
      end
    end
    if (reset) begin
      state_wr <= 2'h0;
    end else begin
      if (_T_65) begin
        if (_T_66) begin
          state_wr <= 2'h3;
        end else begin
          if (io_ctrl_AW_valid) begin
            state_wr <= 2'h1;
          end else begin
            if (io_ctrl_W_valid) begin
              state_wr <= 2'h2;
            end
          end
        end
      end else begin
        if (_T_70) begin
          if (io_ctrl_W_valid) begin
            state_wr <= 2'h3;
          end
        end else begin
          if (_T_72) begin
            if (io_ctrl_AW_valid) begin
              state_wr <= 2'h3;
            end
          end else begin
            if (_T_75) begin
              if (io_ctrl_B_ready) begin
                state_wr <= 2'h0;
              end
            end
          end
        end
      end
    end
    if (_T_65) begin
      wr_en <= _T_66;
    end else begin
      if (_T_70) begin
        wr_en <= io_ctrl_W_valid;
      end else begin
        if (_T_72) begin
          wr_en <= io_ctrl_AW_valid;
        end else begin
          wr_en <= 1'h0;
        end
      end
    end
    if (_T_65) begin
      if (_T_66) begin
        wr_addr <= _T_68;
      end else begin
        if (io_ctrl_AW_valid) begin
          wr_addr <= _T_68;
        end
      end
    end else begin
      if (!(_T_70)) begin
        if (_T_72) begin
          if (io_ctrl_AW_valid) begin
            wr_addr <= _T_68;
          end
        end
      end
    end
    if (_T_65) begin
      if (_T_66) begin
        wr_data <= io_ctrl_W_bits_wdata;
      end else begin
        if (!(io_ctrl_AW_valid)) begin
          if (io_ctrl_W_valid) begin
            wr_data <= io_ctrl_W_bits_wdata;
          end
        end
      end
    end else begin
      if (_T_70) begin
        if (io_ctrl_W_valid) begin
          wr_data <= io_ctrl_W_bits_wdata;
        end
      end
    end
    if (_T_65) begin
      if (_T_66) begin
        wr_strb <= io_ctrl_W_bits_wstrb;
      end else begin
        if (!(io_ctrl_AW_valid)) begin
          if (io_ctrl_W_valid) begin
            wr_strb <= io_ctrl_W_bits_wstrb;
          end
        end
      end
    end else begin
      if (_T_70) begin
        if (io_ctrl_W_valid) begin
          wr_strb <= io_ctrl_W_bits_wstrb;
        end
      end
    end
    if (reset) begin
      state_rd <= 2'h0;
    end else begin
      if (_T_124) begin
        if (io_ctrl_AR_valid) begin
          state_rd <= 2'h1;
        end
      end else begin
        if (_T_127) begin
          state_rd <= 2'h2;
        end else begin
          if (_T_128) begin
            if (io_ctrl_R_ready) begin
              state_rd <= 2'h0;
            end
          end
        end
      end
    end
    if (_T_124) begin
      rd_en <= io_ctrl_AR_valid;
    end else begin
      rd_en <= 1'h0;
    end
    if (_T_124) begin
      if (io_ctrl_AR_valid) begin
        rd_addr <= _T_126;
      end
    end
    if (rd_en) begin
      if (_T_143) begin
        rd_data <= 34'h71711123;
      end else begin
        if (_T_144) begin
          rd_data <= 34'h10000;
        end else begin
          if (_T_145) begin
            rd_data <= {{2'd0}, io_stats_nr_samp};
          end else begin
            if (_T_146) begin
              rd_data <= {{18'd0}, REG_COEF};
            end
          end
        end
      end
    end
  end
endmodule
module PipelineWithAxiLite( // @[:@198.2]
  input         clock, // @[:@199.4]
  input         reset, // @[:@200.4]
  output        io_ctrl_AW_ready, // @[:@201.4]
  input         io_ctrl_AW_valid, // @[:@201.4]
  input  [3:0]  io_ctrl_AW_bits, // @[:@201.4]
  output        io_ctrl_W_ready, // @[:@201.4]
  input         io_ctrl_W_valid, // @[:@201.4]
  input  [31:0] io_ctrl_W_bits_wdata, // @[:@201.4]
  input  [3:0]  io_ctrl_W_bits_wstrb, // @[:@201.4]
  input         io_ctrl_B_ready, // @[:@201.4]
  output        io_ctrl_B_valid, // @[:@201.4]
  output [1:0]  io_ctrl_B_bits, // @[:@201.4]
  output        io_ctrl_AR_ready, // @[:@201.4]
  input         io_ctrl_AR_valid, // @[:@201.4]
  input  [3:0]  io_ctrl_AR_bits, // @[:@201.4]
  input         io_ctrl_R_ready, // @[:@201.4]
  output        io_ctrl_R_valid, // @[:@201.4]
  output [31:0] io_ctrl_R_bits_rdata, // @[:@201.4]
  output [1:0]  io_ctrl_R_bits_rresp, // @[:@201.4]
  input  [15:0] io_in_data, // @[:@201.4]
  input         io_in_valid, // @[:@201.4]
  output [15:0] io_out_data, // @[:@201.4]
  output        io_out_valid // @[:@201.4]
);
  wire  axi_ctrl_clock; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_reset; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_AW_ready; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_AW_valid; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [3:0] axi_ctrl_io_ctrl_AW_bits; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_W_ready; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_W_valid; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [31:0] axi_ctrl_io_ctrl_W_bits_wdata; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [3:0] axi_ctrl_io_ctrl_W_bits_wstrb; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_B_ready; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_B_valid; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_AR_ready; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_AR_valid; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [3:0] axi_ctrl_io_ctrl_AR_bits; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_R_ready; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire  axi_ctrl_io_ctrl_R_valid; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [31:0] axi_ctrl_io_ctrl_R_bits_rdata; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [15:0] axi_ctrl_io_coef; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  wire [31:0] axi_ctrl_io_stats_nr_samp; // @[PipelineWithAxiLite.scala 51:26:@203.4]
  reg [31:0] nr_samples; // @[PipelineWithAxiLite.scala 53:27:@207.4]
  reg [31:0] _RAND_0;
  wire [32:0] _T_65; // @[PipelineWithAxiLite.scala 59:30:@228.6]
  wire [32:0] _GEN_0; // @[PipelineWithAxiLite.scala 58:23:@227.4]
  reg [15:0] p0_data; // @[PipelineWithAxiLite.scala 66:19:@234.4]
  reg [31:0] _RAND_1;
  reg  p0_valid; // @[PipelineWithAxiLite.scala 66:19:@234.4]
  reg [31:0] _RAND_2;
  wire [15:0] coef; // @[PipelineWithAxiLite.scala 52:24:@206.4 PipelineWithAxiLite.scala 55:29:@225.4]
  wire [16:0] _T_70; // @[PipelineWithAxiLite.scala 45:30:@239.4]
  reg [15:0] p1_data; // @[PipelineWithAxiLite.scala 67:19:@241.4]
  reg [31:0] _RAND_3;
  reg  p1_valid; // @[PipelineWithAxiLite.scala 67:19:@241.4]
  reg [31:0] _RAND_4;
  PipelineWithAxiLiteSlave axi_ctrl ( // @[PipelineWithAxiLite.scala 51:26:@203.4]
    .clock(axi_ctrl_clock),
    .reset(axi_ctrl_reset),
    .io_ctrl_AW_ready(axi_ctrl_io_ctrl_AW_ready),
    .io_ctrl_AW_valid(axi_ctrl_io_ctrl_AW_valid),
    .io_ctrl_AW_bits(axi_ctrl_io_ctrl_AW_bits),
    .io_ctrl_W_ready(axi_ctrl_io_ctrl_W_ready),
    .io_ctrl_W_valid(axi_ctrl_io_ctrl_W_valid),
    .io_ctrl_W_bits_wdata(axi_ctrl_io_ctrl_W_bits_wdata),
    .io_ctrl_W_bits_wstrb(axi_ctrl_io_ctrl_W_bits_wstrb),
    .io_ctrl_B_ready(axi_ctrl_io_ctrl_B_ready),
    .io_ctrl_B_valid(axi_ctrl_io_ctrl_B_valid),
    .io_ctrl_AR_ready(axi_ctrl_io_ctrl_AR_ready),
    .io_ctrl_AR_valid(axi_ctrl_io_ctrl_AR_valid),
    .io_ctrl_AR_bits(axi_ctrl_io_ctrl_AR_bits),
    .io_ctrl_R_ready(axi_ctrl_io_ctrl_R_ready),
    .io_ctrl_R_valid(axi_ctrl_io_ctrl_R_valid),
    .io_ctrl_R_bits_rdata(axi_ctrl_io_ctrl_R_bits_rdata),
    .io_coef(axi_ctrl_io_coef),
    .io_stats_nr_samp(axi_ctrl_io_stats_nr_samp)
  );
  assign _T_65 = nr_samples + 32'h1; // @[PipelineWithAxiLite.scala 59:30:@228.6]
  assign _GEN_0 = io_out_valid ? _T_65 : {{1'd0}, nr_samples}; // @[PipelineWithAxiLite.scala 58:23:@227.4]
  assign coef = axi_ctrl_io_coef; // @[PipelineWithAxiLite.scala 52:24:@206.4 PipelineWithAxiLite.scala 55:29:@225.4]
  assign _T_70 = p0_data + coef; // @[PipelineWithAxiLite.scala 45:30:@239.4]
  assign io_ctrl_AW_ready = axi_ctrl_io_ctrl_AW_ready; // @[PipelineWithAxiLite.scala 54:29:@224.4]
  assign io_ctrl_W_ready = axi_ctrl_io_ctrl_W_ready; // @[PipelineWithAxiLite.scala 54:29:@221.4]
  assign io_ctrl_B_valid = axi_ctrl_io_ctrl_B_valid; // @[PipelineWithAxiLite.scala 54:29:@216.4]
  assign io_ctrl_B_bits = 2'h0; // @[PipelineWithAxiLite.scala 54:29:@215.4]
  assign io_ctrl_AR_ready = axi_ctrl_io_ctrl_AR_ready; // @[PipelineWithAxiLite.scala 54:29:@214.4]
  assign io_ctrl_R_valid = axi_ctrl_io_ctrl_R_valid; // @[PipelineWithAxiLite.scala 54:29:@210.4]
  assign io_ctrl_R_bits_rdata = axi_ctrl_io_ctrl_R_bits_rdata; // @[PipelineWithAxiLite.scala 54:29:@209.4]
  assign io_ctrl_R_bits_rresp = 2'h0; // @[PipelineWithAxiLite.scala 54:29:@208.4]
  assign io_out_data = p1_data; // @[PipelineWithAxiLite.scala 69:15:@244.4]
  assign io_out_valid = p1_valid; // @[PipelineWithAxiLite.scala 70:16:@245.4]
  assign axi_ctrl_clock = clock; // @[:@204.4]
  assign axi_ctrl_reset = reset; // @[:@205.4]
  assign axi_ctrl_io_ctrl_AW_valid = io_ctrl_AW_valid; // @[PipelineWithAxiLite.scala 54:29:@223.4]
  assign axi_ctrl_io_ctrl_AW_bits = io_ctrl_AW_bits; // @[PipelineWithAxiLite.scala 54:29:@222.4]
  assign axi_ctrl_io_ctrl_W_valid = io_ctrl_W_valid; // @[PipelineWithAxiLite.scala 54:29:@220.4]
  assign axi_ctrl_io_ctrl_W_bits_wdata = io_ctrl_W_bits_wdata; // @[PipelineWithAxiLite.scala 54:29:@219.4]
  assign axi_ctrl_io_ctrl_W_bits_wstrb = io_ctrl_W_bits_wstrb; // @[PipelineWithAxiLite.scala 54:29:@218.4]
  assign axi_ctrl_io_ctrl_B_ready = io_ctrl_B_ready; // @[PipelineWithAxiLite.scala 54:29:@217.4]
  assign axi_ctrl_io_ctrl_AR_valid = io_ctrl_AR_valid; // @[PipelineWithAxiLite.scala 54:29:@213.4]
  assign axi_ctrl_io_ctrl_AR_bits = io_ctrl_AR_bits; // @[PipelineWithAxiLite.scala 54:29:@212.4]
  assign axi_ctrl_io_ctrl_R_ready = io_ctrl_R_ready; // @[PipelineWithAxiLite.scala 54:29:@211.4]
  assign axi_ctrl_io_stats_nr_samp = nr_samples; // @[PipelineWithAxiLite.scala 56:29:@226.4]
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
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  nr_samples = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  p0_data = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  p0_valid = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  p1_data = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  p1_valid = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      nr_samples <= 32'h0;
    end else begin
      nr_samples <= _GEN_0[31:0];
    end
    p0_data <= io_in_data;
    p0_valid <= io_in_valid;
    p1_data <= _T_70[15:0];
    p1_valid <= p0_valid;
  end
endmodule
