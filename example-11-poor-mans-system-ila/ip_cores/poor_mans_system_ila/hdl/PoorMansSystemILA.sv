module AxiLiteSubordinateGenerator(
  input         clock,
  input         reset,
  input  [31:0] io_inp_MEM_DATA_DOUT,
  input  [15:0] io_inp_TRIG_FILT_HI_MARK,
  input         io_inp_STATUS_DONE,
  output        io_out_MEM_DATA_WE,
  output [11:0] io_out_MEM_DATA_ADDR,
  output [31:0] io_out_MEM_DATA_DIN,
  output [12:0] io_out_TRIG_FILT_LEVEL,
  output        io_out_TRIG_CTRL_FORCE,
  output        io_out_TRIG_CTRL_FILT_EN,
  output        io_out_TRIG_CTRL_CORR_EN,
  output [16:0] io_out_TRIG_CTRL_MASK,
  output        io_out_CONTROL_CLEAR,
  output        io_ctrl_AW_ready,
  input         io_ctrl_AW_valid,
  input  [14:0] io_ctrl_AW_bits_addr,
  output        io_ctrl_W_ready,
  input         io_ctrl_W_valid,
  input  [31:0] io_ctrl_W_bits_wdata,
  input         io_ctrl_B_ready,
  output        io_ctrl_B_valid,
  output        io_ctrl_AR_ready,
  input         io_ctrl_AR_valid,
  input  [14:0] io_ctrl_AR_bits_addr,
  input         io_ctrl_R_ready,
  output        io_ctrl_R_valid,
  output [31:0] io_ctrl_R_bits_rdata
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
  reg [31:0] _RAND_6;
  reg [31:0] _RAND_7;
  reg [31:0] _RAND_8;
  reg [31:0] _RAND_9;
  reg [31:0] _RAND_10;
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
  reg [63:0] _RAND_22;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] SCRATCH_FIELD; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  STATUS_DONE; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  CONTROL_CLEAR; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg [16:0] TRIG_CTRL_MASK; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  TRIG_CTRL_CORR_EN; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  TRIG_CTRL_FILT_EN; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  TRIG_CTRL_FORCE; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg [11:0] TRIG_CTRL2_PRE_LEN; // @[AxiLiteSubordinateGenerator.scala 349:18]
  reg [15:0] TRIG_FILT_HI_MARK; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg [12:0] TRIG_FILT_LEVEL; // @[AxiLiteSubordinateGenerator.scala 349:18]
  reg [31:0] MEM_DATA_DIN; // @[AxiLiteSubordinateGenerator.scala 359:45]
  reg [11:0] MEM_DATA_ADDR; // @[AxiLiteSubordinateGenerator.scala 363:46]
  reg  MEM_DATA_WE; // @[AxiLiteSubordinateGenerator.scala 365:48]
  reg  MEM_DATA_ACT1; // @[AxiLiteSubordinateGenerator.scala 368:29]
  reg  MEM_DATA_ACT2; // @[AxiLiteSubordinateGenerator.scala 370:50]
  reg [1:0] state_wr; // @[AxiLiteSubordinateGenerator.scala 63:25]
  reg  wr_en; // @[AxiLiteSubordinateGenerator.scala 65:18]
  reg [12:0] wr_addr; // @[AxiLiteSubordinateGenerator.scala 66:20]
  reg [31:0] wr_data; // @[AxiLiteSubordinateGenerator.scala 67:20]
  wire  _T = 2'h0 == state_wr; // @[Conditional.scala 37:30]
  wire  _T_1 = io_ctrl_AW_valid & io_ctrl_W_valid; // @[AxiLiteSubordinateGenerator.scala 76:29]
  wire [31:0] _GEN_0 = io_ctrl_W_valid ? io_ctrl_W_bits_wdata : wr_data; // @[AxiLiteSubordinateGenerator.scala 86:36 AxiLiteSubordinateGenerator.scala 87:19 AxiLiteSubordinateGenerator.scala 67:20]
  wire [1:0] _GEN_2 = io_ctrl_W_valid ? 2'h2 : state_wr; // @[AxiLiteSubordinateGenerator.scala 86:36 AxiLiteSubordinateGenerator.scala 89:20 AxiLiteSubordinateGenerator.scala 63:25]
  wire [12:0] _GEN_3 = io_ctrl_AW_valid ? io_ctrl_AW_bits_addr[14:2] : wr_addr; // @[AxiLiteSubordinateGenerator.scala 82:36 AxiLiteSubordinateGenerator.scala 83:19 AxiLiteSubordinateGenerator.scala 66:20]
  wire  _T_4 = 2'h1 == state_wr; // @[Conditional.scala 37:30]
  wire  _T_5 = 2'h2 == state_wr; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_16 = io_ctrl_AW_valid ? 2'h3 : state_wr; // @[AxiLiteSubordinateGenerator.scala 101:30 AxiLiteSubordinateGenerator.scala 104:18 AxiLiteSubordinateGenerator.scala 63:25]
  wire  _T_7 = 2'h3 == state_wr; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_17 = io_ctrl_B_ready ? 2'h0 : state_wr; // @[AxiLiteSubordinateGenerator.scala 108:29 AxiLiteSubordinateGenerator.scala 109:18 AxiLiteSubordinateGenerator.scala 63:25]
  wire [1:0] _GEN_18 = _T_7 ? _GEN_17 : state_wr; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 63:25]
  wire  _GEN_19 = _T_5 & io_ctrl_AW_valid; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 72:9]
  wire  _GEN_36 = _T_4 ? 1'h0 : _T_7; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 134:23]
  wire  _GEN_38 = _T_5 ? 1'h0 : _T_4; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 128:23]
  wire  _GEN_39 = _T_5 ? 1'h0 : _GEN_36; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 129:23]
  wire  _GEN_44 = wr_addr == 13'h5 & wr_data[0]; // @[AxiLiteSubordinateGenerator.scala 493:46 AxiLiteSubordinateGenerator.scala 494:42 AxiLiteSubordinateGenerator.scala 470:40]
  wire  _GEN_48 = wr_addr == 13'h9 & wr_data[31]; // @[AxiLiteSubordinateGenerator.scala 493:46 AxiLiteSubordinateGenerator.scala 494:42 AxiLiteSubordinateGenerator.scala 470:40]
  wire  _T_22 = wr_addr >= 13'h400 & wr_addr < 13'h1400; // @[AxiLiteSubordinateGenerator.scala 505:32]
  wire [12:0] _MEM_DATA_ADDR_T_1 = wr_addr - 13'h400; // @[AxiLiteSubordinateGenerator.scala 507:54]
  wire [12:0] _GEN_52 = wr_addr >= 13'h400 & wr_addr < 13'h1400 ? _MEM_DATA_ADDR_T_1 : {{1'd0}, MEM_DATA_ADDR}; // @[AxiLiteSubordinateGenerator.scala 505:55 AxiLiteSubordinateGenerator.scala 507:43 AxiLiteSubordinateGenerator.scala 363:46]
  wire [12:0] _GEN_63 = wr_en ? _GEN_52 : {{1'd0}, MEM_DATA_ADDR}; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 363:46]
  wire  _GEN_64 = wr_en & _T_22; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 478:39]
  reg [1:0] state_rd; // @[AxiLiteSubordinateGenerator.scala 172:25]
  reg  rd_en; // @[AxiLiteSubordinateGenerator.scala 174:18]
  reg [12:0] rd_addr; // @[AxiLiteSubordinateGenerator.scala 175:20]
  reg [33:0] rd_data; // @[AxiLiteSubordinateGenerator.scala 176:20]
  wire  _T_23 = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_28 = io_ctrl_AR_bits_addr[14:2] >= 13'h400 & io_ctrl_AR_bits_addr[14:2] < 13'h1400; // @[AxiLiteSubordinateGenerator.scala 532:29]
  wire  _T_30 = 2'h1 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_31 = 2'h2 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_32 = 2'h3 == state_rd; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_70 = io_ctrl_R_ready ? 2'h0 : state_rd; // @[AxiLiteSubordinateGenerator.scala 200:29 AxiLiteSubordinateGenerator.scala 201:18 AxiLiteSubordinateGenerator.scala 172:25]
  wire [1:0] _GEN_71 = _T_32 ? _GEN_70 : state_rd; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 172:25]
  wire [33:0] _GEN_79 = _T_32 ? rd_data : 34'h0; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 229:28 AxiLiteSubordinateGenerator.scala 208:24]
  wire  _GEN_81 = _T_31 ? 1'h0 : _T_32; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 223:23]
  wire [33:0] _GEN_82 = _T_31 ? 34'h0 : _GEN_79; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 224:28]
  wire  _GEN_84 = _T_30 ? 1'h0 : _GEN_81; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 218:23]
  wire [33:0] _GEN_85 = _T_30 ? 34'h0 : _GEN_82; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 219:28]
  wire [33:0] _GEN_88 = _T_23 ? 34'h0 : _GEN_85; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 214:28]
  wire  _T_39 = rd_addr >= 13'h400 & rd_addr < 13'h1400; // @[AxiLiteSubordinateGenerator.scala 532:29]
  wire [31:0] _GEN_90 = rd_addr == 13'h0 ? 32'h5157311a : 32'hdeadbeef; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17 AxiLiteSubordinateGenerator.scala 435:13]
  wire [31:0] _GEN_91 = rd_addr == 13'h1 ? 32'h10303 : _GEN_90; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_2 = {SCRATCH_FIELD[7],SCRATCH_FIELD[6],SCRATCH_FIELD[5],SCRATCH_FIELD[4],SCRATCH_FIELD[3],
    SCRATCH_FIELD[2],SCRATCH_FIELD[1],SCRATCH_FIELD[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [15:0] lo_2 = {SCRATCH_FIELD[15],SCRATCH_FIELD[14],SCRATCH_FIELD[13],SCRATCH_FIELD[12],SCRATCH_FIELD[11],
    SCRATCH_FIELD[10],SCRATCH_FIELD[9],SCRATCH_FIELD[8],lo_lo_2}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [7:0] hi_lo_2 = {SCRATCH_FIELD[23],SCRATCH_FIELD[22],SCRATCH_FIELD[21],SCRATCH_FIELD[20],SCRATCH_FIELD[19],
    SCRATCH_FIELD[18],SCRATCH_FIELD[17],SCRATCH_FIELD[16]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_136 = {SCRATCH_FIELD[31],SCRATCH_FIELD[30],SCRATCH_FIELD[29],SCRATCH_FIELD[28],SCRATCH_FIELD[27],
    SCRATCH_FIELD[26],SCRATCH_FIELD[25],SCRATCH_FIELD[24],hi_lo_2,lo_2}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_92 = rd_addr == 13'h3 ? _T_136 : _GEN_91; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [31:0] _T_138 = {16'h0,8'h0,4'h0,2'h0,1'h0,STATUS_DONE}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_93 = rd_addr == 13'h4 ? _T_138 : _GEN_92; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [31:0] _T_140 = {16'h0,8'h0,4'h0,2'h0,1'h0,CONTROL_CLEAR}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_94 = rd_addr == 13'h5 ? _T_140 : _GEN_93; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_5 = {TRIG_CTRL_MASK[7],TRIG_CTRL_MASK[6],TRIG_CTRL_MASK[5],TRIG_CTRL_MASK[4],TRIG_CTRL_MASK[3],
    TRIG_CTRL_MASK[2],TRIG_CTRL_MASK[1],TRIG_CTRL_MASK[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [15:0] lo_5 = {TRIG_CTRL_MASK[15],TRIG_CTRL_MASK[14],TRIG_CTRL_MASK[13],TRIG_CTRL_MASK[12],TRIG_CTRL_MASK[11],
    TRIG_CTRL_MASK[10],TRIG_CTRL_MASK[9],TRIG_CTRL_MASK[8],lo_lo_5}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_159 = {TRIG_CTRL_FORCE,TRIG_CTRL_FILT_EN,TRIG_CTRL_CORR_EN,1'h0,4'h0,4'h0,2'h0,1'h0,TRIG_CTRL_MASK[16],
    lo_5}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_95 = rd_addr == 13'h9 ? _T_159 : _GEN_94; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_6 = {TRIG_CTRL2_PRE_LEN[7],TRIG_CTRL2_PRE_LEN[6],TRIG_CTRL2_PRE_LEN[5],TRIG_CTRL2_PRE_LEN[4],
    TRIG_CTRL2_PRE_LEN[3],TRIG_CTRL2_PRE_LEN[2],TRIG_CTRL2_PRE_LEN[1],TRIG_CTRL2_PRE_LEN[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_173 = {16'h0,4'h0,TRIG_CTRL2_PRE_LEN[11],TRIG_CTRL2_PRE_LEN[10],TRIG_CTRL2_PRE_LEN[9],
    TRIG_CTRL2_PRE_LEN[8],lo_lo_6}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_96 = rd_addr == 13'ha ? _T_173 : _GEN_95; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_7 = {TRIG_FILT_LEVEL[7],TRIG_FILT_LEVEL[6],TRIG_FILT_LEVEL[5],TRIG_FILT_LEVEL[4],TRIG_FILT_LEVEL[3],
    TRIG_FILT_LEVEL[2],TRIG_FILT_LEVEL[1],TRIG_FILT_LEVEL[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [15:0] lo_7 = {2'h0,1'h0,TRIG_FILT_LEVEL[12],TRIG_FILT_LEVEL[11],TRIG_FILT_LEVEL[10],TRIG_FILT_LEVEL[9],
    TRIG_FILT_LEVEL[8],lo_lo_7}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [7:0] hi_lo_7 = {TRIG_FILT_HI_MARK[7],TRIG_FILT_HI_MARK[6],TRIG_FILT_HI_MARK[5],TRIG_FILT_HI_MARK[4],
    TRIG_FILT_HI_MARK[3],TRIG_FILT_HI_MARK[2],TRIG_FILT_HI_MARK[1],TRIG_FILT_HI_MARK[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_204 = {TRIG_FILT_HI_MARK[15],TRIG_FILT_HI_MARK[14],TRIG_FILT_HI_MARK[13],TRIG_FILT_HI_MARK[12],
    TRIG_FILT_HI_MARK[11],TRIG_FILT_HI_MARK[10],TRIG_FILT_HI_MARK[9],TRIG_FILT_HI_MARK[8],hi_lo_7,lo_7}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_97 = rd_addr == 13'hb ? _T_204 : _GEN_96; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [12:0] _MEM_DATA_ADDR_T_3 = io_ctrl_AR_bits_addr[14:2] - 13'h400; // @[AxiLiteSubordinateGenerator.scala 456:54]
  wire [12:0] _GEN_99 = _T_28 ? _MEM_DATA_ADDR_T_3 : _GEN_63; // @[AxiLiteSubordinateGenerator.scala 455:55 AxiLiteSubordinateGenerator.scala 456:43]
  wire [12:0] _GEN_101 = state_rd == 2'h0 ? _GEN_99 : _GEN_63; // @[AxiLiteSubordinateGenerator.scala 238:30]
  wire  _GEN_102 = state_rd == 2'h0 & _T_28; // @[AxiLiteSubordinateGenerator.scala 238:30 AxiLiteSubordinateGenerator.scala 477:41]
  assign io_out_MEM_DATA_WE = MEM_DATA_WE; // @[AxiLiteSubordinateGenerator.scala 407:50]
  assign io_out_MEM_DATA_ADDR = MEM_DATA_ADDR; // @[AxiLiteSubordinateGenerator.scala 406:52]
  assign io_out_MEM_DATA_DIN = MEM_DATA_DIN; // @[AxiLiteSubordinateGenerator.scala 405:51]
  assign io_out_TRIG_FILT_LEVEL = TRIG_FILT_LEVEL; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_TRIG_CTRL_FORCE = TRIG_CTRL_FORCE; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_TRIG_CTRL_FILT_EN = TRIG_CTRL_FILT_EN; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_TRIG_CTRL_CORR_EN = TRIG_CTRL_CORR_EN; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_TRIG_CTRL_MASK = TRIG_CTRL_MASK; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_CONTROL_CLEAR = CONTROL_CLEAR; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_ctrl_AW_ready = _T | _T_5; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 122:24]
  assign io_ctrl_W_ready = _T | _GEN_38; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 123:23]
  assign io_ctrl_B_valid = _T ? 1'h0 : _GEN_39; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 124:23]
  assign io_ctrl_AR_ready = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  assign io_ctrl_R_valid = _T_23 ? 1'h0 : _GEN_84; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 213:23]
  assign io_ctrl_R_bits_rdata = _GEN_88[31:0];
  always @(posedge clock) begin
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'h3) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        SCRATCH_FIELD <= wr_data; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    STATUS_DONE <= io_inp_STATUS_DONE; // @[AxiLiteSubordinateGenerator.scala 382:40]
    CONTROL_CLEAR <= wr_en & _GEN_44; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 470:40]
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'h9) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_CTRL_MASK <= wr_data[16:0]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'h9) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_CTRL_CORR_EN <= wr_data[29]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'h9) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_CTRL_FILT_EN <= wr_data[30]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    TRIG_CTRL_FORCE <= wr_en & _GEN_48; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 470:40]
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 349:18]
      TRIG_CTRL2_PRE_LEN <= 12'h64; // @[AxiLiteSubordinateGenerator.scala 349:18]
    end else if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'ha) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_CTRL2_PRE_LEN <= wr_data[11:0]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    TRIG_FILT_HI_MARK <= io_inp_TRIG_FILT_HI_MARK; // @[AxiLiteSubordinateGenerator.scala 382:40]
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 349:18]
      TRIG_FILT_LEVEL <= 13'h1f4; // @[AxiLiteSubordinateGenerator.scala 349:18]
    end else if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 13'hb) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_FILT_LEVEL <= wr_data[12:0]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr >= 13'h400 & wr_addr < 13'h1400) begin // @[AxiLiteSubordinateGenerator.scala 505:55]
        MEM_DATA_DIN <= wr_data; // @[AxiLiteSubordinateGenerator.scala 506:42]
      end
    end
    MEM_DATA_ADDR <= _GEN_101[11:0];
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 365:48]
      MEM_DATA_WE <= 1'h0; // @[AxiLiteSubordinateGenerator.scala 365:48]
    end else begin
      MEM_DATA_WE <= _GEN_64;
    end
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 368:29]
      MEM_DATA_ACT1 <= 1'h0; // @[AxiLiteSubordinateGenerator.scala 368:29]
    end else begin
      MEM_DATA_ACT1 <= _GEN_102;
    end
    MEM_DATA_ACT2 <= MEM_DATA_ACT1; // @[AxiLiteSubordinateGenerator.scala 370:50]
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 63:25]
      state_wr <= 2'h0; // @[AxiLiteSubordinateGenerator.scala 63:25]
    end else if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[AxiLiteSubordinateGenerator.scala 76:49]
        state_wr <= 2'h3; // @[AxiLiteSubordinateGenerator.scala 81:18]
      end else if (io_ctrl_AW_valid) begin // @[AxiLiteSubordinateGenerator.scala 82:36]
        state_wr <= 2'h1; // @[AxiLiteSubordinateGenerator.scala 84:20]
      end else begin
        state_wr <= _GEN_2;
      end
    end else if (_T_4) begin // @[Conditional.scala 39:67]
      if (io_ctrl_W_valid) begin // @[AxiLiteSubordinateGenerator.scala 93:29]
        state_wr <= 2'h3; // @[AxiLiteSubordinateGenerator.scala 97:18]
      end
    end else if (_T_5) begin // @[Conditional.scala 39:67]
      state_wr <= _GEN_16;
    end else begin
      state_wr <= _GEN_18;
    end
    if (_T) begin // @[Conditional.scala 40:58]
      wr_en <= _T_1;
    end else if (_T_4) begin // @[Conditional.scala 39:67]
      wr_en <= io_ctrl_W_valid;
    end else begin
      wr_en <= _GEN_19;
    end
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[AxiLiteSubordinateGenerator.scala 76:49]
        wr_addr <= io_ctrl_AW_bits_addr[14:2]; // @[AxiLiteSubordinateGenerator.scala 78:17]
      end else begin
        wr_addr <= _GEN_3;
      end
    end else if (!(_T_4)) begin // @[Conditional.scala 39:67]
      if (_T_5) begin // @[Conditional.scala 39:67]
        wr_addr <= _GEN_3;
      end
    end
    if (_T) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AW_valid & io_ctrl_W_valid) begin // @[AxiLiteSubordinateGenerator.scala 76:49]
        wr_data <= io_ctrl_W_bits_wdata; // @[AxiLiteSubordinateGenerator.scala 79:17]
      end else if (!(io_ctrl_AW_valid)) begin // @[AxiLiteSubordinateGenerator.scala 82:36]
        wr_data <= _GEN_0;
      end
    end else if (_T_4) begin // @[Conditional.scala 39:67]
      wr_data <= _GEN_0;
    end
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 172:25]
      state_rd <= 2'h0; // @[AxiLiteSubordinateGenerator.scala 172:25]
    end else if (_T_23) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[AxiLiteSubordinateGenerator.scala 183:30]
        if (_T_28) begin // @[AxiLiteSubordinateGenerator.scala 186:83]
          state_rd <= 2'h1; // @[AxiLiteSubordinateGenerator.scala 187:20]
        end else begin
          state_rd <= 2'h2; // @[AxiLiteSubordinateGenerator.scala 189:20]
        end
      end
    end else if (_T_30) begin // @[Conditional.scala 39:67]
      state_rd <= 2'h2; // @[AxiLiteSubordinateGenerator.scala 194:16]
    end else if (_T_31) begin // @[Conditional.scala 39:67]
      state_rd <= 2'h3; // @[AxiLiteSubordinateGenerator.scala 197:16]
    end else begin
      state_rd <= _GEN_71;
    end
    rd_en <= _T_23 & io_ctrl_AR_valid; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 179:9]
    if (_T_23) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[AxiLiteSubordinateGenerator.scala 183:30]
        rd_addr <= io_ctrl_AR_bits_addr[14:2]; // @[AxiLiteSubordinateGenerator.scala 185:17]
      end
    end
    if (MEM_DATA_ACT2) begin // @[AxiLiteSubordinateGenerator.scala 517:65]
      rd_data <= {{2'd0}, io_inp_MEM_DATA_DOUT}; // @[AxiLiteSubordinateGenerator.scala 518:17]
    end else if (rd_en & ~_T_39) begin // @[AxiLiteSubordinateGenerator.scala 234:59]
      rd_data <= {{2'd0}, _GEN_97};
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
  SCRATCH_FIELD = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  STATUS_DONE = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  CONTROL_CLEAR = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  TRIG_CTRL_MASK = _RAND_3[16:0];
  _RAND_4 = {1{`RANDOM}};
  TRIG_CTRL_CORR_EN = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  TRIG_CTRL_FILT_EN = _RAND_5[0:0];
  _RAND_6 = {1{`RANDOM}};
  TRIG_CTRL_FORCE = _RAND_6[0:0];
  _RAND_7 = {1{`RANDOM}};
  TRIG_CTRL2_PRE_LEN = _RAND_7[11:0];
  _RAND_8 = {1{`RANDOM}};
  TRIG_FILT_HI_MARK = _RAND_8[15:0];
  _RAND_9 = {1{`RANDOM}};
  TRIG_FILT_LEVEL = _RAND_9[12:0];
  _RAND_10 = {1{`RANDOM}};
  MEM_DATA_DIN = _RAND_10[31:0];
  _RAND_11 = {1{`RANDOM}};
  MEM_DATA_ADDR = _RAND_11[11:0];
  _RAND_12 = {1{`RANDOM}};
  MEM_DATA_WE = _RAND_12[0:0];
  _RAND_13 = {1{`RANDOM}};
  MEM_DATA_ACT1 = _RAND_13[0:0];
  _RAND_14 = {1{`RANDOM}};
  MEM_DATA_ACT2 = _RAND_14[0:0];
  _RAND_15 = {1{`RANDOM}};
  state_wr = _RAND_15[1:0];
  _RAND_16 = {1{`RANDOM}};
  wr_en = _RAND_16[0:0];
  _RAND_17 = {1{`RANDOM}};
  wr_addr = _RAND_17[12:0];
  _RAND_18 = {1{`RANDOM}};
  wr_data = _RAND_18[31:0];
  _RAND_19 = {1{`RANDOM}};
  state_rd = _RAND_19[1:0];
  _RAND_20 = {1{`RANDOM}};
  rd_en = _RAND_20[0:0];
  _RAND_21 = {1{`RANDOM}};
  rd_addr = _RAND_21[12:0];
  _RAND_22 = {2{`RANDOM}};
  rd_data = _RAND_22[33:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module FilterTrigger(
  input         clock,
  input         reset,
  input         io_data_in,
  input  [11:0] io_level,
  output        io_trigger_out,
  output [11:0] io_debug_q
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
`endif // RANDOMIZE_REG_INIT
  reg [19:0] q; // @[FilterTrigger.scala 14:18]
  reg  data_in_reg; // @[FilterTrigger.scala 19:28]
  reg  data_in_reg_p; // @[FilterTrigger.scala 20:30]
  wire  data_edge = data_in_reg ^ data_in_reg_p; // @[FilterTrigger.scala 22:40]
  wire [19:0] _T_1 = 20'hfffff - 20'hc8; // @[FilterTrigger.scala 24:34]
  wire [19:0] _q_T_1 = q + 20'hc8; // @[FilterTrigger.scala 25:12]
  wire [19:0] _q_T_3 = q - 20'h1; // @[FilterTrigger.scala 27:12]
  wire [12:0] _GEN_3 = {{1'd0}, io_level}; // @[FilterTrigger.scala 30:35]
  reg  trig_out; // @[FilterTrigger.scala 30:25]
  reg  trig_out_p; // @[FilterTrigger.scala 31:27]
  assign io_trigger_out = ~trig_out_p & trig_out; // @[FilterTrigger.scala 33:21]
  assign io_debug_q = q[18:7]; // @[FilterTrigger.scala 38:14]
  always @(posedge clock) begin
    if (reset) begin // @[FilterTrigger.scala 14:18]
      q <= 20'h0; // @[FilterTrigger.scala 14:18]
    end else if (data_edge & q <= _T_1) begin // @[FilterTrigger.scala 24:45]
      q <= _q_T_1; // @[FilterTrigger.scala 25:7]
    end else if (~data_edge & q >= 20'h1) begin // @[FilterTrigger.scala 26:43]
      q <= _q_T_3; // @[FilterTrigger.scala 27:7]
    end
    data_in_reg <= io_data_in; // @[FilterTrigger.scala 19:28]
    data_in_reg_p <= data_in_reg; // @[FilterTrigger.scala 20:30]
    trig_out <= q[19:7] > _GEN_3; // @[FilterTrigger.scala 30:35]
    trig_out_p <= trig_out; // @[FilterTrigger.scala 31:27]
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
  q = _RAND_0[19:0];
  _RAND_1 = {1{`RANDOM}};
  data_in_reg = _RAND_1[0:0];
  _RAND_2 = {1{`RANDOM}};
  data_in_reg_p = _RAND_2[0:0];
  _RAND_3 = {1{`RANDOM}};
  trig_out = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  trig_out_p = _RAND_4[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module CorrelatorChannel(
  input        clock,
  input        reset,
  input        io_inp,
  output [8:0] io_out,
  output       io_out_valid
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [8:0] pos; // @[Correlator.scala 16:20]
  reg [8:0] accum; // @[Correlator.scala 17:22]
  reg [8:0] out; // @[Correlator.scala 18:20]
  reg  out_valid; // @[Correlator.scala 19:26]
  wire  _T = pos == 9'h59; // @[Correlator.scala 24:13]
  wire [8:0] _pos_T_1 = pos + 9'h1; // @[Correlator.scala 30:16]
  wire [89:0] _T_1 = 90'h3ff00000003ff00000003ff >> pos; // @[Correlator.scala 35:27]
  wire [1:0] _GEN_3 = io_inp ? 2'h2 : 2'h1; // @[Correlator.scala 36:30 Correlator.scala 37:21 Correlator.scala 39:21]
  wire [1:0] inp_pat_match = io_inp == _T_1[0] ? _GEN_3 : 2'h0; // @[Correlator.scala 35:34 Correlator.scala 34:17]
  wire [8:0] _GEN_6 = {{7'd0}, inp_pat_match}; // @[Correlator.scala 46:20]
  wire [8:0] _accum_T_1 = accum + _GEN_6; // @[Correlator.scala 46:20]
  assign io_out = out; // @[Correlator.scala 21:10]
  assign io_out_valid = out_valid; // @[Correlator.scala 22:16]
  always @(posedge clock) begin
    if (reset) begin // @[Correlator.scala 16:20]
      pos <= 9'h0; // @[Correlator.scala 16:20]
    end else if (pos == 9'h59) begin // @[Correlator.scala 24:41]
      pos <= 9'h0; // @[Correlator.scala 27:9]
    end else begin
      pos <= _pos_T_1; // @[Correlator.scala 30:9]
    end
    if (reset) begin // @[Correlator.scala 17:22]
      accum <= 9'h0; // @[Correlator.scala 17:22]
    end else if (out_valid) begin // @[Correlator.scala 43:20]
      accum <= {{7'd0}, inp_pat_match}; // @[Correlator.scala 44:11]
    end else begin
      accum <= _accum_T_1; // @[Correlator.scala 46:11]
    end
    if (reset) begin // @[Correlator.scala 18:20]
      out <= 9'h0; // @[Correlator.scala 18:20]
    end else if (pos == 9'h59) begin // @[Correlator.scala 24:41]
      out <= accum; // @[Correlator.scala 25:9]
    end
    if (reset) begin // @[Correlator.scala 19:26]
      out_valid <= 1'h0; // @[Correlator.scala 19:26]
    end else begin
      out_valid <= _T;
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
  pos = _RAND_0[8:0];
  _RAND_1 = {1{`RANDOM}};
  accum = _RAND_1[8:0];
  _RAND_2 = {1{`RANDOM}};
  out = _RAND_2[8:0];
  _RAND_3 = {1{`RANDOM}};
  out_valid = _RAND_3[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module PoorMansSystemILAKernel(
  input         clock,
  input         reset,
  input         io_clear,
  output        io_done,
  input  [16:0] io_trigger_mask,
  input         io_trigger_force,
  input         io_trigger_filt_en,
  input  [11:0] io_trigger_filt_level,
  input         io_trigger_corr_en,
  output        io_trigger,
  output [11:0] io_status_filt_high_mark,
  input         io_MBDEBUG_TDI,
  input         io_MBDEBUG_TDO,
  input         io_MBDEBUG_CLK,
  input  [7:0]  io_MBDEBUG_REG_EN,
  input         io_MBDEBUG_SHIFT,
  input         io_MBDEBUG_CAPTURE,
  input         io_MBDEBUG_UPDATE,
  input         io_MBDEBUG_RST,
  input         io_MBDEBUG_DISABLE,
  input         io_DEBUG_SYS_RESET,
  output [31:0] io_dout,
  output [11:0] io_addr,
  output        io_we
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
`endif // RANDOMIZE_REG_INIT
  wire  mod_filt_clock; // @[PoorMansSystemILAKernel.scala 94:24]
  wire  mod_filt_reset; // @[PoorMansSystemILAKernel.scala 94:24]
  wire  mod_filt_io_data_in; // @[PoorMansSystemILAKernel.scala 94:24]
  wire [11:0] mod_filt_io_level; // @[PoorMansSystemILAKernel.scala 94:24]
  wire  mod_filt_io_trigger_out; // @[PoorMansSystemILAKernel.scala 94:24]
  wire [11:0] mod_filt_io_debug_q; // @[PoorMansSystemILAKernel.scala 94:24]
  wire  mod_corr_ch_clock; // @[PoorMansSystemILAKernel.scala 100:27]
  wire  mod_corr_ch_reset; // @[PoorMansSystemILAKernel.scala 100:27]
  wire  mod_corr_ch_io_inp; // @[PoorMansSystemILAKernel.scala 100:27]
  wire [8:0] mod_corr_ch_io_out; // @[PoorMansSystemILAKernel.scala 100:27]
  wire  mod_corr_ch_io_out_valid; // @[PoorMansSystemILAKernel.scala 100:27]
  reg [11:0] addr; // @[PoorMansSystemILAKernel.scala 56:17]
  reg [11:0] addr_last; // @[PoorMansSystemILAKernel.scala 57:22]
  reg [1:0] state; // @[PoorMansSystemILAKernel.scala 64:22]
  wire  _T_2 = 2'h0 == state; // @[Conditional.scala 37:30]
  wire  _T_5 = 2'h1 == state; // @[Conditional.scala 37:30]
  wire [11:0] _addr_T_1 = addr + 12'h1; // @[PoorMansSystemILAKernel.scala 74:20]
  wire [11:0] _addr_last_T_1 = addr - 12'h64; // @[PoorMansSystemILAKernel.scala 77:27]
  reg [15:0] mbdebug_prev; // @[PoorMansSystemILAKernel.scala 104:29]
  wire [11:0] mbdebug_edge_hi = {io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,io_MBDEBUG_SHIFT}; // @[PoorMansSystemILAKernel.scala 105:63]
  wire [3:0] mbdebug_edge_lo = {io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[PoorMansSystemILAKernel.scala 105:63]
  wire [15:0] _mbdebug_edge_T = {io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,io_MBDEBUG_SHIFT,
    io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[PoorMansSystemILAKernel.scala 105:63]
  wire [15:0] mbdebug_edge = mbdebug_prev ^ _mbdebug_edge_T; // @[PoorMansSystemILAKernel.scala 105:44]
  wire [16:0] _GEN_16 = {{1'd0}, mbdebug_edge}; // @[PoorMansSystemILAKernel.scala 106:29]
  wire [16:0] _trigger_T = _GEN_16 & io_trigger_mask; // @[PoorMansSystemILAKernel.scala 106:29]
  wire  _trigger_T_2 = _trigger_T != 17'h0 | io_trigger_force; // @[PoorMansSystemILAKernel.scala 106:57]
  wire  _trigger_T_3 = io_trigger_filt_en & mod_filt_io_trigger_out; // @[PoorMansSystemILAKernel.scala 108:25]
  wire  _trigger_T_4 = _trigger_T_2 | _trigger_T_3; // @[PoorMansSystemILAKernel.scala 107:22]
  wire  _trigger_T_7 = io_trigger_corr_en & mod_corr_ch_io_out_valid & mod_corr_ch_io_out > 9'h50; // @[PoorMansSystemILAKernel.scala 109:53]
  wire  trigger = _trigger_T_4 | _trigger_T_7; // @[PoorMansSystemILAKernel.scala 108:53]
  wire  _T_8 = 2'h2 == state; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_4 = addr == addr_last ? 2'h3 : state; // @[PoorMansSystemILAKernel.scala 82:32 PoorMansSystemILAKernel.scala 83:15 PoorMansSystemILAKernel.scala 64:22]
  wire  _T_12 = 2'h3 == state; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_5 = io_clear ? 2'h0 : state; // @[PoorMansSystemILAKernel.scala 87:22 PoorMansSystemILAKernel.scala 88:15 PoorMansSystemILAKernel.scala 64:22]
  wire [1:0] _GEN_6 = _T_12 ? _GEN_5 : state; // @[Conditional.scala 39:67 PoorMansSystemILAKernel.scala 64:22]
  wire  _io_trigger_T = state == 2'h1; // @[PoorMansSystemILAKernel.scala 110:34]
  reg [11:0] cntr; // @[PoorMansSystemILAKernel.scala 113:17]
  wire [16:0] out_data_lo_1 = {io_DEBUG_SYS_RESET,io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,
    io_MBDEBUG_SHIFT,io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[Cat.scala 30:58]
  wire [14:0] out_data_hi_1 = {cntr,trigger,state}; // @[Cat.scala 30:58]
  reg [11:0] status_filt_high_mark_reg; // @[PoorMansSystemILAKernel.scala 129:42]
  FilterTrigger mod_filt ( // @[PoorMansSystemILAKernel.scala 94:24]
    .clock(mod_filt_clock),
    .reset(mod_filt_reset),
    .io_data_in(mod_filt_io_data_in),
    .io_level(mod_filt_io_level),
    .io_trigger_out(mod_filt_io_trigger_out),
    .io_debug_q(mod_filt_io_debug_q)
  );
  CorrelatorChannel mod_corr_ch ( // @[PoorMansSystemILAKernel.scala 100:27]
    .clock(mod_corr_ch_clock),
    .reset(mod_corr_ch_reset),
    .io_inp(mod_corr_ch_io_inp),
    .io_out(mod_corr_ch_io_out),
    .io_out_valid(mod_corr_ch_io_out_valid)
  );
  assign io_done = state == 2'h3; // @[PoorMansSystemILAKernel.scala 127:20]
  assign io_trigger = trigger & state == 2'h1; // @[PoorMansSystemILAKernel.scala 110:25]
  assign io_status_filt_high_mark = status_filt_high_mark_reg; // @[PoorMansSystemILAKernel.scala 133:28]
  assign io_dout = {out_data_hi_1,out_data_lo_1}; // @[Cat.scala 30:58]
  assign io_addr = addr; // @[PoorMansSystemILAKernel.scala 125:11]
  assign io_we = _io_trigger_T | state == 2'h2; // @[PoorMansSystemILAKernel.scala 126:42]
  assign mod_filt_clock = clock;
  assign mod_filt_reset = reset;
  assign mod_filt_io_data_in = io_MBDEBUG_TDO; // @[PoorMansSystemILAKernel.scala 95:23]
  assign mod_filt_io_level = io_trigger_filt_level; // @[PoorMansSystemILAKernel.scala 96:21]
  assign mod_corr_ch_clock = clock;
  assign mod_corr_ch_reset = reset;
  assign mod_corr_ch_io_inp = io_MBDEBUG_TDO; // @[PoorMansSystemILAKernel.scala 101:22]
  always @(posedge clock) begin
    if (_T_2) begin // @[Conditional.scala 40:58]
      addr <= 12'h0;
    end else if (_T_5) begin // @[Conditional.scala 39:67]
      addr <= _addr_T_1; // @[PoorMansSystemILAKernel.scala 74:12]
    end else if (_T_8) begin // @[Conditional.scala 39:67]
      addr <= _addr_T_1; // @[PoorMansSystemILAKernel.scala 81:12]
    end
    if (!(_T_2)) begin // @[Conditional.scala 40:58]
      if (_T_5) begin // @[Conditional.scala 39:67]
        if (trigger) begin // @[PoorMansSystemILAKernel.scala 75:21]
          addr_last <= _addr_last_T_1; // @[PoorMansSystemILAKernel.scala 77:19]
        end
      end
    end
    if (reset) begin // @[PoorMansSystemILAKernel.scala 64:22]
      state <= 2'h0; // @[PoorMansSystemILAKernel.scala 64:22]
    end else if (_T_2) begin // @[Conditional.scala 40:58]
      state <= 2'h1;
    end else if (_T_5) begin // @[Conditional.scala 39:67]
      if (trigger) begin // @[PoorMansSystemILAKernel.scala 75:21]
        state <= 2'h2; // @[PoorMansSystemILAKernel.scala 76:15]
      end
    end else if (_T_8) begin // @[Conditional.scala 39:67]
      state <= _GEN_4;
    end else begin
      state <= _GEN_6;
    end
    mbdebug_prev <= {mbdebug_edge_hi,mbdebug_edge_lo}; // @[PoorMansSystemILAKernel.scala 104:47]
    cntr <= cntr + 12'h1; // @[PoorMansSystemILAKernel.scala 114:16]
    if (reset) begin // @[PoorMansSystemILAKernel.scala 129:42]
      status_filt_high_mark_reg <= 12'h0; // @[PoorMansSystemILAKernel.scala 129:42]
    end else if (mod_filt_io_debug_q > status_filt_high_mark_reg) begin // @[PoorMansSystemILAKernel.scala 130:58]
      status_filt_high_mark_reg <= mod_filt_io_debug_q; // @[PoorMansSystemILAKernel.scala 131:31]
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
  addr = _RAND_0[11:0];
  _RAND_1 = {1{`RANDOM}};
  addr_last = _RAND_1[11:0];
  _RAND_2 = {1{`RANDOM}};
  state = _RAND_2[1:0];
  _RAND_3 = {1{`RANDOM}};
  mbdebug_prev = _RAND_3[15:0];
  _RAND_4 = {1{`RANDOM}};
  cntr = _RAND_4[11:0];
  _RAND_5 = {1{`RANDOM}};
  status_filt_high_mark_reg = _RAND_5[11:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
endmodule
module PoorMansSystemILA(
  input         clock,
  input         reset,
  output        io_ctrl_AW_ready,
  input         io_ctrl_AW_valid,
  input  [14:0] io_ctrl_AW_bits_addr,
  input  [2:0]  io_ctrl_AW_bits_prot,
  output        io_ctrl_W_ready,
  input         io_ctrl_W_valid,
  input  [31:0] io_ctrl_W_bits_wdata,
  input  [3:0]  io_ctrl_W_bits_wstrb,
  input         io_ctrl_B_ready,
  output        io_ctrl_B_valid,
  output [1:0]  io_ctrl_B_bits,
  output        io_ctrl_AR_ready,
  input         io_ctrl_AR_valid,
  input  [14:0] io_ctrl_AR_bits_addr,
  input  [2:0]  io_ctrl_AR_bits_prot,
  input         io_ctrl_R_ready,
  output        io_ctrl_R_valid,
  output [31:0] io_ctrl_R_bits_rdata,
  output [1:0]  io_ctrl_R_bits_rresp,
  input         io_MBDEBUG_TDI,
  input         io_MBDEBUG_TDO,
  input         io_MBDEBUG_CLK,
  input  [7:0]  io_MBDEBUG_REG_EN,
  input         io_MBDEBUG_SHIFT,
  input         io_MBDEBUG_CAPTURE,
  input         io_MBDEBUG_UPDATE,
  input         io_MBDEBUG_RST,
  input         io_MBDEBUG_DISABLE,
  input         io_DEBUG_SYS_RESET,
  output        io_int_req,
  output        io_trigger_out
);
  wire  mod_ctrl_clock; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_reset; // @[PoorMansSystemILA.scala 81:24]
  wire [31:0] mod_ctrl_io_inp_MEM_DATA_DOUT; // @[PoorMansSystemILA.scala 81:24]
  wire [15:0] mod_ctrl_io_inp_TRIG_FILT_HI_MARK; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_inp_STATUS_DONE; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_out_MEM_DATA_WE; // @[PoorMansSystemILA.scala 81:24]
  wire [11:0] mod_ctrl_io_out_MEM_DATA_ADDR; // @[PoorMansSystemILA.scala 81:24]
  wire [31:0] mod_ctrl_io_out_MEM_DATA_DIN; // @[PoorMansSystemILA.scala 81:24]
  wire [12:0] mod_ctrl_io_out_TRIG_FILT_LEVEL; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_out_TRIG_CTRL_FORCE; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_out_TRIG_CTRL_FILT_EN; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_out_TRIG_CTRL_CORR_EN; // @[PoorMansSystemILA.scala 81:24]
  wire [16:0] mod_ctrl_io_out_TRIG_CTRL_MASK; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_out_CONTROL_CLEAR; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_AW_ready; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_AW_valid; // @[PoorMansSystemILA.scala 81:24]
  wire [14:0] mod_ctrl_io_ctrl_AW_bits_addr; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_W_ready; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_W_valid; // @[PoorMansSystemILA.scala 81:24]
  wire [31:0] mod_ctrl_io_ctrl_W_bits_wdata; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_B_ready; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_B_valid; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_AR_ready; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_AR_valid; // @[PoorMansSystemILA.scala 81:24]
  wire [14:0] mod_ctrl_io_ctrl_AR_bits_addr; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_R_ready; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_ctrl_io_ctrl_R_valid; // @[PoorMansSystemILA.scala 81:24]
  wire [31:0] mod_ctrl_io_ctrl_R_bits_rdata; // @[PoorMansSystemILA.scala 81:24]
  wire  mod_mem_clk; // @[PoorMansSystemILA.scala 90:23]
  wire [11:0] mod_mem_addra; // @[PoorMansSystemILA.scala 90:23]
  wire [31:0] mod_mem_dina; // @[PoorMansSystemILA.scala 90:23]
  wire [31:0] mod_mem_douta; // @[PoorMansSystemILA.scala 90:23]
  wire  mod_mem_wea; // @[PoorMansSystemILA.scala 90:23]
  wire [11:0] mod_mem_addrb; // @[PoorMansSystemILA.scala 90:23]
  wire [31:0] mod_mem_dinb; // @[PoorMansSystemILA.scala 90:23]
  wire [31:0] mod_mem_doutb; // @[PoorMansSystemILA.scala 90:23]
  wire  mod_mem_web; // @[PoorMansSystemILA.scala 90:23]
  wire  mod_kernel_clock; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_reset; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_clear; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_done; // @[PoorMansSystemILA.scala 97:26]
  wire [16:0] mod_kernel_io_trigger_mask; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_trigger_force; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_trigger_filt_en; // @[PoorMansSystemILA.scala 97:26]
  wire [11:0] mod_kernel_io_trigger_filt_level; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_trigger_corr_en; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_trigger; // @[PoorMansSystemILA.scala 97:26]
  wire [11:0] mod_kernel_io_status_filt_high_mark; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_TDI; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_TDO; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_CLK; // @[PoorMansSystemILA.scala 97:26]
  wire [7:0] mod_kernel_io_MBDEBUG_REG_EN; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_SHIFT; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_CAPTURE; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_UPDATE; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_RST; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_MBDEBUG_DISABLE; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_DEBUG_SYS_RESET; // @[PoorMansSystemILA.scala 97:26]
  wire [31:0] mod_kernel_io_dout; // @[PoorMansSystemILA.scala 97:26]
  wire [11:0] mod_kernel_io_addr; // @[PoorMansSystemILA.scala 97:26]
  wire  mod_kernel_io_we; // @[PoorMansSystemILA.scala 97:26]
  AxiLiteSubordinateGenerator mod_ctrl ( // @[PoorMansSystemILA.scala 81:24]
    .clock(mod_ctrl_clock),
    .reset(mod_ctrl_reset),
    .io_inp_MEM_DATA_DOUT(mod_ctrl_io_inp_MEM_DATA_DOUT),
    .io_inp_TRIG_FILT_HI_MARK(mod_ctrl_io_inp_TRIG_FILT_HI_MARK),
    .io_inp_STATUS_DONE(mod_ctrl_io_inp_STATUS_DONE),
    .io_out_MEM_DATA_WE(mod_ctrl_io_out_MEM_DATA_WE),
    .io_out_MEM_DATA_ADDR(mod_ctrl_io_out_MEM_DATA_ADDR),
    .io_out_MEM_DATA_DIN(mod_ctrl_io_out_MEM_DATA_DIN),
    .io_out_TRIG_FILT_LEVEL(mod_ctrl_io_out_TRIG_FILT_LEVEL),
    .io_out_TRIG_CTRL_FORCE(mod_ctrl_io_out_TRIG_CTRL_FORCE),
    .io_out_TRIG_CTRL_FILT_EN(mod_ctrl_io_out_TRIG_CTRL_FILT_EN),
    .io_out_TRIG_CTRL_CORR_EN(mod_ctrl_io_out_TRIG_CTRL_CORR_EN),
    .io_out_TRIG_CTRL_MASK(mod_ctrl_io_out_TRIG_CTRL_MASK),
    .io_out_CONTROL_CLEAR(mod_ctrl_io_out_CONTROL_CLEAR),
    .io_ctrl_AW_ready(mod_ctrl_io_ctrl_AW_ready),
    .io_ctrl_AW_valid(mod_ctrl_io_ctrl_AW_valid),
    .io_ctrl_AW_bits_addr(mod_ctrl_io_ctrl_AW_bits_addr),
    .io_ctrl_W_ready(mod_ctrl_io_ctrl_W_ready),
    .io_ctrl_W_valid(mod_ctrl_io_ctrl_W_valid),
    .io_ctrl_W_bits_wdata(mod_ctrl_io_ctrl_W_bits_wdata),
    .io_ctrl_B_ready(mod_ctrl_io_ctrl_B_ready),
    .io_ctrl_B_valid(mod_ctrl_io_ctrl_B_valid),
    .io_ctrl_AR_ready(mod_ctrl_io_ctrl_AR_ready),
    .io_ctrl_AR_valid(mod_ctrl_io_ctrl_AR_valid),
    .io_ctrl_AR_bits_addr(mod_ctrl_io_ctrl_AR_bits_addr),
    .io_ctrl_R_ready(mod_ctrl_io_ctrl_R_ready),
    .io_ctrl_R_valid(mod_ctrl_io_ctrl_R_valid),
    .io_ctrl_R_bits_rdata(mod_ctrl_io_ctrl_R_bits_rdata)
  );
  DualPortRam #(.RAM_WIDTH(32), .RAM_DEPTH(4096)) mod_mem ( // @[PoorMansSystemILA.scala 90:23]
    .clk(mod_mem_clk),
    .addra(mod_mem_addra),
    .dina(mod_mem_dina),
    .douta(mod_mem_douta),
    .wea(mod_mem_wea),
    .addrb(mod_mem_addrb),
    .dinb(mod_mem_dinb),
    .doutb(mod_mem_doutb),
    .web(mod_mem_web)
  );
  PoorMansSystemILAKernel mod_kernel ( // @[PoorMansSystemILA.scala 97:26]
    .clock(mod_kernel_clock),
    .reset(mod_kernel_reset),
    .io_clear(mod_kernel_io_clear),
    .io_done(mod_kernel_io_done),
    .io_trigger_mask(mod_kernel_io_trigger_mask),
    .io_trigger_force(mod_kernel_io_trigger_force),
    .io_trigger_filt_en(mod_kernel_io_trigger_filt_en),
    .io_trigger_filt_level(mod_kernel_io_trigger_filt_level),
    .io_trigger_corr_en(mod_kernel_io_trigger_corr_en),
    .io_trigger(mod_kernel_io_trigger),
    .io_status_filt_high_mark(mod_kernel_io_status_filt_high_mark),
    .io_MBDEBUG_TDI(mod_kernel_io_MBDEBUG_TDI),
    .io_MBDEBUG_TDO(mod_kernel_io_MBDEBUG_TDO),
    .io_MBDEBUG_CLK(mod_kernel_io_MBDEBUG_CLK),
    .io_MBDEBUG_REG_EN(mod_kernel_io_MBDEBUG_REG_EN),
    .io_MBDEBUG_SHIFT(mod_kernel_io_MBDEBUG_SHIFT),
    .io_MBDEBUG_CAPTURE(mod_kernel_io_MBDEBUG_CAPTURE),
    .io_MBDEBUG_UPDATE(mod_kernel_io_MBDEBUG_UPDATE),
    .io_MBDEBUG_RST(mod_kernel_io_MBDEBUG_RST),
    .io_MBDEBUG_DISABLE(mod_kernel_io_MBDEBUG_DISABLE),
    .io_DEBUG_SYS_RESET(mod_kernel_io_DEBUG_SYS_RESET),
    .io_dout(mod_kernel_io_dout),
    .io_addr(mod_kernel_io_addr),
    .io_we(mod_kernel_io_we)
  );
  assign io_ctrl_AW_ready = mod_ctrl_io_ctrl_AW_ready; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_W_ready = mod_ctrl_io_ctrl_W_ready; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_B_valid = mod_ctrl_io_ctrl_B_valid; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_B_bits = 2'h0; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_AR_ready = mod_ctrl_io_ctrl_AR_ready; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_R_valid = mod_ctrl_io_ctrl_R_valid; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_R_bits_rdata = mod_ctrl_io_ctrl_R_bits_rdata; // @[PoorMansSystemILA.scala 84:11]
  assign io_ctrl_R_bits_rresp = 2'h0; // @[PoorMansSystemILA.scala 84:11]
  assign io_int_req = mod_kernel_io_done; // @[PoorMansSystemILA.scala 117:14]
  assign io_trigger_out = mod_kernel_io_trigger; // @[PoorMansSystemILA.scala 118:18]
  assign mod_ctrl_clock = clock;
  assign mod_ctrl_reset = reset;
  assign mod_ctrl_io_inp_MEM_DATA_DOUT = mod_mem_douta; // @[PoorMansSystemILA.scala 95:36]
  assign mod_ctrl_io_inp_TRIG_FILT_HI_MARK = {{4'd0}, mod_kernel_io_status_filt_high_mark}; // @[PoorMansSystemILA.scala 107:40]
  assign mod_ctrl_io_inp_STATUS_DONE = mod_kernel_io_done; // @[PoorMansSystemILA.scala 111:34]
  assign mod_ctrl_io_ctrl_AW_valid = io_ctrl_AW_valid; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_AW_bits_addr = io_ctrl_AW_bits_addr; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_W_valid = io_ctrl_W_valid; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_W_bits_wdata = io_ctrl_W_bits_wdata; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_B_ready = io_ctrl_B_ready; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_AR_valid = io_ctrl_AR_valid; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_AR_bits_addr = io_ctrl_AR_bits_addr; // @[PoorMansSystemILA.scala 84:11]
  assign mod_ctrl_io_ctrl_R_ready = io_ctrl_R_ready; // @[PoorMansSystemILA.scala 84:11]
  assign mod_mem_clk = clock; // @[PoorMansSystemILA.scala 91:18]
  assign mod_mem_addra = mod_ctrl_io_out_MEM_DATA_ADDR; // @[PoorMansSystemILA.scala 92:20]
  assign mod_mem_dina = mod_ctrl_io_out_MEM_DATA_DIN; // @[PoorMansSystemILA.scala 93:19]
  assign mod_mem_wea = mod_ctrl_io_out_MEM_DATA_WE; // @[PoorMansSystemILA.scala 94:67]
  assign mod_mem_addrb = mod_kernel_io_addr; // @[PoorMansSystemILA.scala 113:20]
  assign mod_mem_dinb = mod_kernel_io_dout; // @[PoorMansSystemILA.scala 114:19]
  assign mod_mem_web = mod_kernel_io_we; // @[PoorMansSystemILA.scala 115:18]
  assign mod_kernel_clock = clock;
  assign mod_kernel_reset = reset;
  assign mod_kernel_io_clear = mod_ctrl_io_out_CONTROL_CLEAR; // @[PoorMansSystemILA.scala 110:23]
  assign mod_kernel_io_trigger_mask = mod_ctrl_io_out_TRIG_CTRL_MASK; // @[PoorMansSystemILA.scala 101:30]
  assign mod_kernel_io_trigger_force = mod_ctrl_io_out_TRIG_CTRL_FORCE; // @[PoorMansSystemILA.scala 102:31]
  assign mod_kernel_io_trigger_filt_en = mod_ctrl_io_out_TRIG_CTRL_FILT_EN; // @[PoorMansSystemILA.scala 103:33]
  assign mod_kernel_io_trigger_filt_level = mod_ctrl_io_out_TRIG_FILT_LEVEL[11:0]; // @[PoorMansSystemILA.scala 104:36]
  assign mod_kernel_io_trigger_corr_en = mod_ctrl_io_out_TRIG_CTRL_CORR_EN; // @[PoorMansSystemILA.scala 105:33]
  assign mod_kernel_io_MBDEBUG_TDI = io_MBDEBUG_TDI; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_TDO = io_MBDEBUG_TDO; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_CLK = io_MBDEBUG_CLK; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_REG_EN = io_MBDEBUG_REG_EN; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_SHIFT = io_MBDEBUG_SHIFT; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_CAPTURE = io_MBDEBUG_CAPTURE; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_UPDATE = io_MBDEBUG_UPDATE; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_RST = io_MBDEBUG_RST; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_MBDEBUG_DISABLE = io_MBDEBUG_DISABLE; // @[PoorMansSystemILA.scala 98:25]
  assign mod_kernel_io_DEBUG_SYS_RESET = io_DEBUG_SYS_RESET; // @[PoorMansSystemILA.scala 99:33]
endmodule
