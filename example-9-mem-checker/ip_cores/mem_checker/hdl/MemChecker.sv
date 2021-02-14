module MemCheckerAxiSlave(
  input         clock,
  input         reset,
  output        io_ctrl_AW_ready,
  input         io_ctrl_AW_valid,
  input  [7:0]  io_ctrl_AW_bits_addr,
  output        io_ctrl_W_ready,
  input         io_ctrl_W_valid,
  input  [31:0] io_ctrl_W_bits_wdata,
  input         io_ctrl_B_ready,
  output        io_ctrl_B_valid,
  output        io_ctrl_AR_ready,
  input         io_ctrl_AR_valid,
  input  [7:0]  io_ctrl_AR_bits_addr,
  input         io_ctrl_R_ready,
  output        io_ctrl_R_valid,
  output [31:0] io_ctrl_R_bits_rdata,
  output        io_ctrl_dir,
  output [2:0]  io_ctrl_mode,
  output [63:0] io_read_addr,
  output [31:0] io_read_len,
  output        io_read_start,
  input  [31:0] io_rd_stats_resp_cntr,
  input         io_rd_stats_done,
  input  [31:0] io_rd_stats_duration,
  output [63:0] io_write_addr,
  output [31:0] io_write_len,
  output        io_write_start,
  input  [31:0] io_wr_stats_resp_cntr,
  input         io_wr_stats_done,
  input  [31:0] io_wr_stats_duration,
  input  [31:0] io_check_tot,
  input  [31:0] io_check_ok
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [63:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [63:0] _RAND_10;
  reg [31:0] _RAND_11;
  reg [31:0] _RAND_12;
  reg [31:0] _RAND_13;
  reg [31:0] _RAND_14;
  reg [31:0] _RAND_15;
  reg [31:0] _RAND_16;
  reg [31:0] _RAND_17;
  reg [31:0] _RAND_18;
  reg [31:0] _RAND_19;
  reg [31:0] _RAND_20;
  reg [31:0] _RAND_21;
  reg [31:0] _RAND_22;
  reg [63:0] _RAND_23;
`endif // RANDOMIZE_REG_INIT
  reg  reg_ctrl_dir; // @[MemCheckerAxiSlave.scala 79:29]
  reg [2:0] reg_ctrl_mode; // @[MemCheckerAxiSlave.scala 80:30]
  reg [31:0] reg_read_status; // @[MemCheckerAxiSlave.scala 81:32]
  reg  reg_read_start; // @[MemCheckerAxiSlave.scala 83:31]
  reg [63:0] reg_read_addr; // @[MemCheckerAxiSlave.scala 84:30]
  reg [31:0] reg_read_len; // @[MemCheckerAxiSlave.scala 85:29]
  reg [31:0] reg_read_resp_cntr; // @[MemCheckerAxiSlave.scala 86:35]
  reg [31:0] reg_read_duration; // @[MemCheckerAxiSlave.scala 87:34]
  reg [31:0] reg_write_status; // @[MemCheckerAxiSlave.scala 88:33]
  reg  reg_write_start; // @[MemCheckerAxiSlave.scala 90:32]
  reg [63:0] reg_write_addr; // @[MemCheckerAxiSlave.scala 91:31]
  reg [31:0] reg_write_len; // @[MemCheckerAxiSlave.scala 92:30]
  reg [31:0] reg_write_resp_cntr; // @[MemCheckerAxiSlave.scala 93:36]
  reg [31:0] reg_write_duration; // @[MemCheckerAxiSlave.scala 94:35]
  reg [31:0] reg_check_tot; // @[MemCheckerAxiSlave.scala 95:30]
  reg [31:0] reg_check_ok; // @[MemCheckerAxiSlave.scala 96:29]
  reg  wr_en; // @[MemCheckerAxiSlave.scala 122:18]
  reg [5:0] wr_addr; // @[MemCheckerAxiSlave.scala 123:20]
  wire  _T_20 = 6'h4 == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_23 = 6'h8 == wr_addr; // @[Conditional.scala 37:30]
  reg [31:0] wr_data; // @[MemCheckerAxiSlave.scala 124:20]
  wire [31:0] _GEN_79 = _T_23 ? wr_data : 32'h0; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 231:52 MemCheckerAxiSlave.scala 222:25]
  wire [31:0] _GEN_89 = _T_20 ? 32'h0 : _GEN_79; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 222:25]
  wire [31:0] reg_read_status_clear = wr_en ? _GEN_89 : 32'h0; // @[MemCheckerAxiSlave.scala 225:15 MemCheckerAxiSlave.scala 222:25]
  wire [31:0] _T = ~reg_read_status_clear; // @[MemCheckerAxiSlave.scala 107:42]
  wire [31:0] _T_1 = reg_read_status & _T; // @[MemCheckerAxiSlave.scala 107:39]
  wire [31:0] _T_2 = {31'h0,io_rd_stats_done}; // @[Cat.scala 30:58]
  wire [31:0] _T_3 = _T_1 | _T_2; // @[MemCheckerAxiSlave.scala 107:76]
  wire  _T_24 = 6'h9 == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_26 = 6'ha == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_28 = 6'hb == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_30 = 6'hc == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_31 = 6'h18 == wr_addr; // @[Conditional.scala 37:30]
  wire [31:0] _GEN_51 = _T_31 ? wr_data : 32'h0; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 236:54 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_56 = _T_30 ? 32'h0 : _GEN_51; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_62 = _T_28 ? 32'h0 : _GEN_56; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_68 = _T_26 ? 32'h0 : _GEN_62; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_75 = _T_24 ? 32'h0 : _GEN_68; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_83 = _T_23 ? 32'h0 : _GEN_75; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _GEN_93 = _T_20 ? 32'h0 : _GEN_83; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] reg_write_status_clear = wr_en ? _GEN_93 : 32'h0; // @[MemCheckerAxiSlave.scala 225:15 MemCheckerAxiSlave.scala 223:26]
  wire [31:0] _T_4 = ~reg_write_status_clear; // @[MemCheckerAxiSlave.scala 111:44]
  wire [31:0] _T_5 = reg_write_status & _T_4; // @[MemCheckerAxiSlave.scala 111:41]
  wire [31:0] _T_6 = {31'h0,io_wr_stats_done}; // @[Cat.scala 30:58]
  wire [31:0] _T_7 = _T_5 | _T_6; // @[MemCheckerAxiSlave.scala 111:79]
  reg [1:0] state_wr; // @[MemCheckerAxiSlave.scala 120:25]
  wire  _T_8 = 2'h0 == state_wr; // @[Conditional.scala 37:30]
  wire  _T_9 = io_ctrl_AW_valid & io_ctrl_W_valid; // @[MemCheckerAxiSlave.scala 133:29]
  wire [31:0] _GEN_0 = io_ctrl_W_valid ? io_ctrl_W_bits_wdata : wr_data; // @[MemCheckerAxiSlave.scala 143:36 MemCheckerAxiSlave.scala 144:19 MemCheckerAxiSlave.scala 124:20]
  wire [1:0] _GEN_2 = io_ctrl_W_valid ? 2'h2 : state_wr; // @[MemCheckerAxiSlave.scala 143:36 MemCheckerAxiSlave.scala 146:20 MemCheckerAxiSlave.scala 120:25]
  wire [5:0] _GEN_3 = io_ctrl_AW_valid ? io_ctrl_AW_bits_addr[7:2] : wr_addr; // @[MemCheckerAxiSlave.scala 139:36 MemCheckerAxiSlave.scala 140:19 MemCheckerAxiSlave.scala 123:20]
  wire  _T_12 = 2'h1 == state_wr; // @[Conditional.scala 37:30]
  wire  _T_13 = 2'h2 == state_wr; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_16 = io_ctrl_AW_valid ? 2'h3 : state_wr; // @[MemCheckerAxiSlave.scala 158:30 MemCheckerAxiSlave.scala 161:18 MemCheckerAxiSlave.scala 120:25]
  wire  _T_15 = 2'h3 == state_wr; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_17 = io_ctrl_B_ready ? 2'h0 : state_wr; // @[MemCheckerAxiSlave.scala 165:29 MemCheckerAxiSlave.scala 166:18 MemCheckerAxiSlave.scala 120:25]
  wire [1:0] _GEN_18 = _T_15 ? _GEN_17 : state_wr; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 120:25]
  wire  _GEN_19 = _T_13 & io_ctrl_AW_valid; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 129:9]
  wire  _GEN_36 = _T_12 ? 1'h0 : _T_15; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 191:23]
  wire  _GEN_38 = _T_13 ? 1'h0 : _T_12; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 185:23]
  wire  _GEN_39 = _T_13 ? 1'h0 : _GEN_36; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 186:23]
  wire [31:0] hi = reg_read_addr[63:32]; // @[MemCheckerAxiSlave.scala 233:65]
  wire [63:0] _T_27 = {hi,wr_data}; // @[Cat.scala 30:58]
  wire [31:0] lo = reg_read_addr[31:0]; // @[MemCheckerAxiSlave.scala 234:74]
  wire [63:0] _T_29 = {wr_data,lo}; // @[Cat.scala 30:58]
  wire  _T_32 = 6'h19 == wr_addr; // @[Conditional.scala 37:30]
  wire  _T_34 = 6'h1a == wr_addr; // @[Conditional.scala 37:30]
  wire [31:0] hi_1 = reg_write_addr[63:32]; // @[MemCheckerAxiSlave.scala 238:68]
  wire [63:0] _T_35 = {hi_1,wr_data}; // @[Cat.scala 30:58]
  wire  _T_36 = 6'h1b == wr_addr; // @[Conditional.scala 37:30]
  wire [31:0] lo_1 = reg_write_addr[31:0]; // @[MemCheckerAxiSlave.scala 239:77]
  wire [63:0] _T_37 = {wr_data,lo_1}; // @[Cat.scala 30:58]
  wire  _T_38 = 6'h1c == wr_addr; // @[Conditional.scala 37:30]
  wire [31:0] _GEN_43 = _T_38 ? wr_data : reg_write_len; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 240:42 MemCheckerAxiSlave.scala 92:30]
  wire [63:0] _GEN_44 = _T_36 ? _T_37 : reg_write_addr; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 239:47 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_45 = _T_36 ? reg_write_len : _GEN_43; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire [63:0] _GEN_46 = _T_34 ? _T_35 : _GEN_44; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 238:47]
  wire [31:0] _GEN_47 = _T_34 ? reg_write_len : _GEN_45; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire  _GEN_48 = _T_32 & wr_data[0]; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 237:45 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_49 = _T_32 ? reg_write_addr : _GEN_46; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_50 = _T_32 ? reg_write_len : _GEN_47; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire  _GEN_52 = _T_31 ? 1'h0 : _GEN_48; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_53 = _T_31 ? reg_write_addr : _GEN_49; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_54 = _T_31 ? reg_write_len : _GEN_50; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire [31:0] _GEN_55 = _T_30 ? wr_data : reg_read_len; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 235:40 MemCheckerAxiSlave.scala 85:29]
  wire  _GEN_57 = _T_30 ? 1'h0 : _GEN_52; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_58 = _T_30 ? reg_write_addr : _GEN_53; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_59 = _T_30 ? reg_write_len : _GEN_54; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire [63:0] _GEN_60 = _T_28 ? _T_29 : reg_read_addr; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 234:45 MemCheckerAxiSlave.scala 84:30]
  wire [31:0] _GEN_61 = _T_28 ? reg_read_len : _GEN_55; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 85:29]
  wire  _GEN_63 = _T_28 ? 1'h0 : _GEN_57; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_64 = _T_28 ? reg_write_addr : _GEN_58; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_65 = _T_28 ? reg_write_len : _GEN_59; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire [63:0] _GEN_66 = _T_26 ? _T_27 : _GEN_60; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 233:45]
  wire [31:0] _GEN_67 = _T_26 ? reg_read_len : _GEN_61; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 85:29]
  wire  _GEN_69 = _T_26 ? 1'h0 : _GEN_63; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_70 = _T_26 ? reg_write_addr : _GEN_64; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_71 = _T_26 ? reg_write_len : _GEN_65; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire  _GEN_72 = _T_24 & wr_data[0]; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 232:43 MemCheckerAxiSlave.scala 220:18]
  wire [63:0] _GEN_73 = _T_24 ? reg_read_addr : _GEN_66; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 84:30]
  wire [31:0] _GEN_74 = _T_24 ? reg_read_len : _GEN_67; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 85:29]
  wire  _GEN_76 = _T_24 ? 1'h0 : _GEN_69; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire [63:0] _GEN_77 = _T_24 ? reg_write_addr : _GEN_70; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 91:31]
  wire [31:0] _GEN_78 = _T_24 ? reg_write_len : _GEN_71; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 92:30]
  wire  _GEN_80 = _T_23 ? 1'h0 : _GEN_72; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 220:18]
  wire  _GEN_84 = _T_23 ? 1'h0 : _GEN_76; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 221:19]
  wire  _GEN_90 = _T_20 ? 1'h0 : _GEN_80; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 220:18]
  wire  _GEN_94 = _T_20 ? 1'h0 : _GEN_84; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 221:19]
  wire  _GEN_100 = wr_en & _GEN_90; // @[MemCheckerAxiSlave.scala 225:15 MemCheckerAxiSlave.scala 220:18]
  wire  _GEN_104 = wr_en & _GEN_94; // @[MemCheckerAxiSlave.scala 225:15 MemCheckerAxiSlave.scala 221:19]
  reg [1:0] state_rd; // @[MemCheckerAxiSlave.scala 248:25]
  reg  rd_en; // @[MemCheckerAxiSlave.scala 250:18]
  reg [5:0] rd_addr; // @[MemCheckerAxiSlave.scala 251:20]
  reg [33:0] rd_data; // @[MemCheckerAxiSlave.scala 252:20]
  wire  _T_39 = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_41 = 2'h1 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_42 = 2'h2 == state_rd; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_110 = io_ctrl_R_ready ? 2'h0 : state_rd; // @[MemCheckerAxiSlave.scala 269:29 MemCheckerAxiSlave.scala 270:18 MemCheckerAxiSlave.scala 248:25]
  wire [33:0] _GEN_118 = _T_42 ? rd_data : 34'h0; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 293:28 MemCheckerAxiSlave.scala 277:24]
  wire  _GEN_120 = _T_41 ? 1'h0 : _T_42; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 287:23]
  wire [33:0] _GEN_121 = _T_41 ? 34'h0 : _GEN_118; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 288:28]
  wire [33:0] _GEN_124 = _T_39 ? 34'h0 : _GEN_121; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 283:28]
  wire  _T_46 = 6'h0 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_47 = 6'h1 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_48 = 6'h4 == rd_addr; // @[Conditional.scala 37:30]
  wire [10:0] _T_49 = {reg_ctrl_mode,7'h0,reg_ctrl_dir}; // @[Cat.scala 30:58]
  wire  _T_50 = 6'h8 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_51 = 6'h9 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_52 = 6'ha == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_54 = 6'hb == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_56 = 6'hc == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_57 = 6'hd == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_58 = 6'he == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_59 = 6'h18 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_60 = 6'h19 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_61 = 6'h1a == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_63 = 6'h1b == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_65 = 6'h1c == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_66 = 6'h1d == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_67 = 6'h1e == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_68 = 6'h28 == rd_addr; // @[Conditional.scala 37:30]
  wire  _T_69 = 6'h29 == rd_addr; // @[Conditional.scala 37:30]
  wire [33:0] _GEN_125 = _T_69 ? {{2'd0}, reg_check_ok} : rd_data; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 318:35 MemCheckerAxiSlave.scala 252:20]
  wire [33:0] _GEN_126 = _T_68 ? {{2'd0}, reg_check_tot} : _GEN_125; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 317:36]
  wire [33:0] _GEN_127 = _T_67 ? {{2'd0}, reg_write_duration} : _GEN_126; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 316:41]
  wire [33:0] _GEN_128 = _T_66 ? {{2'd0}, reg_write_resp_cntr} : _GEN_127; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 315:42]
  wire [33:0] _GEN_129 = _T_65 ? {{2'd0}, reg_write_len} : _GEN_128; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 314:36]
  wire [33:0] _GEN_130 = _T_63 ? {{2'd0}, hi_1} : _GEN_129; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 313:40]
  wire [33:0] _GEN_131 = _T_61 ? {{2'd0}, lo_1} : _GEN_130; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 312:40]
  wire [33:0] _GEN_132 = _T_60 ? 34'h0 : _GEN_131; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 311:37]
  wire [33:0] _GEN_133 = _T_59 ? {{2'd0}, reg_write_status} : _GEN_132; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 310:39]
  wire [33:0] _GEN_134 = _T_58 ? {{2'd0}, reg_read_duration} : _GEN_133; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 309:40]
  wire [33:0] _GEN_135 = _T_57 ? {{2'd0}, reg_read_resp_cntr} : _GEN_134; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 308:41]
  wire [33:0] _GEN_136 = _T_56 ? {{2'd0}, reg_read_len} : _GEN_135; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 307:35]
  wire [33:0] _GEN_137 = _T_54 ? {{2'd0}, hi} : _GEN_136; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 306:39]
  wire [33:0] _GEN_138 = _T_52 ? {{2'd0}, lo} : _GEN_137; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 305:39]
  wire [33:0] _GEN_139 = _T_51 ? 34'h0 : _GEN_138; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 304:36]
  wire [33:0] _GEN_140 = _T_50 ? {{2'd0}, reg_read_status} : _GEN_139; // @[Conditional.scala 39:67 MemCheckerAxiSlave.scala 303:38]
  assign io_ctrl_AW_ready = _T_8 | _T_13; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 179:24]
  assign io_ctrl_W_ready = _T_8 | _GEN_38; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 180:23]
  assign io_ctrl_B_valid = _T_8 ? 1'h0 : _GEN_39; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 181:23]
  assign io_ctrl_AR_ready = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  assign io_ctrl_R_valid = _T_39 ? 1'h0 : _GEN_120; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 282:23]
  assign io_ctrl_R_bits_rdata = _GEN_124[31:0];
  assign io_ctrl_dir = reg_ctrl_dir; // @[MemCheckerAxiSlave.scala 98:15]
  assign io_ctrl_mode = reg_ctrl_mode; // @[MemCheckerAxiSlave.scala 99:16]
  assign io_read_addr = reg_read_addr; // @[MemCheckerAxiSlave.scala 100:16]
  assign io_read_len = reg_read_len; // @[MemCheckerAxiSlave.scala 101:15]
  assign io_read_start = reg_read_start; // @[MemCheckerAxiSlave.scala 102:17]
  assign io_write_addr = reg_write_addr; // @[MemCheckerAxiSlave.scala 103:17]
  assign io_write_len = reg_write_len; // @[MemCheckerAxiSlave.scala 104:16]
  assign io_write_start = reg_write_start; // @[MemCheckerAxiSlave.scala 105:18]
  always @(posedge clock) begin
    if (reset) begin // @[MemCheckerAxiSlave.scala 79:29]
      reg_ctrl_dir <= 1'h0; // @[MemCheckerAxiSlave.scala 79:29]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (_T_20) begin // @[Conditional.scala 40:58]
        reg_ctrl_dir <= wr_data[0]; // @[MemCheckerAxiSlave.scala 228:22]
      end
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 80:30]
      reg_ctrl_mode <= 3'h0; // @[MemCheckerAxiSlave.scala 80:30]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (_T_20) begin // @[Conditional.scala 40:58]
        reg_ctrl_mode <= wr_data[10:8]; // @[MemCheckerAxiSlave.scala 229:23]
      end
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 81:32]
      reg_read_status <= 32'h0; // @[MemCheckerAxiSlave.scala 81:32]
    end else begin
      reg_read_status <= _T_3; // @[MemCheckerAxiSlave.scala 107:19]
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 83:31]
      reg_read_start <= 1'h0; // @[MemCheckerAxiSlave.scala 83:31]
    end else begin
      reg_read_start <= _GEN_100;
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 84:30]
      reg_read_addr <= 64'h0; // @[MemCheckerAxiSlave.scala 84:30]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (!(_T_20)) begin // @[Conditional.scala 40:58]
        if (!(_T_23)) begin // @[Conditional.scala 39:67]
          reg_read_addr <= _GEN_73;
        end
      end
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 85:29]
      reg_read_len <= 32'h0; // @[MemCheckerAxiSlave.scala 85:29]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (!(_T_20)) begin // @[Conditional.scala 40:58]
        if (!(_T_23)) begin // @[Conditional.scala 39:67]
          reg_read_len <= _GEN_74;
        end
      end
    end
    reg_read_resp_cntr <= io_rd_stats_resp_cntr; // @[MemCheckerAxiSlave.scala 86:35]
    reg_read_duration <= io_rd_stats_duration; // @[MemCheckerAxiSlave.scala 87:34]
    if (reset) begin // @[MemCheckerAxiSlave.scala 88:33]
      reg_write_status <= 32'h0; // @[MemCheckerAxiSlave.scala 88:33]
    end else begin
      reg_write_status <= _T_7; // @[MemCheckerAxiSlave.scala 111:20]
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 90:32]
      reg_write_start <= 1'h0; // @[MemCheckerAxiSlave.scala 90:32]
    end else begin
      reg_write_start <= _GEN_104;
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 91:31]
      reg_write_addr <= 64'h0; // @[MemCheckerAxiSlave.scala 91:31]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (!(_T_20)) begin // @[Conditional.scala 40:58]
        if (!(_T_23)) begin // @[Conditional.scala 39:67]
          reg_write_addr <= _GEN_77;
        end
      end
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 92:30]
      reg_write_len <= 32'h0; // @[MemCheckerAxiSlave.scala 92:30]
    end else if (wr_en) begin // @[MemCheckerAxiSlave.scala 225:15]
      if (!(_T_20)) begin // @[Conditional.scala 40:58]
        if (!(_T_23)) begin // @[Conditional.scala 39:67]
          reg_write_len <= _GEN_78;
        end
      end
    end
    reg_write_resp_cntr <= io_wr_stats_resp_cntr; // @[MemCheckerAxiSlave.scala 93:36]
    reg_write_duration <= io_wr_stats_duration; // @[MemCheckerAxiSlave.scala 94:35]
    reg_check_tot <= io_check_tot; // @[MemCheckerAxiSlave.scala 95:30]
    reg_check_ok <= io_check_ok; // @[MemCheckerAxiSlave.scala 96:29]
    if (_T_8) begin // @[Conditional.scala 40:58]
      wr_en <= _T_9;
    end else if (_T_12) begin // @[Conditional.scala 39:67]
      wr_en <= io_ctrl_W_valid;
    end else begin
      wr_en <= _GEN_19;
    end
    if (_T_8) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[MemCheckerAxiSlave.scala 133:49]
        wr_addr <= io_ctrl_AW_bits_addr[7:2]; // @[MemCheckerAxiSlave.scala 135:17]
      end else begin
        wr_addr <= _GEN_3;
      end
    end else if (!(_T_12)) begin // @[Conditional.scala 39:67]
      if (_T_13) begin // @[Conditional.scala 39:67]
        wr_addr <= _GEN_3;
      end
    end
    if (_T_8) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[MemCheckerAxiSlave.scala 133:49]
        wr_data <= io_ctrl_W_bits_wdata; // @[MemCheckerAxiSlave.scala 136:17]
      end else if (!(io_ctrl_AW_valid)) begin // @[MemCheckerAxiSlave.scala 139:36]
        wr_data <= _GEN_0;
      end
    end else if (_T_12) begin // @[Conditional.scala 39:67]
      wr_data <= _GEN_0;
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 120:25]
      state_wr <= 2'h0; // @[MemCheckerAxiSlave.scala 120:25]
    end else if (_T_8) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[MemCheckerAxiSlave.scala 133:49]
        state_wr <= 2'h3; // @[MemCheckerAxiSlave.scala 138:18]
      end else if (io_ctrl_AW_valid) begin // @[MemCheckerAxiSlave.scala 139:36]
        state_wr <= 2'h1; // @[MemCheckerAxiSlave.scala 141:20]
      end else begin
        state_wr <= _GEN_2;
      end
    end else if (_T_12) begin // @[Conditional.scala 39:67]
      if (io_ctrl_W_valid) begin // @[MemCheckerAxiSlave.scala 150:29]
        state_wr <= 2'h3; // @[MemCheckerAxiSlave.scala 154:18]
      end
    end else if (_T_13) begin // @[Conditional.scala 39:67]
      state_wr <= _GEN_16;
    end else begin
      state_wr <= _GEN_18;
    end
    if (reset) begin // @[MemCheckerAxiSlave.scala 248:25]
      state_rd <= 2'h0; // @[MemCheckerAxiSlave.scala 248:25]
    end else if (_T_39) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[MemCheckerAxiSlave.scala 259:30]
        state_rd <= 2'h1; // @[MemCheckerAxiSlave.scala 262:18]
      end
    end else if (_T_41) begin // @[Conditional.scala 39:67]
      state_rd <= 2'h2; // @[MemCheckerAxiSlave.scala 266:16]
    end else if (_T_42) begin // @[Conditional.scala 39:67]
      state_rd <= _GEN_110;
    end
    rd_en <= _T_39 & io_ctrl_AR_valid; // @[Conditional.scala 40:58 MemCheckerAxiSlave.scala 255:9]
    if (_T_39) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[MemCheckerAxiSlave.scala 259:30]
        rd_addr <= io_ctrl_AR_bits_addr[7:2]; // @[MemCheckerAxiSlave.scala 261:17]
      end
    end
    if (rd_en) begin // @[MemCheckerAxiSlave.scala 298:15]
      if (_T_46) begin // @[Conditional.scala 40:58]
        rd_data <= 34'h3e3c8ec8; // @[MemCheckerAxiSlave.scala 300:29]
      end else if (_T_47) begin // @[Conditional.scala 39:67]
        rd_data <= 34'h10000; // @[MemCheckerAxiSlave.scala 301:34]
      end else if (_T_48) begin // @[Conditional.scala 39:67]
        rd_data <= {{23'd0}, _T_49}; // @[MemCheckerAxiSlave.scala 302:31]
      end else begin
        rd_data <= _GEN_140;
      end
    end
  end
// Register and memory initialization
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
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
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
  reg_ctrl_dir = _RAND_0[0:0];
  _RAND_1 = {1{`RANDOM}};
  reg_ctrl_mode = _RAND_1[2:0];
  _RAND_2 = {1{`RANDOM}};
  reg_read_status = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  reg_read_start = _RAND_3[0:0];
  _RAND_4 = {2{`RANDOM}};
  reg_read_addr = _RAND_4[63:0];
  _RAND_5 = {1{`RANDOM}};
  reg_read_len = _RAND_5[31:0];
  _RAND_6 = {1{`RANDOM}};
  reg_read_resp_cntr = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  reg_read_duration = _RAND_7[31:0];
  _RAND_8 = {1{`RANDOM}};
  reg_write_status = _RAND_8[31:0];
  _RAND_9 = {1{`RANDOM}};
  reg_write_start = _RAND_9[0:0];
  _RAND_10 = {2{`RANDOM}};
  reg_write_addr = _RAND_10[63:0];
  _RAND_11 = {1{`RANDOM}};
  reg_write_len = _RAND_11[31:0];
  _RAND_12 = {1{`RANDOM}};
  reg_write_resp_cntr = _RAND_12[31:0];
  _RAND_13 = {1{`RANDOM}};
  reg_write_duration = _RAND_13[31:0];
  _RAND_14 = {1{`RANDOM}};
  reg_check_tot = _RAND_14[31:0];
  _RAND_15 = {1{`RANDOM}};
  reg_check_ok = _RAND_15[31:0];
  _RAND_16 = {1{`RANDOM}};
  wr_en = _RAND_16[0:0];
  _RAND_17 = {1{`RANDOM}};
  wr_addr = _RAND_17[5:0];
  _RAND_18 = {1{`RANDOM}};
  wr_data = _RAND_18[31:0];
  _RAND_19 = {1{`RANDOM}};
  state_wr = _RAND_19[1:0];
  _RAND_20 = {1{`RANDOM}};
  state_rd = _RAND_20[1:0];
  _RAND_21 = {1{`RANDOM}};
  rd_en = _RAND_21[0:0];
  _RAND_22 = {1{`RANDOM}};
  rd_addr = _RAND_22[5:0];
  _RAND_23 = {2{`RANDOM}};
  rd_data = _RAND_23[33:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module AvalonMMWriter(
  input          clock,
  input          reset,
  output [47:0]  io_address,
  input  [1:0]   io_response,
  output         io_write,
  output [127:0] io_writedata,
  input          io_waitrequest,
  input          io_writeresponsevalid,
  output [4:0]   io_burstcount,
  input  [47:0]  io_ctrl_addr,
  input  [31:0]  io_ctrl_len_bytes,
  input          io_ctrl_start,
  output         io_data_init,
  output         io_data_inc,
  input  [127:0] io_data_data,
  output [31:0]  io_stats_resp_cntr,
  output         io_stats_done,
  output [31:0]  io_stats_duration
);
`ifdef RANDOMIZE_REG_INIT
  reg [63:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
`endif // RANDOMIZE_REG_INIT
  reg [47:0] cur_addr; // @[AvalonMMWriter.scala 64:21]
  reg [31:0] rem_tot_len_bytes; // @[AvalonMMWriter.scala 65:30]
  reg [4:0] rem_cyc_len_words; // @[AvalonMMWriter.scala 66:30]
  reg [1:0] state; // @[AvalonMMWriter.scala 69:22]
  wire  _T = 2'h0 == state; // @[Conditional.scala 37:30]
  wire [31:0] _T_2 = io_ctrl_len_bytes - 32'h10; // @[AvalonMMWriter.scala 75:48]
  wire [31:0] _T_4 = io_ctrl_len_bytes / 32'h10; // @[AvalonMMWriter.scala 79:51]
  wire [31:0] _T_6 = _T_4 - 32'h1; // @[AvalonMMWriter.scala 79:70]
  wire [31:0] _GEN_0 = io_ctrl_len_bytes > 32'h100 ? 32'hf : _T_6; // @[AvalonMMWriter.scala 76:65 AvalonMMWriter.scala 77:29 AvalonMMWriter.scala 79:29]
  wire [31:0] _GEN_3 = io_ctrl_start ? _GEN_0 : {{27'd0}, rem_cyc_len_words}; // @[AvalonMMWriter.scala 73:27 AvalonMMWriter.scala 66:30]
  wire  _T_7 = 2'h1 == state; // @[Conditional.scala 37:30]
  wire  _T_8 = ~io_waitrequest; // @[AvalonMMWriter.scala 85:12]
  wire [4:0] _T_10 = rem_cyc_len_words - 5'h1; // @[AvalonMMWriter.scala 86:48]
  wire [31:0] _T_12 = rem_tot_len_bytes - 32'h10; // @[AvalonMMWriter.scala 87:48]
  wire  _T_13 = rem_cyc_len_words == 5'h0; // @[AvalonMMWriter.scala 88:32]
  wire [1:0] _GEN_5 = rem_cyc_len_words == 5'h0 ? 2'h0 : 2'h2; // @[AvalonMMWriter.scala 88:41 AvalonMMWriter.scala 89:17 AvalonMMWriter.scala 91:17]
  wire [4:0] _GEN_6 = ~io_waitrequest ? _T_10 : rem_cyc_len_words; // @[AvalonMMWriter.scala 85:29 AvalonMMWriter.scala 86:27 AvalonMMWriter.scala 66:30]
  wire [31:0] _GEN_7 = ~io_waitrequest ? _T_12 : rem_tot_len_bytes; // @[AvalonMMWriter.scala 85:29 AvalonMMWriter.scala 87:27 AvalonMMWriter.scala 65:30]
  wire  _T_14 = 2'h2 == state; // @[Conditional.scala 37:30]
  wire [47:0] _T_23 = cur_addr + 48'h100; // @[AvalonMMWriter.scala 104:34]
  wire [31:0] _T_25 = rem_tot_len_bytes / 32'h10; // @[AvalonMMWriter.scala 108:55]
  wire [31:0] _T_27 = _T_25 - 32'h1; // @[AvalonMMWriter.scala 108:74]
  wire [31:0] _GEN_9 = rem_tot_len_bytes >= 32'h100 ? 32'hf : _T_27; // @[AvalonMMWriter.scala 105:70 AvalonMMWriter.scala 106:33 AvalonMMWriter.scala 108:33]
  wire [1:0] _GEN_10 = rem_tot_len_bytes == 32'h0 ? 2'h0 : 2'h1; // @[AvalonMMWriter.scala 100:43 AvalonMMWriter.scala 101:19 AvalonMMWriter.scala 103:19]
  wire [47:0] _GEN_11 = rem_tot_len_bytes == 32'h0 ? cur_addr : _T_23; // @[AvalonMMWriter.scala 100:43 AvalonMMWriter.scala 64:21 AvalonMMWriter.scala 104:22]
  wire [31:0] _GEN_12 = rem_tot_len_bytes == 32'h0 ? {{27'd0}, _T_10} : _GEN_9; // @[AvalonMMWriter.scala 100:43 AvalonMMWriter.scala 97:27]
  wire [1:0] _GEN_13 = _T_13 ? _GEN_10 : state; // @[AvalonMMWriter.scala 99:41 AvalonMMWriter.scala 69:22]
  wire [47:0] _GEN_14 = _T_13 ? _GEN_11 : cur_addr; // @[AvalonMMWriter.scala 99:41 AvalonMMWriter.scala 64:21]
  wire [31:0] _GEN_15 = _T_13 ? _GEN_12 : {{27'd0}, _T_10}; // @[AvalonMMWriter.scala 99:41 AvalonMMWriter.scala 97:27]
  wire [31:0] _GEN_16 = _T_8 ? _GEN_15 : {{27'd0}, rem_cyc_len_words}; // @[AvalonMMWriter.scala 96:29 AvalonMMWriter.scala 66:30]
  wire [1:0] _GEN_18 = _T_8 ? _GEN_13 : state; // @[AvalonMMWriter.scala 96:29 AvalonMMWriter.scala 69:22]
  wire [31:0] _GEN_20 = _T_14 ? _GEN_16 : {{27'd0}, rem_cyc_len_words}; // @[Conditional.scala 39:67 AvalonMMWriter.scala 66:30]
  wire [31:0] _GEN_24 = _T_7 ? {{27'd0}, _GEN_6} : _GEN_20; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_30 = _T ? _GEN_3 : _GEN_24; // @[Conditional.scala 40:58]
  wire  _T_29 = state == 2'h1; // @[AvalonMMWriter.scala 118:15]
  wire [4:0] _T_31 = rem_cyc_len_words + 5'h1; // @[AvalonMMWriter.scala 119:40]
  wire  _T_33 = state != 2'h0; // @[AvalonMMWriter.scala 130:21]
  wire  _T_34 = state == 2'h0; // @[AvalonMMWriter.scala 133:25]
  wire  _T_35 = state == 2'h0 & io_ctrl_start; // @[AvalonMMWriter.scala 133:35]
  reg [31:0] resp_cntr_reg; // @[AvalonMMWriter.scala 140:26]
  wire [31:0] _T_45 = resp_cntr_reg + 32'h1; // @[AvalonMMWriter.scala 147:38]
  reg [1:0] state_prev; // @[AvalonMMWriter.scala 154:27]
  reg [31:0] wr_duration; // @[AvalonMMWriter.scala 157:28]
  reg  wr_duration_en; // @[AvalonMMWriter.scala 158:31]
  wire  _GEN_37 = io_stats_done ? 1'h0 : wr_duration_en; // @[AvalonMMWriter.scala 163:31 AvalonMMWriter.scala 164:20 AvalonMMWriter.scala 158:31]
  wire  _GEN_38 = _T_35 | _GEN_37; // @[AvalonMMWriter.scala 161:43 AvalonMMWriter.scala 162:20]
  wire [31:0] _T_52 = wr_duration + 32'h1; // @[AvalonMMWriter.scala 168:32]
  assign io_address = _T_29 ? cur_addr : 48'h0; // @[AvalonMMWriter.scala 124:29 AvalonMMWriter.scala 125:16 AvalonMMWriter.scala 127:16]
  assign io_write = state != 2'h0; // @[AvalonMMWriter.scala 130:21]
  assign io_writedata = io_data_data; // @[AvalonMMWriter.scala 135:16]
  assign io_burstcount = state == 2'h1 ? _T_31 : 5'h0; // @[AvalonMMWriter.scala 118:29 AvalonMMWriter.scala 119:19 AvalonMMWriter.scala 121:19]
  assign io_data_init = state == 2'h0 & io_ctrl_start; // @[AvalonMMWriter.scala 133:35]
  assign io_data_inc = _T_33 & _T_8; // @[AvalonMMWriter.scala 134:34]
  assign io_stats_resp_cntr = resp_cntr_reg; // @[AvalonMMWriter.scala 141:22]
  assign io_stats_done = state_prev != 2'h0 & _T_34; // @[AvalonMMWriter.scala 155:41]
  assign io_stats_duration = wr_duration; // @[AvalonMMWriter.scala 159:21]
  always @(posedge clock) begin
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMWriter.scala 73:27]
        cur_addr <= io_ctrl_addr; // @[AvalonMMWriter.scala 74:18]
      end
    end else if (!(_T_7)) begin // @[Conditional.scala 39:67]
      if (_T_14) begin // @[Conditional.scala 39:67]
        if (_T_8) begin // @[AvalonMMWriter.scala 96:29]
          cur_addr <= _GEN_14;
        end
      end
    end
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMWriter.scala 73:27]
        rem_tot_len_bytes <= _T_2; // @[AvalonMMWriter.scala 75:27]
      end
    end else if (_T_7) begin // @[Conditional.scala 39:67]
      rem_tot_len_bytes <= _GEN_7;
    end else if (_T_14) begin // @[Conditional.scala 39:67]
      rem_tot_len_bytes <= _GEN_7;
    end
    rem_cyc_len_words <= _GEN_30[4:0];
    if (reset) begin // @[AvalonMMWriter.scala 69:22]
      state <= 2'h0; // @[AvalonMMWriter.scala 69:22]
    end else if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMWriter.scala 73:27]
        state <= 2'h1; // @[AvalonMMWriter.scala 81:15]
      end
    end else if (_T_7) begin // @[Conditional.scala 39:67]
      if (~io_waitrequest) begin // @[AvalonMMWriter.scala 85:29]
        state <= _GEN_5;
      end
    end else if (_T_14) begin // @[Conditional.scala 39:67]
      state <= _GEN_18;
    end
    if (_T_35) begin // @[AvalonMMWriter.scala 143:43]
      resp_cntr_reg <= 32'h0; // @[AvalonMMWriter.scala 144:19]
    end else if (_T_33) begin // @[AvalonMMWriter.scala 145:33]
      if (io_writeresponsevalid & io_response == 2'h0) begin // @[AvalonMMWriter.scala 146:57]
        resp_cntr_reg <= _T_45; // @[AvalonMMWriter.scala 147:21]
      end
    end
    state_prev <= state; // @[AvalonMMWriter.scala 154:27]
    if (reset) begin // @[AvalonMMWriter.scala 157:28]
      wr_duration <= 32'h0; // @[AvalonMMWriter.scala 157:28]
    end else if (wr_duration_en) begin // @[AvalonMMWriter.scala 167:25]
      wr_duration <= _T_52; // @[AvalonMMWriter.scala 168:17]
    end else if (_T_35) begin // @[AvalonMMWriter.scala 169:49]
      wr_duration <= 32'h0; // @[AvalonMMWriter.scala 170:17]
    end
    if (reset) begin // @[AvalonMMWriter.scala 158:31]
      wr_duration_en <= 1'h0; // @[AvalonMMWriter.scala 158:31]
    end else begin
      wr_duration_en <= _GEN_38;
    end
  end
// Register and memory initialization
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
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
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
  _RAND_0 = {2{`RANDOM}};
  cur_addr = _RAND_0[47:0];
  _RAND_1 = {1{`RANDOM}};
  rem_tot_len_bytes = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  rem_cyc_len_words = _RAND_2[4:0];
  _RAND_3 = {1{`RANDOM}};
  state = _RAND_3[1:0];
  _RAND_4 = {1{`RANDOM}};
  resp_cntr_reg = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  state_prev = _RAND_5[1:0];
  _RAND_6 = {1{`RANDOM}};
  wr_duration = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  wr_duration_en = _RAND_7[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module AvalonMMReader(
  input          clock,
  input          reset,
  output [47:0]  io_address,
  output         io_read,
  input  [127:0] io_readdata,
  input  [1:0]   io_response,
  input          io_waitrequest,
  input          io_readdatavalid,
  output [4:0]   io_burstcount,
  input  [47:0]  io_ctrl_addr,
  input  [31:0]  io_ctrl_len_bytes,
  input          io_ctrl_start,
  output         io_data_init,
  output         io_data_inc,
  output [127:0] io_data_data,
  output [31:0]  io_stats_resp_cntr,
  output         io_stats_done,
  output [31:0]  io_stats_duration
);
`ifdef RANDOMIZE_REG_INIT
  reg [63:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
`endif // RANDOMIZE_REG_INIT
  reg [47:0] cur_addr; // @[AvalonMMReader.scala 63:21]
  reg [31:0] rem_tot_len_bytes; // @[AvalonMMReader.scala 64:30]
  reg [4:0] rem_cyc_len_words; // @[AvalonMMReader.scala 65:30]
  reg [1:0] state; // @[AvalonMMReader.scala 68:22]
  wire  _T = 2'h0 == state; // @[Conditional.scala 37:30]
  wire [31:0] _T_2 = io_ctrl_len_bytes - 32'h10; // @[AvalonMMReader.scala 74:48]
  wire [31:0] _T_4 = io_ctrl_len_bytes / 32'h10; // @[AvalonMMReader.scala 78:51]
  wire [31:0] _T_6 = _T_4 - 32'h1; // @[AvalonMMReader.scala 78:70]
  wire [31:0] _GEN_0 = io_ctrl_len_bytes > 32'h100 ? 32'hf : _T_6; // @[AvalonMMReader.scala 75:65 AvalonMMReader.scala 76:29 AvalonMMReader.scala 78:29]
  wire [31:0] _GEN_3 = io_ctrl_start ? _GEN_0 : {{27'd0}, rem_cyc_len_words}; // @[AvalonMMReader.scala 72:27 AvalonMMReader.scala 65:30]
  wire  _T_7 = 2'h1 == state; // @[Conditional.scala 37:30]
  wire  _T_9 = rem_cyc_len_words == 5'h0; // @[AvalonMMReader.scala 85:32]
  wire [1:0] _GEN_5 = rem_cyc_len_words == 5'h0 ? 2'h0 : 2'h2; // @[AvalonMMReader.scala 85:41 AvalonMMReader.scala 86:17 AvalonMMReader.scala 88:17]
  wire  _T_10 = 2'h2 == state; // @[Conditional.scala 37:30]
  wire [4:0] _T_12 = rem_cyc_len_words - 5'h1; // @[AvalonMMReader.scala 94:48]
  wire [31:0] _T_14 = rem_tot_len_bytes - 32'h10; // @[AvalonMMReader.scala 95:48]
  wire [47:0] _T_18 = cur_addr + 48'h100; // @[AvalonMMReader.scala 101:34]
  wire [31:0] _T_20 = rem_tot_len_bytes / 32'h10; // @[AvalonMMReader.scala 105:55]
  wire [31:0] _T_22 = _T_20 - 32'h1; // @[AvalonMMReader.scala 105:74]
  wire [31:0] _GEN_7 = rem_tot_len_bytes >= 32'h100 ? 32'hf : _T_22; // @[AvalonMMReader.scala 102:70 AvalonMMReader.scala 103:33 AvalonMMReader.scala 105:33]
  wire [1:0] _GEN_8 = rem_tot_len_bytes == 32'h0 ? 2'h0 : 2'h1; // @[AvalonMMReader.scala 97:43 AvalonMMReader.scala 98:19 AvalonMMReader.scala 100:19]
  wire [47:0] _GEN_9 = rem_tot_len_bytes == 32'h0 ? cur_addr : _T_18; // @[AvalonMMReader.scala 97:43 AvalonMMReader.scala 63:21 AvalonMMReader.scala 101:22]
  wire [31:0] _GEN_10 = rem_tot_len_bytes == 32'h0 ? {{27'd0}, _T_12} : _GEN_7; // @[AvalonMMReader.scala 97:43 AvalonMMReader.scala 94:27]
  wire [1:0] _GEN_11 = _T_9 ? _GEN_8 : state; // @[AvalonMMReader.scala 96:41 AvalonMMReader.scala 68:22]
  wire [47:0] _GEN_12 = _T_9 ? _GEN_9 : cur_addr; // @[AvalonMMReader.scala 96:41 AvalonMMReader.scala 63:21]
  wire [31:0] _GEN_13 = _T_9 ? _GEN_10 : {{27'd0}, _T_12}; // @[AvalonMMReader.scala 96:41 AvalonMMReader.scala 94:27]
  wire [31:0] _GEN_14 = io_readdatavalid ? _GEN_13 : {{27'd0}, rem_cyc_len_words}; // @[AvalonMMReader.scala 93:30 AvalonMMReader.scala 65:30]
  wire [1:0] _GEN_16 = io_readdatavalid ? _GEN_11 : state; // @[AvalonMMReader.scala 93:30 AvalonMMReader.scala 68:22]
  wire [31:0] _GEN_18 = _T_10 ? _GEN_14 : {{27'd0}, rem_cyc_len_words}; // @[Conditional.scala 39:67 AvalonMMReader.scala 65:30]
  wire [31:0] _GEN_23 = _T_7 ? {{27'd0}, rem_cyc_len_words} : _GEN_18; // @[Conditional.scala 39:67 AvalonMMReader.scala 65:30]
  wire [31:0] _GEN_28 = _T ? _GEN_3 : _GEN_23; // @[Conditional.scala 40:58]
  wire  _T_23 = state == 2'h1; // @[AvalonMMReader.scala 113:15]
  wire [4:0] _T_25 = rem_cyc_len_words + 5'h1; // @[AvalonMMReader.scala 114:40]
  wire  _T_28 = state == 2'h0; // @[AvalonMMReader.scala 130:25]
  wire  _T_29 = state == 2'h0 & io_ctrl_start; // @[AvalonMMReader.scala 130:35]
  wire  _T_30 = state != 2'h0; // @[AvalonMMReader.scala 131:24]
  reg [31:0] resp_cntr_reg; // @[AvalonMMReader.scala 135:26]
  wire [31:0] _T_38 = resp_cntr_reg + 32'h1; // @[AvalonMMReader.scala 142:38]
  reg [1:0] state_prev; // @[AvalonMMReader.scala 148:27]
  reg [31:0] rd_duration; // @[AvalonMMReader.scala 151:28]
  reg  rd_duration_en; // @[AvalonMMReader.scala 152:31]
  wire  _GEN_35 = io_stats_done ? 1'h0 : rd_duration_en; // @[AvalonMMReader.scala 157:31 AvalonMMReader.scala 158:20 AvalonMMReader.scala 152:31]
  wire  _GEN_36 = _T_29 | _GEN_35; // @[AvalonMMReader.scala 155:43 AvalonMMReader.scala 156:20]
  wire [31:0] _T_45 = rd_duration + 32'h1; // @[AvalonMMReader.scala 162:32]
  assign io_address = _T_23 ? cur_addr : 48'h0; // @[AvalonMMReader.scala 119:28 AvalonMMReader.scala 120:16 AvalonMMReader.scala 122:16]
  assign io_read = state == 2'h1; // @[AvalonMMReader.scala 125:20]
  assign io_burstcount = state == 2'h1 ? _T_25 : 5'h0; // @[AvalonMMReader.scala 113:28 AvalonMMReader.scala 114:19 AvalonMMReader.scala 116:19]
  assign io_data_init = state == 2'h0 & io_ctrl_start; // @[AvalonMMReader.scala 130:35]
  assign io_data_inc = state != 2'h0 & io_readdatavalid; // @[AvalonMMReader.scala 131:34]
  assign io_data_data = io_readdata; // @[AvalonMMReader.scala 129:16]
  assign io_stats_resp_cntr = resp_cntr_reg; // @[AvalonMMReader.scala 136:22]
  assign io_stats_done = state_prev != 2'h0 & _T_28; // @[AvalonMMReader.scala 149:41]
  assign io_stats_duration = rd_duration; // @[AvalonMMReader.scala 153:21]
  always @(posedge clock) begin
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMReader.scala 72:27]
        cur_addr <= io_ctrl_addr; // @[AvalonMMReader.scala 73:18]
      end
    end else if (!(_T_7)) begin // @[Conditional.scala 39:67]
      if (_T_10) begin // @[Conditional.scala 39:67]
        if (io_readdatavalid) begin // @[AvalonMMReader.scala 93:30]
          cur_addr <= _GEN_12;
        end
      end
    end
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMReader.scala 72:27]
        rem_tot_len_bytes <= _T_2; // @[AvalonMMReader.scala 74:27]
      end
    end else if (!(_T_7)) begin // @[Conditional.scala 39:67]
      if (_T_10) begin // @[Conditional.scala 39:67]
        if (io_readdatavalid) begin // @[AvalonMMReader.scala 93:30]
          rem_tot_len_bytes <= _T_14; // @[AvalonMMReader.scala 95:27]
        end
      end
    end
    rem_cyc_len_words <= _GEN_28[4:0];
    if (reset) begin // @[AvalonMMReader.scala 68:22]
      state <= 2'h0; // @[AvalonMMReader.scala 68:22]
    end else if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_start) begin // @[AvalonMMReader.scala 72:27]
        state <= 2'h1; // @[AvalonMMReader.scala 80:15]
      end
    end else if (_T_7) begin // @[Conditional.scala 39:67]
      if (~io_waitrequest) begin // @[AvalonMMReader.scala 84:29]
        state <= _GEN_5;
      end
    end else if (_T_10) begin // @[Conditional.scala 39:67]
      state <= _GEN_16;
    end
    if (_T_29) begin // @[AvalonMMReader.scala 138:43]
      resp_cntr_reg <= 32'h0; // @[AvalonMMReader.scala 139:19]
    end else if (_T_30) begin // @[AvalonMMReader.scala 140:33]
      if (io_readdatavalid & io_response == 2'h0) begin // @[AvalonMMReader.scala 141:52]
        resp_cntr_reg <= _T_38; // @[AvalonMMReader.scala 142:21]
      end
    end
    state_prev <= state; // @[AvalonMMReader.scala 148:27]
    if (reset) begin // @[AvalonMMReader.scala 151:28]
      rd_duration <= 32'h0; // @[AvalonMMReader.scala 151:28]
    end else if (rd_duration_en) begin // @[AvalonMMReader.scala 161:25]
      rd_duration <= _T_45; // @[AvalonMMReader.scala 162:17]
    end else if (_T_29) begin // @[AvalonMMReader.scala 163:49]
      rd_duration <= 32'h0; // @[AvalonMMReader.scala 164:17]
    end
    if (reset) begin // @[AvalonMMReader.scala 152:31]
      rd_duration_en <= 1'h0; // @[AvalonMMReader.scala 152:31]
    end else begin
      rd_duration_en <= _GEN_36;
    end
  end
// Register and memory initialization
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
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
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
  _RAND_0 = {2{`RANDOM}};
  cur_addr = _RAND_0[47:0];
  _RAND_1 = {1{`RANDOM}};
  rem_tot_len_bytes = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  rem_cyc_len_words = _RAND_2[4:0];
  _RAND_3 = {1{`RANDOM}};
  state = _RAND_3[1:0];
  _RAND_4 = {1{`RANDOM}};
  resp_cntr_reg = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  state_prev = _RAND_5[1:0];
  _RAND_6 = {1{`RANDOM}};
  rd_duration = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  rd_duration_en = _RAND_7[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module DataDriver(
  input          clock,
  input  [3:0]   io_ctrl_mode,
  input          io_data_init,
  input          io_data_inc,
  output [127:0] io_data_data
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [127:0] _RAND_1;
`endif // RANDOMIZE_REG_INIT
  reg [3:0] mode_reg; // @[DataDriver.scala 50:25]
  reg [127:0] data_reg; // @[DataDriver.scala 59:21]
  wire  _T = 4'h0 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_1 = 4'h1 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_3 = 4'h2 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_4 = 4'h3 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_7 = 4'h4 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_9 = 4'h5 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_10 = 4'h6 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_11 = 4'h7 == mode_reg; // @[Conditional.scala 37:30]
  wire [127:0] _GEN_0 = _T_11 ? 128'h0 : data_reg; // @[Conditional.scala 39:67 DataDriver.scala 70:26 DataDriver.scala 59:21]
  wire [127:0] _GEN_1 = _T_10 ? 128'h3000000020000000100000000 : _GEN_0; // @[Conditional.scala 39:67 DataDriver.scala 69:26]
  wire [127:0] _GEN_2 = _T_9 ? 128'hf0e0d0c0b0a09080706050403020100 : _GEN_1; // @[Conditional.scala 39:67 DataDriver.scala 68:26]
  wire [127:0] _GEN_3 = _T_7 ? 128'hffffffffffffffffffffffffffffffff : _GEN_2; // @[Conditional.scala 39:67 DataDriver.scala 67:26]
  wire [127:0] _GEN_4 = _T_4 ? 128'hfffffffffffffffffffffffffffffffe : _GEN_3; // @[Conditional.scala 39:67 DataDriver.scala 66:26]
  wire [127:0] _GEN_5 = _T_3 ? 128'h1 : _GEN_4; // @[Conditional.scala 39:67 DataDriver.scala 65:26]
  wire [127:0] _GEN_6 = _T_1 ? 128'hffffffffffffffffffffffffffffffff : _GEN_5; // @[Conditional.scala 39:67 DataDriver.scala 64:26]
  wire [127:0] _GEN_7 = _T ? 128'h0 : _GEN_6; // @[Conditional.scala 40:58 DataDriver.scala 63:26]
  wire [126:0] hi = data_reg[126:0]; // @[DataDriver.scala 76:41]
  wire  lo = data_reg[127]; // @[DataDriver.scala 76:64]
  wire [127:0] _T_15 = {hi,lo}; // @[Cat.scala 30:58]
  wire [127:0] _T_20 = data_reg ^ 128'hffffffffffffffffffffffffffffffff; // @[DataDriver.scala 78:38]
  wire [127:0] _T_23 = data_reg + 128'h10101010101010101010101010101010; // @[DataDriver.scala 79:38]
  wire [482:0] _GEN_18 = {{355'd0}, data_reg}; // @[DataDriver.scala 80:38]
  wire [482:0] _T_26 = _GEN_18 + 483'h4000000040000000400000004000000040000000400000004000000040000000400000004000000040000000400000004000000040000000400000004
    ; // @[DataDriver.scala 80:38]
  wire [1920:0] _GEN_19 = {{1793'd0}, data_reg}; // @[DataDriver.scala 81:38]
  wire [1920:0] _T_29 = _GEN_19 + 1921'h1000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001
    ; // @[DataDriver.scala 81:38]
  wire [1920:0] _GEN_8 = _T_11 ? _T_29 : {{1793'd0}, data_reg}; // @[Conditional.scala 39:67 DataDriver.scala 81:26 DataDriver.scala 59:21]
  wire [1920:0] _GEN_9 = _T_10 ? {{1438'd0}, _T_26} : _GEN_8; // @[Conditional.scala 39:67 DataDriver.scala 80:26]
  wire [1920:0] _GEN_10 = _T_9 ? {{1793'd0}, _T_23} : _GEN_9; // @[Conditional.scala 39:67 DataDriver.scala 79:26]
  wire [1920:0] _GEN_11 = _T_7 ? {{1793'd0}, _T_20} : _GEN_10; // @[Conditional.scala 39:67 DataDriver.scala 78:26]
  wire [1920:0] _GEN_12 = _T_4 ? {{1793'd0}, _T_15} : _GEN_11; // @[Conditional.scala 39:67 DataDriver.scala 77:26]
  wire [1920:0] _GEN_13 = _T_3 ? {{1793'd0}, _T_15} : _GEN_12; // @[Conditional.scala 39:67 DataDriver.scala 76:26]
  wire [1920:0] _GEN_14 = _T_1 ? {{1793'd0}, data_reg} : _GEN_13; // @[Conditional.scala 39:67 DataDriver.scala 59:21]
  wire [1920:0] _GEN_15 = _T ? {{1793'd0}, data_reg} : _GEN_14; // @[Conditional.scala 40:58 DataDriver.scala 59:21]
  wire [1920:0] _GEN_16 = io_data_inc ? _GEN_15 : {{1793'd0}, data_reg}; // @[DataDriver.scala 72:27 DataDriver.scala 59:21]
  wire [1920:0] _GEN_17 = io_data_init ? {{1793'd0}, _GEN_7} : _GEN_16; // @[DataDriver.scala 61:22]
  assign io_data_data = data_reg; // @[DataDriver.scala 85:16]
  always @(posedge clock) begin
    mode_reg <= io_ctrl_mode; // @[DataDriver.scala 50:25]
    data_reg <= _GEN_17[127:0];
  end
// Register and memory initialization
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
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
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
  mode_reg = _RAND_0[3:0];
  _RAND_1 = {4{`RANDOM}};
  data_reg = _RAND_1[127:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module DataChecker(
  input          clock,
  input  [3:0]   io_ctrl_mode,
  input          io_data_init,
  input          io_data_inc,
  input  [127:0] io_data_data,
  output [31:0]  io_check_tot,
  output [31:0]  io_check_ok
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [127:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [3:0] mode_reg; // @[DataChecker.scala 53:25]
  reg [127:0] data_reg; // @[DataChecker.scala 62:21]
  wire  _T = 4'h0 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_1 = 4'h1 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_3 = 4'h2 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_4 = 4'h3 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_7 = 4'h4 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_9 = 4'h5 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_10 = 4'h6 == mode_reg; // @[Conditional.scala 37:30]
  wire  _T_11 = 4'h7 == mode_reg; // @[Conditional.scala 37:30]
  wire [127:0] _GEN_0 = _T_11 ? 128'h0 : data_reg; // @[Conditional.scala 39:67 DataChecker.scala 73:26 DataChecker.scala 62:21]
  wire [127:0] _GEN_1 = _T_10 ? 128'h3000000020000000100000000 : _GEN_0; // @[Conditional.scala 39:67 DataChecker.scala 72:26]
  wire [127:0] _GEN_2 = _T_9 ? 128'hf0e0d0c0b0a09080706050403020100 : _GEN_1; // @[Conditional.scala 39:67 DataChecker.scala 71:26]
  wire [127:0] _GEN_3 = _T_7 ? 128'hffffffffffffffffffffffffffffffff : _GEN_2; // @[Conditional.scala 39:67 DataChecker.scala 70:26]
  wire [127:0] _GEN_4 = _T_4 ? 128'hfffffffffffffffffffffffffffffffe : _GEN_3; // @[Conditional.scala 39:67 DataChecker.scala 69:26]
  wire [127:0] _GEN_5 = _T_3 ? 128'h1 : _GEN_4; // @[Conditional.scala 39:67 DataChecker.scala 68:26]
  wire [127:0] _GEN_6 = _T_1 ? 128'hffffffffffffffffffffffffffffffff : _GEN_5; // @[Conditional.scala 39:67 DataChecker.scala 67:26]
  wire [127:0] _GEN_7 = _T ? 128'h0 : _GEN_6; // @[Conditional.scala 40:58 DataChecker.scala 66:26]
  wire [126:0] hi = data_reg[126:0]; // @[DataChecker.scala 79:41]
  wire  lo = data_reg[127]; // @[DataChecker.scala 79:64]
  wire [127:0] _T_15 = {hi,lo}; // @[Cat.scala 30:58]
  wire [127:0] _T_20 = data_reg ^ 128'hffffffffffffffffffffffffffffffff; // @[DataChecker.scala 81:38]
  wire [127:0] _T_23 = data_reg + 128'h10101010101010101010101010101010; // @[DataChecker.scala 82:38]
  wire [482:0] _GEN_23 = {{355'd0}, data_reg}; // @[DataChecker.scala 83:38]
  wire [482:0] _T_26 = _GEN_23 + 483'h4000000040000000400000004000000040000000400000004000000040000000400000004000000040000000400000004000000040000000400000004
    ; // @[DataChecker.scala 83:38]
  wire [1920:0] _GEN_24 = {{1793'd0}, data_reg}; // @[DataChecker.scala 84:38]
  wire [1920:0] _T_29 = _GEN_24 + 1921'h1000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001000000000000000000000000000000010000000000000000000000000000000100000000000000000000000000000001
    ; // @[DataChecker.scala 84:38]
  wire [1920:0] _GEN_8 = _T_11 ? _T_29 : {{1793'd0}, data_reg}; // @[Conditional.scala 39:67 DataChecker.scala 84:26 DataChecker.scala 62:21]
  wire [1920:0] _GEN_9 = _T_10 ? {{1438'd0}, _T_26} : _GEN_8; // @[Conditional.scala 39:67 DataChecker.scala 83:26]
  wire [1920:0] _GEN_10 = _T_9 ? {{1793'd0}, _T_23} : _GEN_9; // @[Conditional.scala 39:67 DataChecker.scala 82:26]
  wire [1920:0] _GEN_11 = _T_7 ? {{1793'd0}, _T_20} : _GEN_10; // @[Conditional.scala 39:67 DataChecker.scala 81:26]
  wire [1920:0] _GEN_12 = _T_4 ? {{1793'd0}, _T_15} : _GEN_11; // @[Conditional.scala 39:67 DataChecker.scala 80:26]
  wire [1920:0] _GEN_13 = _T_3 ? {{1793'd0}, _T_15} : _GEN_12; // @[Conditional.scala 39:67 DataChecker.scala 79:26]
  wire [1920:0] _GEN_14 = _T_1 ? {{1793'd0}, data_reg} : _GEN_13; // @[Conditional.scala 39:67 DataChecker.scala 62:21]
  wire [1920:0] _GEN_15 = _T ? {{1793'd0}, data_reg} : _GEN_14; // @[Conditional.scala 40:58 DataChecker.scala 62:21]
  wire [1920:0] _GEN_16 = io_data_inc ? _GEN_15 : {{1793'd0}, data_reg}; // @[DataChecker.scala 75:27 DataChecker.scala 62:21]
  wire [1920:0] _GEN_17 = io_data_init ? {{1793'd0}, _GEN_7} : _GEN_16; // @[DataChecker.scala 64:22]
  reg [31:0] check_tot_reg; // @[DataChecker.scala 89:26]
  reg [31:0] check_ok_reg; // @[DataChecker.scala 90:25]
  wire [31:0] _T_31 = check_tot_reg + 32'h1; // @[DataChecker.scala 98:36]
  wire [31:0] _T_34 = check_ok_reg + 32'h1; // @[DataChecker.scala 100:36]
  assign io_check_tot = check_tot_reg; // @[DataChecker.scala 91:16]
  assign io_check_ok = check_ok_reg; // @[DataChecker.scala 92:15]
  always @(posedge clock) begin
    mode_reg <= io_ctrl_mode; // @[DataChecker.scala 53:25]
    data_reg <= _GEN_17[127:0];
    if (io_data_init) begin // @[DataChecker.scala 94:23]
      check_tot_reg <= 32'h0; // @[DataChecker.scala 95:19]
    end else if (io_data_inc) begin // @[DataChecker.scala 97:29]
      check_tot_reg <= _T_31; // @[DataChecker.scala 98:19]
    end
    if (io_data_init) begin // @[DataChecker.scala 94:23]
      check_ok_reg <= 32'h0; // @[DataChecker.scala 96:18]
    end else if (io_data_inc) begin // @[DataChecker.scala 97:29]
      if (data_reg == io_data_data) begin // @[DataChecker.scala 99:38]
        check_ok_reg <= _T_34; // @[DataChecker.scala 100:20]
      end
    end
  end
// Register and memory initialization
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
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
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
  mode_reg = _RAND_0[3:0];
  _RAND_1 = {4{`RANDOM}};
  data_reg = _RAND_1[127:0];
  _RAND_2 = {1{`RANDOM}};
  check_tot_reg = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  check_ok_reg = _RAND_3[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module MemChecker(
  input          clock,
  input          reset,
  output [47:0]  io_mem_address,
  output [15:0]  io_mem_byteenable,
  output         io_mem_read,
  input  [127:0] io_mem_readdata,
  input  [1:0]   io_mem_response,
  output         io_mem_write,
  output [127:0] io_mem_writedata,
  input          io_mem_waitrequest,
  input          io_mem_readdatavalid,
  input          io_mem_writeresponsevalid,
  output [4:0]   io_mem_burstcount,
  output         io_ctrl_AW_ready,
  input          io_ctrl_AW_valid,
  input  [7:0]   io_ctrl_AW_bits_addr,
  input  [2:0]   io_ctrl_AW_bits_prot,
  output         io_ctrl_W_ready,
  input          io_ctrl_W_valid,
  input  [31:0]  io_ctrl_W_bits_wdata,
  input  [3:0]   io_ctrl_W_bits_wstrb,
  input          io_ctrl_B_ready,
  output         io_ctrl_B_valid,
  output [1:0]   io_ctrl_B_bits,
  output         io_ctrl_AR_ready,
  input          io_ctrl_AR_valid,
  input  [7:0]   io_ctrl_AR_bits_addr,
  input  [2:0]   io_ctrl_AR_bits_prot,
  input          io_ctrl_R_ready,
  output         io_ctrl_R_valid,
  output [31:0]  io_ctrl_R_bits_rdata,
  output [1:0]   io_ctrl_R_bits_rresp
);
  wire  mod_axi_slave_clock; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_reset; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_AW_ready; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_AW_valid; // @[MemChecker.scala 40:29]
  wire [7:0] mod_axi_slave_io_ctrl_AW_bits_addr; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_W_ready; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_W_valid; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_ctrl_W_bits_wdata; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_B_ready; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_B_valid; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_AR_ready; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_AR_valid; // @[MemChecker.scala 40:29]
  wire [7:0] mod_axi_slave_io_ctrl_AR_bits_addr; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_R_ready; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_R_valid; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_ctrl_R_bits_rdata; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_ctrl_dir; // @[MemChecker.scala 40:29]
  wire [2:0] mod_axi_slave_io_ctrl_mode; // @[MemChecker.scala 40:29]
  wire [63:0] mod_axi_slave_io_read_addr; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_read_len; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_read_start; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_rd_stats_resp_cntr; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_rd_stats_done; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_rd_stats_duration; // @[MemChecker.scala 40:29]
  wire [63:0] mod_axi_slave_io_write_addr; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_write_len; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_write_start; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_wr_stats_resp_cntr; // @[MemChecker.scala 40:29]
  wire  mod_axi_slave_io_wr_stats_done; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_wr_stats_duration; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_check_tot; // @[MemChecker.scala 40:29]
  wire [31:0] mod_axi_slave_io_check_ok; // @[MemChecker.scala 40:29]
  wire  mod_avalon_wr_clock; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_reset; // @[MemChecker.scala 43:29]
  wire [47:0] mod_avalon_wr_io_address; // @[MemChecker.scala 43:29]
  wire [1:0] mod_avalon_wr_io_response; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_write; // @[MemChecker.scala 43:29]
  wire [127:0] mod_avalon_wr_io_writedata; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_waitrequest; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_writeresponsevalid; // @[MemChecker.scala 43:29]
  wire [4:0] mod_avalon_wr_io_burstcount; // @[MemChecker.scala 43:29]
  wire [47:0] mod_avalon_wr_io_ctrl_addr; // @[MemChecker.scala 43:29]
  wire [31:0] mod_avalon_wr_io_ctrl_len_bytes; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_ctrl_start; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_data_init; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_data_inc; // @[MemChecker.scala 43:29]
  wire [127:0] mod_avalon_wr_io_data_data; // @[MemChecker.scala 43:29]
  wire [31:0] mod_avalon_wr_io_stats_resp_cntr; // @[MemChecker.scala 43:29]
  wire  mod_avalon_wr_io_stats_done; // @[MemChecker.scala 43:29]
  wire [31:0] mod_avalon_wr_io_stats_duration; // @[MemChecker.scala 43:29]
  wire  mod_avalon_rd_clock; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_reset; // @[MemChecker.scala 44:29]
  wire [47:0] mod_avalon_rd_io_address; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_read; // @[MemChecker.scala 44:29]
  wire [127:0] mod_avalon_rd_io_readdata; // @[MemChecker.scala 44:29]
  wire [1:0] mod_avalon_rd_io_response; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_waitrequest; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_readdatavalid; // @[MemChecker.scala 44:29]
  wire [4:0] mod_avalon_rd_io_burstcount; // @[MemChecker.scala 44:29]
  wire [47:0] mod_avalon_rd_io_ctrl_addr; // @[MemChecker.scala 44:29]
  wire [31:0] mod_avalon_rd_io_ctrl_len_bytes; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_ctrl_start; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_data_init; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_data_inc; // @[MemChecker.scala 44:29]
  wire [127:0] mod_avalon_rd_io_data_data; // @[MemChecker.scala 44:29]
  wire [31:0] mod_avalon_rd_io_stats_resp_cntr; // @[MemChecker.scala 44:29]
  wire  mod_avalon_rd_io_stats_done; // @[MemChecker.scala 44:29]
  wire [31:0] mod_avalon_rd_io_stats_duration; // @[MemChecker.scala 44:29]
  wire  mod_data_drv_clock; // @[MemChecker.scala 46:28]
  wire [3:0] mod_data_drv_io_ctrl_mode; // @[MemChecker.scala 46:28]
  wire  mod_data_drv_io_data_init; // @[MemChecker.scala 46:28]
  wire  mod_data_drv_io_data_inc; // @[MemChecker.scala 46:28]
  wire [127:0] mod_data_drv_io_data_data; // @[MemChecker.scala 46:28]
  wire  mod_data_chk_clock; // @[MemChecker.scala 47:28]
  wire [3:0] mod_data_chk_io_ctrl_mode; // @[MemChecker.scala 47:28]
  wire  mod_data_chk_io_data_init; // @[MemChecker.scala 47:28]
  wire  mod_data_chk_io_data_inc; // @[MemChecker.scala 47:28]
  wire [127:0] mod_data_chk_io_data_data; // @[MemChecker.scala 47:28]
  wire [31:0] mod_data_chk_io_check_tot; // @[MemChecker.scala 47:28]
  wire [31:0] mod_data_chk_io_check_ok; // @[MemChecker.scala 47:28]
  MemCheckerAxiSlave mod_axi_slave ( // @[MemChecker.scala 40:29]
    .clock(mod_axi_slave_clock),
    .reset(mod_axi_slave_reset),
    .io_ctrl_AW_ready(mod_axi_slave_io_ctrl_AW_ready),
    .io_ctrl_AW_valid(mod_axi_slave_io_ctrl_AW_valid),
    .io_ctrl_AW_bits_addr(mod_axi_slave_io_ctrl_AW_bits_addr),
    .io_ctrl_W_ready(mod_axi_slave_io_ctrl_W_ready),
    .io_ctrl_W_valid(mod_axi_slave_io_ctrl_W_valid),
    .io_ctrl_W_bits_wdata(mod_axi_slave_io_ctrl_W_bits_wdata),
    .io_ctrl_B_ready(mod_axi_slave_io_ctrl_B_ready),
    .io_ctrl_B_valid(mod_axi_slave_io_ctrl_B_valid),
    .io_ctrl_AR_ready(mod_axi_slave_io_ctrl_AR_ready),
    .io_ctrl_AR_valid(mod_axi_slave_io_ctrl_AR_valid),
    .io_ctrl_AR_bits_addr(mod_axi_slave_io_ctrl_AR_bits_addr),
    .io_ctrl_R_ready(mod_axi_slave_io_ctrl_R_ready),
    .io_ctrl_R_valid(mod_axi_slave_io_ctrl_R_valid),
    .io_ctrl_R_bits_rdata(mod_axi_slave_io_ctrl_R_bits_rdata),
    .io_ctrl_dir(mod_axi_slave_io_ctrl_dir),
    .io_ctrl_mode(mod_axi_slave_io_ctrl_mode),
    .io_read_addr(mod_axi_slave_io_read_addr),
    .io_read_len(mod_axi_slave_io_read_len),
    .io_read_start(mod_axi_slave_io_read_start),
    .io_rd_stats_resp_cntr(mod_axi_slave_io_rd_stats_resp_cntr),
    .io_rd_stats_done(mod_axi_slave_io_rd_stats_done),
    .io_rd_stats_duration(mod_axi_slave_io_rd_stats_duration),
    .io_write_addr(mod_axi_slave_io_write_addr),
    .io_write_len(mod_axi_slave_io_write_len),
    .io_write_start(mod_axi_slave_io_write_start),
    .io_wr_stats_resp_cntr(mod_axi_slave_io_wr_stats_resp_cntr),
    .io_wr_stats_done(mod_axi_slave_io_wr_stats_done),
    .io_wr_stats_duration(mod_axi_slave_io_wr_stats_duration),
    .io_check_tot(mod_axi_slave_io_check_tot),
    .io_check_ok(mod_axi_slave_io_check_ok)
  );
  AvalonMMWriter mod_avalon_wr ( // @[MemChecker.scala 43:29]
    .clock(mod_avalon_wr_clock),
    .reset(mod_avalon_wr_reset),
    .io_address(mod_avalon_wr_io_address),
    .io_response(mod_avalon_wr_io_response),
    .io_write(mod_avalon_wr_io_write),
    .io_writedata(mod_avalon_wr_io_writedata),
    .io_waitrequest(mod_avalon_wr_io_waitrequest),
    .io_writeresponsevalid(mod_avalon_wr_io_writeresponsevalid),
    .io_burstcount(mod_avalon_wr_io_burstcount),
    .io_ctrl_addr(mod_avalon_wr_io_ctrl_addr),
    .io_ctrl_len_bytes(mod_avalon_wr_io_ctrl_len_bytes),
    .io_ctrl_start(mod_avalon_wr_io_ctrl_start),
    .io_data_init(mod_avalon_wr_io_data_init),
    .io_data_inc(mod_avalon_wr_io_data_inc),
    .io_data_data(mod_avalon_wr_io_data_data),
    .io_stats_resp_cntr(mod_avalon_wr_io_stats_resp_cntr),
    .io_stats_done(mod_avalon_wr_io_stats_done),
    .io_stats_duration(mod_avalon_wr_io_stats_duration)
  );
  AvalonMMReader mod_avalon_rd ( // @[MemChecker.scala 44:29]
    .clock(mod_avalon_rd_clock),
    .reset(mod_avalon_rd_reset),
    .io_address(mod_avalon_rd_io_address),
    .io_read(mod_avalon_rd_io_read),
    .io_readdata(mod_avalon_rd_io_readdata),
    .io_response(mod_avalon_rd_io_response),
    .io_waitrequest(mod_avalon_rd_io_waitrequest),
    .io_readdatavalid(mod_avalon_rd_io_readdatavalid),
    .io_burstcount(mod_avalon_rd_io_burstcount),
    .io_ctrl_addr(mod_avalon_rd_io_ctrl_addr),
    .io_ctrl_len_bytes(mod_avalon_rd_io_ctrl_len_bytes),
    .io_ctrl_start(mod_avalon_rd_io_ctrl_start),
    .io_data_init(mod_avalon_rd_io_data_init),
    .io_data_inc(mod_avalon_rd_io_data_inc),
    .io_data_data(mod_avalon_rd_io_data_data),
    .io_stats_resp_cntr(mod_avalon_rd_io_stats_resp_cntr),
    .io_stats_done(mod_avalon_rd_io_stats_done),
    .io_stats_duration(mod_avalon_rd_io_stats_duration)
  );
  DataDriver mod_data_drv ( // @[MemChecker.scala 46:28]
    .clock(mod_data_drv_clock),
    .io_ctrl_mode(mod_data_drv_io_ctrl_mode),
    .io_data_init(mod_data_drv_io_data_init),
    .io_data_inc(mod_data_drv_io_data_inc),
    .io_data_data(mod_data_drv_io_data_data)
  );
  DataChecker mod_data_chk ( // @[MemChecker.scala 47:28]
    .clock(mod_data_chk_clock),
    .io_ctrl_mode(mod_data_chk_io_ctrl_mode),
    .io_data_init(mod_data_chk_io_data_init),
    .io_data_inc(mod_data_chk_io_data_inc),
    .io_data_data(mod_data_chk_io_data_data),
    .io_check_tot(mod_data_chk_io_check_tot),
    .io_check_ok(mod_data_chk_io_check_ok)
  );
  assign io_mem_address = mod_axi_slave_io_ctrl_dir ? mod_avalon_wr_io_address : mod_avalon_rd_io_address; // @[MemChecker.scala 98:35 MemChecker.scala 99:20 MemChecker.scala 102:20]
  assign io_mem_byteenable = 16'hffff; // @[MemChecker.scala 52:21]
  assign io_mem_read = mod_avalon_rd_io_read; // @[MemChecker.scala 74:15]
  assign io_mem_write = mod_avalon_wr_io_write; // @[MemChecker.scala 53:16]
  assign io_mem_writedata = mod_avalon_wr_io_writedata; // @[MemChecker.scala 54:20]
  assign io_mem_burstcount = mod_axi_slave_io_ctrl_dir ? mod_avalon_wr_io_burstcount : mod_avalon_rd_io_burstcount; // @[MemChecker.scala 98:35 MemChecker.scala 100:23 MemChecker.scala 103:23]
  assign io_ctrl_AW_ready = mod_axi_slave_io_ctrl_AW_ready; // @[MemChecker.scala 49:11]
  assign io_ctrl_W_ready = mod_axi_slave_io_ctrl_W_ready; // @[MemChecker.scala 49:11]
  assign io_ctrl_B_valid = mod_axi_slave_io_ctrl_B_valid; // @[MemChecker.scala 49:11]
  assign io_ctrl_B_bits = 2'h0; // @[MemChecker.scala 49:11]
  assign io_ctrl_AR_ready = mod_axi_slave_io_ctrl_AR_ready; // @[MemChecker.scala 49:11]
  assign io_ctrl_R_valid = mod_axi_slave_io_ctrl_R_valid; // @[MemChecker.scala 49:11]
  assign io_ctrl_R_bits_rdata = mod_axi_slave_io_ctrl_R_bits_rdata; // @[MemChecker.scala 49:11]
  assign io_ctrl_R_bits_rresp = 2'h0; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_clock = clock;
  assign mod_axi_slave_reset = reset;
  assign mod_axi_slave_io_ctrl_AW_valid = io_ctrl_AW_valid; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_AW_bits_addr = io_ctrl_AW_bits_addr; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_W_valid = io_ctrl_W_valid; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_W_bits_wdata = io_ctrl_W_bits_wdata; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_B_ready = io_ctrl_B_ready; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_AR_valid = io_ctrl_AR_valid; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_AR_bits_addr = io_ctrl_AR_bits_addr; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_ctrl_R_ready = io_ctrl_R_ready; // @[MemChecker.scala 49:11]
  assign mod_axi_slave_io_rd_stats_resp_cntr = mod_avalon_rd_io_stats_resp_cntr; // @[MemChecker.scala 83:39]
  assign mod_axi_slave_io_rd_stats_done = mod_avalon_rd_io_stats_done; // @[MemChecker.scala 84:34]
  assign mod_axi_slave_io_rd_stats_duration = mod_avalon_rd_io_stats_duration; // @[MemChecker.scala 85:38]
  assign mod_axi_slave_io_wr_stats_resp_cntr = mod_avalon_wr_io_stats_resp_cntr; // @[MemChecker.scala 62:39]
  assign mod_axi_slave_io_wr_stats_done = mod_avalon_wr_io_stats_done; // @[MemChecker.scala 63:34]
  assign mod_axi_slave_io_wr_stats_duration = mod_avalon_wr_io_stats_duration; // @[MemChecker.scala 64:38]
  assign mod_axi_slave_io_check_tot = mod_data_chk_io_check_tot; // @[MemChecker.scala 94:30]
  assign mod_axi_slave_io_check_ok = mod_data_chk_io_check_ok; // @[MemChecker.scala 95:29]
  assign mod_avalon_wr_clock = clock;
  assign mod_avalon_wr_reset = reset;
  assign mod_avalon_wr_io_response = io_mem_response; // @[MemChecker.scala 55:29]
  assign mod_avalon_wr_io_waitrequest = io_mem_waitrequest; // @[MemChecker.scala 56:32]
  assign mod_avalon_wr_io_writeresponsevalid = io_mem_writeresponsevalid; // @[MemChecker.scala 57:39]
  assign mod_avalon_wr_io_ctrl_addr = mod_axi_slave_io_write_addr[47:0]; // @[MemChecker.scala 59:30]
  assign mod_avalon_wr_io_ctrl_len_bytes = mod_axi_slave_io_write_len; // @[MemChecker.scala 60:35]
  assign mod_avalon_wr_io_ctrl_start = mod_axi_slave_io_write_start; // @[MemChecker.scala 61:31]
  assign mod_avalon_wr_io_data_data = mod_data_drv_io_data_data; // @[MemChecker.scala 71:30]
  assign mod_avalon_rd_clock = clock;
  assign mod_avalon_rd_reset = reset;
  assign mod_avalon_rd_io_readdata = io_mem_readdata; // @[MemChecker.scala 75:29]
  assign mod_avalon_rd_io_response = io_mem_response; // @[MemChecker.scala 76:29]
  assign mod_avalon_rd_io_waitrequest = io_mem_waitrequest; // @[MemChecker.scala 77:32]
  assign mod_avalon_rd_io_readdatavalid = io_mem_readdatavalid; // @[MemChecker.scala 78:34]
  assign mod_avalon_rd_io_ctrl_addr = mod_axi_slave_io_read_addr[47:0]; // @[MemChecker.scala 80:30]
  assign mod_avalon_rd_io_ctrl_len_bytes = mod_axi_slave_io_read_len; // @[MemChecker.scala 81:35]
  assign mod_avalon_rd_io_ctrl_start = mod_axi_slave_io_read_start; // @[MemChecker.scala 82:31]
  assign mod_data_drv_clock = clock;
  assign mod_data_drv_io_ctrl_mode = {{1'd0}, mod_axi_slave_io_ctrl_mode}; // @[MemChecker.scala 67:29]
  assign mod_data_drv_io_data_init = mod_avalon_wr_io_data_init; // @[MemChecker.scala 69:29]
  assign mod_data_drv_io_data_inc = mod_avalon_wr_io_data_inc; // @[MemChecker.scala 70:28]
  assign mod_data_chk_clock = clock;
  assign mod_data_chk_io_ctrl_mode = {{1'd0}, mod_axi_slave_io_ctrl_mode}; // @[MemChecker.scala 88:29]
  assign mod_data_chk_io_data_init = mod_avalon_rd_io_data_init; // @[MemChecker.scala 90:29]
  assign mod_data_chk_io_data_inc = mod_avalon_rd_io_data_inc; // @[MemChecker.scala 91:28]
  assign mod_data_chk_io_data_data = mod_avalon_rd_io_data_data; // @[MemChecker.scala 92:29]
endmodule
