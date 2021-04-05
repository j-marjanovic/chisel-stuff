module AxiLiteSubordinateGenerator(
  input         clock,
  input         reset,
  input  [31:0] io_inp_MEM_DATA_DOUT,
  input         io_inp_STATUS_DONE,
  output        io_out_MEM_DATA_WE,
  output [11:0] io_out_MEM_DATA_ADDR,
  output [31:0] io_out_MEM_DATA_DIN,
  output        io_out_TRIG_CTRL_FORCE,
  output [9:0]  io_out_TRIG_CTRL_MASK,
  output        io_out_CONTROL_ENABLE,
  output        io_out_CONTROL_CLEAR,
  output        io_ctrl_AW_ready,
  input         io_ctrl_AW_valid,
  input  [15:0] io_ctrl_AW_bits_addr,
  output        io_ctrl_W_ready,
  input         io_ctrl_W_valid,
  input  [31:0] io_ctrl_W_bits_wdata,
  input         io_ctrl_B_ready,
  output        io_ctrl_B_valid,
  output        io_ctrl_AR_ready,
  input         io_ctrl_AR_valid,
  input  [15:0] io_ctrl_AR_bits_addr,
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
  reg [63:0] _RAND_18;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] SCRATCH_FIELD; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  STATUS_DONE; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  CONTROL_CLEAR; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  CONTROL_ENABLE; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg [9:0] TRIG_CTRL_MASK; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg  TRIG_CTRL_FORCE; // @[AxiLiteSubordinateGenerator.scala 351:14]
  reg [31:0] MEM_DATA_DIN; // @[AxiLiteSubordinateGenerator.scala 359:45]
  reg [11:0] MEM_DATA_ADDR; // @[AxiLiteSubordinateGenerator.scala 363:46]
  reg  MEM_DATA_WE; // @[AxiLiteSubordinateGenerator.scala 365:48]
  reg  MEM_DATA_ACT1; // @[AxiLiteSubordinateGenerator.scala 368:29]
  reg  MEM_DATA_ACT2; // @[AxiLiteSubordinateGenerator.scala 370:50]
  reg [1:0] state_wr; // @[AxiLiteSubordinateGenerator.scala 63:25]
  reg  wr_en; // @[AxiLiteSubordinateGenerator.scala 65:18]
  reg [13:0] wr_addr; // @[AxiLiteSubordinateGenerator.scala 66:20]
  reg [31:0] wr_data; // @[AxiLiteSubordinateGenerator.scala 67:20]
  wire  _T = 2'h0 == state_wr; // @[Conditional.scala 37:30]
  wire  _T_1 = io_ctrl_AW_valid & io_ctrl_W_valid; // @[AxiLiteSubordinateGenerator.scala 76:29]
  wire [31:0] _GEN_0 = io_ctrl_W_valid ? io_ctrl_W_bits_wdata : wr_data; // @[AxiLiteSubordinateGenerator.scala 86:36 AxiLiteSubordinateGenerator.scala 87:19 AxiLiteSubordinateGenerator.scala 67:20]
  wire [1:0] _GEN_2 = io_ctrl_W_valid ? 2'h2 : state_wr; // @[AxiLiteSubordinateGenerator.scala 86:36 AxiLiteSubordinateGenerator.scala 89:20 AxiLiteSubordinateGenerator.scala 63:25]
  wire [13:0] _GEN_3 = io_ctrl_AW_valid ? io_ctrl_AW_bits_addr[15:2] : wr_addr; // @[AxiLiteSubordinateGenerator.scala 82:36 AxiLiteSubordinateGenerator.scala 83:19 AxiLiteSubordinateGenerator.scala 66:20]
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
  wire  _GEN_44 = wr_addr == 14'h5 & wr_data[0]; // @[AxiLiteSubordinateGenerator.scala 493:46 AxiLiteSubordinateGenerator.scala 494:42 AxiLiteSubordinateGenerator.scala 470:40]
  wire  _GEN_47 = wr_addr == 14'h9 & wr_data[31]; // @[AxiLiteSubordinateGenerator.scala 493:46 AxiLiteSubordinateGenerator.scala 494:42 AxiLiteSubordinateGenerator.scala 470:40]
  wire  _T_19 = wr_addr >= 14'h400 & wr_addr < 14'h1400; // @[AxiLiteSubordinateGenerator.scala 505:32]
  wire [13:0] _MEM_DATA_ADDR_T_1 = wr_addr - 14'h400; // @[AxiLiteSubordinateGenerator.scala 507:54]
  wire [13:0] _GEN_49 = wr_addr >= 14'h400 & wr_addr < 14'h1400 ? _MEM_DATA_ADDR_T_1 : {{2'd0}, MEM_DATA_ADDR}; // @[AxiLiteSubordinateGenerator.scala 505:55 AxiLiteSubordinateGenerator.scala 507:43 AxiLiteSubordinateGenerator.scala 363:46]
  wire [13:0] _GEN_57 = wr_en ? _GEN_49 : {{2'd0}, MEM_DATA_ADDR}; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 363:46]
  wire  _GEN_58 = wr_en & _T_19; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 478:39]
  reg [1:0] state_rd; // @[AxiLiteSubordinateGenerator.scala 172:25]
  reg  rd_en; // @[AxiLiteSubordinateGenerator.scala 174:18]
  reg [13:0] rd_addr; // @[AxiLiteSubordinateGenerator.scala 175:20]
  reg [33:0] rd_data; // @[AxiLiteSubordinateGenerator.scala 176:20]
  wire  _T_20 = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_25 = io_ctrl_AR_bits_addr[15:2] >= 14'h400 & io_ctrl_AR_bits_addr[15:2] < 14'h1400; // @[AxiLiteSubordinateGenerator.scala 532:29]
  wire  _T_27 = 2'h1 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_28 = 2'h2 == state_rd; // @[Conditional.scala 37:30]
  wire  _T_29 = 2'h3 == state_rd; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_64 = io_ctrl_R_ready ? 2'h0 : state_rd; // @[AxiLiteSubordinateGenerator.scala 200:29 AxiLiteSubordinateGenerator.scala 201:18 AxiLiteSubordinateGenerator.scala 172:25]
  wire [1:0] _GEN_65 = _T_29 ? _GEN_64 : state_rd; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 172:25]
  wire [33:0] _GEN_73 = _T_29 ? rd_data : 34'h0; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 229:28 AxiLiteSubordinateGenerator.scala 208:24]
  wire  _GEN_75 = _T_28 ? 1'h0 : _T_29; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 223:23]
  wire [33:0] _GEN_76 = _T_28 ? 34'h0 : _GEN_73; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 224:28]
  wire  _GEN_78 = _T_27 ? 1'h0 : _GEN_75; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 218:23]
  wire [33:0] _GEN_79 = _T_27 ? 34'h0 : _GEN_76; // @[Conditional.scala 39:67 AxiLiteSubordinateGenerator.scala 219:28]
  wire [33:0] _GEN_82 = _T_20 ? 34'h0 : _GEN_79; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 214:28]
  wire  _T_36 = rd_addr >= 14'h400 & rd_addr < 14'h1400; // @[AxiLiteSubordinateGenerator.scala 532:29]
  wire [31:0] _GEN_84 = rd_addr == 14'h0 ? 32'h5157311a : 32'hdeadbeef; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17 AxiLiteSubordinateGenerator.scala 435:13]
  wire [31:0] _GEN_85 = rd_addr == 14'h1 ? 32'h10100 : _GEN_84; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_2 = {SCRATCH_FIELD[7],SCRATCH_FIELD[6],SCRATCH_FIELD[5],SCRATCH_FIELD[4],SCRATCH_FIELD[3],
    SCRATCH_FIELD[2],SCRATCH_FIELD[1],SCRATCH_FIELD[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [15:0] lo_2 = {SCRATCH_FIELD[15],SCRATCH_FIELD[14],SCRATCH_FIELD[13],SCRATCH_FIELD[12],SCRATCH_FIELD[11],
    SCRATCH_FIELD[10],SCRATCH_FIELD[9],SCRATCH_FIELD[8],lo_lo_2}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [7:0] hi_lo_2 = {SCRATCH_FIELD[23],SCRATCH_FIELD[22],SCRATCH_FIELD[21],SCRATCH_FIELD[20],SCRATCH_FIELD[19],
    SCRATCH_FIELD[18],SCRATCH_FIELD[17],SCRATCH_FIELD[16]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_133 = {SCRATCH_FIELD[31],SCRATCH_FIELD[30],SCRATCH_FIELD[29],SCRATCH_FIELD[28],SCRATCH_FIELD[27],
    SCRATCH_FIELD[26],SCRATCH_FIELD[25],SCRATCH_FIELD[24],hi_lo_2,lo_2}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_86 = rd_addr == 14'h3 ? _T_133 : _GEN_85; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [31:0] _T_135 = {16'h0,8'h0,4'h0,2'h0,1'h0,STATUS_DONE}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_87 = rd_addr == 14'h4 ? _T_135 : _GEN_86; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [31:0] _T_137 = {CONTROL_ENABLE,1'h0,2'h0,4'h0,8'h0,8'h0,4'h0,2'h0,1'h0,CONTROL_CLEAR}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_88 = rd_addr == 14'h5 ? _T_137 : _GEN_87; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [7:0] lo_lo_5 = {TRIG_CTRL_MASK[7],TRIG_CTRL_MASK[6],TRIG_CTRL_MASK[5],TRIG_CTRL_MASK[4],TRIG_CTRL_MASK[3],
    TRIG_CTRL_MASK[2],TRIG_CTRL_MASK[1],TRIG_CTRL_MASK[0]}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _T_149 = {TRIG_CTRL_FORCE,1'h0,2'h0,4'h0,8'h0,4'h0,2'h0,TRIG_CTRL_MASK[9],TRIG_CTRL_MASK[8],lo_lo_5}; // @[AxiLiteSubordinateGenerator.scala 425:17]
  wire [31:0] _GEN_89 = rd_addr == 14'h9 ? _T_149 : _GEN_88; // @[AxiLiteSubordinateGenerator.scala 439:42 AxiLiteSubordinateGenerator.scala 440:17]
  wire [13:0] _MEM_DATA_ADDR_T_3 = io_ctrl_AR_bits_addr[15:2] - 14'h400; // @[AxiLiteSubordinateGenerator.scala 456:54]
  wire [13:0] _GEN_91 = _T_25 ? _MEM_DATA_ADDR_T_3 : _GEN_57; // @[AxiLiteSubordinateGenerator.scala 455:55 AxiLiteSubordinateGenerator.scala 456:43]
  wire [13:0] _GEN_93 = state_rd == 2'h0 ? _GEN_91 : _GEN_57; // @[AxiLiteSubordinateGenerator.scala 238:30]
  wire  _GEN_94 = state_rd == 2'h0 & _T_25; // @[AxiLiteSubordinateGenerator.scala 238:30 AxiLiteSubordinateGenerator.scala 477:41]
  assign io_out_MEM_DATA_WE = MEM_DATA_WE; // @[AxiLiteSubordinateGenerator.scala 407:50]
  assign io_out_MEM_DATA_ADDR = MEM_DATA_ADDR; // @[AxiLiteSubordinateGenerator.scala 406:52]
  assign io_out_MEM_DATA_DIN = MEM_DATA_DIN; // @[AxiLiteSubordinateGenerator.scala 405:51]
  assign io_out_TRIG_CTRL_FORCE = TRIG_CTRL_FORCE; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_TRIG_CTRL_MASK = TRIG_CTRL_MASK; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_CONTROL_ENABLE = CONTROL_ENABLE; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_out_CONTROL_CLEAR = CONTROL_CLEAR; // @[AxiLiteSubordinateGenerator.scala 398:51]
  assign io_ctrl_AW_ready = _T | _T_5; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 122:24]
  assign io_ctrl_W_ready = _T | _GEN_38; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 123:23]
  assign io_ctrl_B_valid = _T ? 1'h0 : _GEN_39; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 124:23]
  assign io_ctrl_AR_ready = 2'h0 == state_rd; // @[Conditional.scala 37:30]
  assign io_ctrl_R_valid = _T_20 ? 1'h0 : _GEN_78; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 213:23]
  assign io_ctrl_R_bits_rdata = _GEN_82[31:0];
  always @(posedge clock) begin
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 14'h3) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        SCRATCH_FIELD <= wr_data; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    STATUS_DONE <= io_inp_STATUS_DONE; // @[AxiLiteSubordinateGenerator.scala 382:40]
    CONTROL_CLEAR <= wr_en & _GEN_44; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 470:40]
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 14'h5) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        CONTROL_ENABLE <= wr_data[31]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr == 14'h9) begin // @[AxiLiteSubordinateGenerator.scala 493:46]
        TRIG_CTRL_MASK <= wr_data[9:0]; // @[AxiLiteSubordinateGenerator.scala 494:42]
      end
    end
    TRIG_CTRL_FORCE <= wr_en & _GEN_47; // @[AxiLiteSubordinateGenerator.scala 164:15 AxiLiteSubordinateGenerator.scala 470:40]
    if (wr_en) begin // @[AxiLiteSubordinateGenerator.scala 164:15]
      if (wr_addr >= 14'h400 & wr_addr < 14'h1400) begin // @[AxiLiteSubordinateGenerator.scala 505:55]
        MEM_DATA_DIN <= wr_data; // @[AxiLiteSubordinateGenerator.scala 506:42]
      end
    end
    MEM_DATA_ADDR <= _GEN_93[11:0];
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 365:48]
      MEM_DATA_WE <= 1'h0; // @[AxiLiteSubordinateGenerator.scala 365:48]
    end else begin
      MEM_DATA_WE <= _GEN_58;
    end
    if (reset) begin // @[AxiLiteSubordinateGenerator.scala 368:29]
      MEM_DATA_ACT1 <= 1'h0; // @[AxiLiteSubordinateGenerator.scala 368:29]
    end else begin
      MEM_DATA_ACT1 <= _GEN_94;
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
        wr_addr <= io_ctrl_AW_bits_addr[15:2]; // @[AxiLiteSubordinateGenerator.scala 78:17]
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
    end else if (_T_20) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[AxiLiteSubordinateGenerator.scala 183:30]
        if (_T_25) begin // @[AxiLiteSubordinateGenerator.scala 186:83]
          state_rd <= 2'h1; // @[AxiLiteSubordinateGenerator.scala 187:20]
        end else begin
          state_rd <= 2'h2; // @[AxiLiteSubordinateGenerator.scala 189:20]
        end
      end
    end else if (_T_27) begin // @[Conditional.scala 39:67]
      state_rd <= 2'h2; // @[AxiLiteSubordinateGenerator.scala 194:16]
    end else if (_T_28) begin // @[Conditional.scala 39:67]
      state_rd <= 2'h3; // @[AxiLiteSubordinateGenerator.scala 197:16]
    end else begin
      state_rd <= _GEN_65;
    end
    rd_en <= _T_20 & io_ctrl_AR_valid; // @[Conditional.scala 40:58 AxiLiteSubordinateGenerator.scala 179:9]
    if (_T_20) begin // @[Conditional.scala 40:58]
      if (io_ctrl_AR_valid) begin // @[AxiLiteSubordinateGenerator.scala 183:30]
        rd_addr <= io_ctrl_AR_bits_addr[15:2]; // @[AxiLiteSubordinateGenerator.scala 185:17]
      end
    end
    if (MEM_DATA_ACT2) begin // @[AxiLiteSubordinateGenerator.scala 517:65]
      rd_data <= {{2'd0}, io_inp_MEM_DATA_DOUT}; // @[AxiLiteSubordinateGenerator.scala 518:17]
    end else if (rd_en & ~_T_36) begin // @[AxiLiteSubordinateGenerator.scala 234:59]
      rd_data <= {{2'd0}, _GEN_89};
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
  CONTROL_ENABLE = _RAND_3[0:0];
  _RAND_4 = {1{`RANDOM}};
  TRIG_CTRL_MASK = _RAND_4[9:0];
  _RAND_5 = {1{`RANDOM}};
  TRIG_CTRL_FORCE = _RAND_5[0:0];
  _RAND_6 = {1{`RANDOM}};
  MEM_DATA_DIN = _RAND_6[31:0];
  _RAND_7 = {1{`RANDOM}};
  MEM_DATA_ADDR = _RAND_7[11:0];
  _RAND_8 = {1{`RANDOM}};
  MEM_DATA_WE = _RAND_8[0:0];
  _RAND_9 = {1{`RANDOM}};
  MEM_DATA_ACT1 = _RAND_9[0:0];
  _RAND_10 = {1{`RANDOM}};
  MEM_DATA_ACT2 = _RAND_10[0:0];
  _RAND_11 = {1{`RANDOM}};
  state_wr = _RAND_11[1:0];
  _RAND_12 = {1{`RANDOM}};
  wr_en = _RAND_12[0:0];
  _RAND_13 = {1{`RANDOM}};
  wr_addr = _RAND_13[13:0];
  _RAND_14 = {1{`RANDOM}};
  wr_data = _RAND_14[31:0];
  _RAND_15 = {1{`RANDOM}};
  state_rd = _RAND_15[1:0];
  _RAND_16 = {1{`RANDOM}};
  rd_en = _RAND_16[0:0];
  _RAND_17 = {1{`RANDOM}};
  rd_addr = _RAND_17[13:0];
  _RAND_18 = {2{`RANDOM}};
  rd_data = _RAND_18[33:0];
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
  input         io_enable,
  input         io_clear,
  output        io_done,
  input  [8:0]  io_trigger_mask,
  input         io_trigger_force,
  input         io_MBDEBUG_TDI,
  input         io_MBDEBUG_TDO,
  input         io_MBDEBUG_CLK,
  input         io_MBDEBUG_REG_EN,
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
`endif // RANDOMIZE_REG_INIT
  reg [11:0] addr; // @[PoorMansSystemILAKernel.scala 49:17]
  reg [11:0] addr_last; // @[PoorMansSystemILAKernel.scala 50:22]
  reg [1:0] state; // @[PoorMansSystemILAKernel.scala 57:22]
  wire  _T_2 = 2'h0 == state; // @[Conditional.scala 37:30]
  wire  _T_5 = 2'h1 == state; // @[Conditional.scala 37:30]
  wire [11:0] _addr_T_1 = addr + 12'h1; // @[PoorMansSystemILAKernel.scala 67:20]
  wire [11:0] _addr_last_T_1 = addr - 12'h64; // @[PoorMansSystemILAKernel.scala 70:27]
  reg [8:0] mbdebug_prev; // @[PoorMansSystemILAKernel.scala 87:29]
  wire [4:0] mbdebug_edge_hi = {io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,io_MBDEBUG_SHIFT}; // @[PoorMansSystemILAKernel.scala 88:63]
  wire [3:0] mbdebug_edge_lo = {io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[PoorMansSystemILAKernel.scala 88:63]
  wire [8:0] _mbdebug_edge_T = {io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,io_MBDEBUG_SHIFT,
    io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[PoorMansSystemILAKernel.scala 88:63]
  wire [8:0] mbdebug_edge = mbdebug_prev ^ _mbdebug_edge_T; // @[PoorMansSystemILAKernel.scala 88:44]
  wire [8:0] _trigger_T = mbdebug_edge & io_trigger_mask; // @[PoorMansSystemILAKernel.scala 89:29]
  wire  trigger = _trigger_T != 9'h0 | io_trigger_force; // @[PoorMansSystemILAKernel.scala 89:57]
  wire  _T_8 = 2'h2 == state; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_4 = addr == addr_last ? 2'h3 : state; // @[PoorMansSystemILAKernel.scala 75:32 PoorMansSystemILAKernel.scala 76:15 PoorMansSystemILAKernel.scala 57:22]
  wire  _T_12 = 2'h3 == state; // @[Conditional.scala 37:30]
  wire [1:0] _GEN_5 = io_clear ? 2'h0 : state; // @[PoorMansSystemILAKernel.scala 80:22 PoorMansSystemILAKernel.scala 81:15 PoorMansSystemILAKernel.scala 57:22]
  wire [1:0] _GEN_6 = _T_12 ? _GEN_5 : state; // @[Conditional.scala 39:67 PoorMansSystemILAKernel.scala 57:22]
  reg [15:0] cntr; // @[PoorMansSystemILAKernel.scala 92:17]
  wire [9:0] out_data_lo_1 = {io_DEBUG_SYS_RESET,io_MBDEBUG_TDI,io_MBDEBUG_TDO,io_MBDEBUG_CLK,io_MBDEBUG_REG_EN,
    io_MBDEBUG_SHIFT,io_MBDEBUG_CAPTURE,io_MBDEBUG_UPDATE,io_MBDEBUG_RST,io_MBDEBUG_DISABLE}; // @[Cat.scala 30:58]
  wire [21:0] out_data_hi_1 = {cntr,4'h0,state}; // @[Cat.scala 30:58]
  assign io_done = state == 2'h3; // @[PoorMansSystemILAKernel.scala 101:20]
  assign io_dout = {out_data_hi_1,out_data_lo_1}; // @[Cat.scala 30:58]
  assign io_addr = addr; // @[PoorMansSystemILAKernel.scala 99:11]
  assign io_we = state == 2'h1 | state == 2'h2; // @[PoorMansSystemILAKernel.scala 100:42]
  always @(posedge clock) begin
    if (_T_2) begin // @[Conditional.scala 40:58]
      if (io_enable) begin // @[PoorMansSystemILAKernel.scala 61:23]
        addr <= 12'h0; // @[PoorMansSystemILAKernel.scala 63:14]
      end
    end else if (_T_5) begin // @[Conditional.scala 39:67]
      addr <= _addr_T_1; // @[PoorMansSystemILAKernel.scala 67:12]
    end else if (_T_8) begin // @[Conditional.scala 39:67]
      addr <= _addr_T_1; // @[PoorMansSystemILAKernel.scala 74:12]
    end
    if (!(_T_2)) begin // @[Conditional.scala 40:58]
      if (_T_5) begin // @[Conditional.scala 39:67]
        if (trigger) begin // @[PoorMansSystemILAKernel.scala 68:21]
          addr_last <= _addr_last_T_1; // @[PoorMansSystemILAKernel.scala 70:19]
        end
      end
    end
    if (reset) begin // @[PoorMansSystemILAKernel.scala 57:22]
      state <= 2'h0; // @[PoorMansSystemILAKernel.scala 57:22]
    end else if (_T_2) begin // @[Conditional.scala 40:58]
      if (io_enable) begin // @[PoorMansSystemILAKernel.scala 61:23]
        state <= 2'h1; // @[PoorMansSystemILAKernel.scala 62:15]
      end
    end else if (_T_5) begin // @[Conditional.scala 39:67]
      if (trigger) begin // @[PoorMansSystemILAKernel.scala 68:21]
        state <= 2'h2; // @[PoorMansSystemILAKernel.scala 69:15]
      end
    end else if (_T_8) begin // @[Conditional.scala 39:67]
      state <= _GEN_4;
    end else begin
      state <= _GEN_6;
    end
    mbdebug_prev <= {mbdebug_edge_hi,mbdebug_edge_lo}; // @[PoorMansSystemILAKernel.scala 87:47]
    cntr <= cntr + 16'h1; // @[PoorMansSystemILAKernel.scala 93:16]
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
  mbdebug_prev = _RAND_3[8:0];
  _RAND_4 = {1{`RANDOM}};
  cntr = _RAND_4[15:0];
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
  input  [13:0] io_ctrl_AW_bits_addr,
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
  input  [13:0] io_ctrl_AR_bits_addr,
  input  [2:0]  io_ctrl_AR_bits_prot,
  input         io_ctrl_R_ready,
  output        io_ctrl_R_valid,
  output [31:0] io_ctrl_R_bits_rdata,
  output [1:0]  io_ctrl_R_bits_rresp,
  input         io_MBDEBUG_TDI,
  input         io_MBDEBUG_TDO,
  input         io_MBDEBUG_CLK,
  input         io_MBDEBUG_REG_EN,
  input         io_MBDEBUG_SHIFT,
  input         io_MBDEBUG_CAPTURE,
  input         io_MBDEBUG_UPDATE,
  input         io_MBDEBUG_RST,
  input         io_MBDEBUG_DISABLE,
  input         io_DEBUG_SYS_RESET,
  output        io_int_req
);
  wire  mod_ctrl_clock; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_reset; // @[PoorMansSystemILA.scala 67:24]
  wire [31:0] mod_ctrl_io_inp_MEM_DATA_DOUT; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_inp_STATUS_DONE; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_out_MEM_DATA_WE; // @[PoorMansSystemILA.scala 67:24]
  wire [11:0] mod_ctrl_io_out_MEM_DATA_ADDR; // @[PoorMansSystemILA.scala 67:24]
  wire [31:0] mod_ctrl_io_out_MEM_DATA_DIN; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_out_TRIG_CTRL_FORCE; // @[PoorMansSystemILA.scala 67:24]
  wire [9:0] mod_ctrl_io_out_TRIG_CTRL_MASK; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_out_CONTROL_ENABLE; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_out_CONTROL_CLEAR; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_AW_ready; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_AW_valid; // @[PoorMansSystemILA.scala 67:24]
  wire [15:0] mod_ctrl_io_ctrl_AW_bits_addr; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_W_ready; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_W_valid; // @[PoorMansSystemILA.scala 67:24]
  wire [31:0] mod_ctrl_io_ctrl_W_bits_wdata; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_B_ready; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_B_valid; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_AR_ready; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_AR_valid; // @[PoorMansSystemILA.scala 67:24]
  wire [15:0] mod_ctrl_io_ctrl_AR_bits_addr; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_R_ready; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_ctrl_io_ctrl_R_valid; // @[PoorMansSystemILA.scala 67:24]
  wire [31:0] mod_ctrl_io_ctrl_R_bits_rdata; // @[PoorMansSystemILA.scala 67:24]
  wire  mod_mem_clk; // @[PoorMansSystemILA.scala 76:23]
  wire [11:0] mod_mem_addra; // @[PoorMansSystemILA.scala 76:23]
  wire [31:0] mod_mem_dina; // @[PoorMansSystemILA.scala 76:23]
  wire [31:0] mod_mem_douta; // @[PoorMansSystemILA.scala 76:23]
  wire  mod_mem_wea; // @[PoorMansSystemILA.scala 76:23]
  wire [11:0] mod_mem_addrb; // @[PoorMansSystemILA.scala 76:23]
  wire [31:0] mod_mem_dinb; // @[PoorMansSystemILA.scala 76:23]
  wire [31:0] mod_mem_doutb; // @[PoorMansSystemILA.scala 76:23]
  wire  mod_mem_web; // @[PoorMansSystemILA.scala 76:23]
  wire  mod_kernel_clock; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_reset; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_enable; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_clear; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_done; // @[PoorMansSystemILA.scala 83:26]
  wire [8:0] mod_kernel_io_trigger_mask; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_trigger_force; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_TDI; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_TDO; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_CLK; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_REG_EN; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_SHIFT; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_CAPTURE; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_UPDATE; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_RST; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_MBDEBUG_DISABLE; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_DEBUG_SYS_RESET; // @[PoorMansSystemILA.scala 83:26]
  wire [31:0] mod_kernel_io_dout; // @[PoorMansSystemILA.scala 83:26]
  wire [11:0] mod_kernel_io_addr; // @[PoorMansSystemILA.scala 83:26]
  wire  mod_kernel_io_we; // @[PoorMansSystemILA.scala 83:26]
  AxiLiteSubordinateGenerator mod_ctrl ( // @[PoorMansSystemILA.scala 67:24]
    .clock(mod_ctrl_clock),
    .reset(mod_ctrl_reset),
    .io_inp_MEM_DATA_DOUT(mod_ctrl_io_inp_MEM_DATA_DOUT),
    .io_inp_STATUS_DONE(mod_ctrl_io_inp_STATUS_DONE),
    .io_out_MEM_DATA_WE(mod_ctrl_io_out_MEM_DATA_WE),
    .io_out_MEM_DATA_ADDR(mod_ctrl_io_out_MEM_DATA_ADDR),
    .io_out_MEM_DATA_DIN(mod_ctrl_io_out_MEM_DATA_DIN),
    .io_out_TRIG_CTRL_FORCE(mod_ctrl_io_out_TRIG_CTRL_FORCE),
    .io_out_TRIG_CTRL_MASK(mod_ctrl_io_out_TRIG_CTRL_MASK),
    .io_out_CONTROL_ENABLE(mod_ctrl_io_out_CONTROL_ENABLE),
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
  DualPortRam #(.RAM_WIDTH(32), .RAM_DEPTH(4096)) mod_mem ( // @[PoorMansSystemILA.scala 76:23]
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
  PoorMansSystemILAKernel mod_kernel ( // @[PoorMansSystemILA.scala 83:26]
    .clock(mod_kernel_clock),
    .reset(mod_kernel_reset),
    .io_enable(mod_kernel_io_enable),
    .io_clear(mod_kernel_io_clear),
    .io_done(mod_kernel_io_done),
    .io_trigger_mask(mod_kernel_io_trigger_mask),
    .io_trigger_force(mod_kernel_io_trigger_force),
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
  assign io_ctrl_AW_ready = mod_ctrl_io_ctrl_AW_ready; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_W_ready = mod_ctrl_io_ctrl_W_ready; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_B_valid = mod_ctrl_io_ctrl_B_valid; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_B_bits = 2'h0; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_AR_ready = mod_ctrl_io_ctrl_AR_ready; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_R_valid = mod_ctrl_io_ctrl_R_valid; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_R_bits_rdata = mod_ctrl_io_ctrl_R_bits_rdata; // @[PoorMansSystemILA.scala 70:11]
  assign io_ctrl_R_bits_rresp = 2'h0; // @[PoorMansSystemILA.scala 70:11]
  assign io_int_req = mod_kernel_io_done; // @[PoorMansSystemILA.scala 98:14]
  assign mod_ctrl_clock = clock;
  assign mod_ctrl_reset = reset;
  assign mod_ctrl_io_inp_MEM_DATA_DOUT = mod_mem_douta; // @[PoorMansSystemILA.scala 81:36]
  assign mod_ctrl_io_inp_STATUS_DONE = mod_kernel_io_done; // @[PoorMansSystemILA.scala 92:34]
  assign mod_ctrl_io_ctrl_AW_valid = io_ctrl_AW_valid; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_AW_bits_addr = {{2'd0}, io_ctrl_AW_bits_addr}; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_W_valid = io_ctrl_W_valid; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_W_bits_wdata = io_ctrl_W_bits_wdata; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_B_ready = io_ctrl_B_ready; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_AR_valid = io_ctrl_AR_valid; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_AR_bits_addr = {{2'd0}, io_ctrl_AR_bits_addr}; // @[PoorMansSystemILA.scala 70:11]
  assign mod_ctrl_io_ctrl_R_ready = io_ctrl_R_ready; // @[PoorMansSystemILA.scala 70:11]
  assign mod_mem_clk = clock; // @[PoorMansSystemILA.scala 77:18]
  assign mod_mem_addra = mod_ctrl_io_out_MEM_DATA_ADDR; // @[PoorMansSystemILA.scala 78:20]
  assign mod_mem_dina = mod_ctrl_io_out_MEM_DATA_DIN; // @[PoorMansSystemILA.scala 79:19]
  assign mod_mem_wea = mod_ctrl_io_out_MEM_DATA_WE; // @[PoorMansSystemILA.scala 80:67]
  assign mod_mem_addrb = mod_kernel_io_addr; // @[PoorMansSystemILA.scala 94:20]
  assign mod_mem_dinb = mod_kernel_io_dout; // @[PoorMansSystemILA.scala 95:19]
  assign mod_mem_web = mod_kernel_io_we; // @[PoorMansSystemILA.scala 96:18]
  assign mod_kernel_clock = clock;
  assign mod_kernel_reset = reset;
  assign mod_kernel_io_enable = mod_ctrl_io_out_CONTROL_ENABLE; // @[PoorMansSystemILA.scala 90:24]
  assign mod_kernel_io_clear = mod_ctrl_io_out_CONTROL_CLEAR; // @[PoorMansSystemILA.scala 91:23]
  assign mod_kernel_io_trigger_mask = mod_ctrl_io_out_TRIG_CTRL_MASK[8:0]; // @[PoorMansSystemILA.scala 87:30]
  assign mod_kernel_io_trigger_force = mod_ctrl_io_out_TRIG_CTRL_FORCE; // @[PoorMansSystemILA.scala 88:31]
  assign mod_kernel_io_MBDEBUG_TDI = io_MBDEBUG_TDI; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_TDO = io_MBDEBUG_TDO; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_CLK = io_MBDEBUG_CLK; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_REG_EN = io_MBDEBUG_REG_EN; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_SHIFT = io_MBDEBUG_SHIFT; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_CAPTURE = io_MBDEBUG_CAPTURE; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_UPDATE = io_MBDEBUG_UPDATE; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_RST = io_MBDEBUG_RST; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_MBDEBUG_DISABLE = io_MBDEBUG_DISABLE; // @[PoorMansSystemILA.scala 84:25]
  assign mod_kernel_io_DEBUG_SYS_RESET = io_DEBUG_SYS_RESET; // @[PoorMansSystemILA.scala 85:33]
endmodule
