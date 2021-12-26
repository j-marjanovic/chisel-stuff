

module axi4_activity_led #(
  parameter PULSE_LEN_CYCLES = 25_000_000
)(
  input clk,
  input reset_n,

  input slot0_axi_arvalid,
  input slot0_axi_arready,
  input slot0_axi_awvalid,
  input slot0_axi_awready,

  input slot1_axi_arvalid,
  input slot1_axi_arready,
  input slot1_axi_awvalid,
  input slot1_axi_awready,

  input slot2_axi_arvalid,
  input slot2_axi_arready,
  input slot2_axi_awvalid,
  input slot2_axi_awready,

  output LED
);

  wire act =
    (slot0_axi_arvalid && slot0_axi_arready) ||
    (slot0_axi_awvalid && slot0_axi_awready) ||
    (slot1_axi_arvalid && slot1_axi_arready) ||
    (slot1_axi_awvalid && slot1_axi_awready) ||
    (slot2_axi_arvalid && slot2_axi_arready) ||
    (slot2_axi_awvalid && slot2_axi_awready) ;

  
  localparam CNTR_W = $clog2(PULSE_LEN_CYCLES)+1;
  logic [CNTR_W-1:0] cntr;

  enum int unsigned {
    S_IDLE,
    S_PULSE
  } state;

  always_ff @(posedge clk) begin: proc_state
    if (!reset_n) begin
      state <= S_IDLE;
    end else begin
      case (state)
        S_IDLE:  if (act)                        state <= S_PULSE;
        S_PULSE: if (cntr >= PULSE_LEN_CYCLES-1) state <= S_IDLE;
        default:                                 state <= S_IDLE;
      endcase
    end
  end

  always_ff @(posedge clk) begin: proc_cntr
    if (!reset_n) begin
      cntr <= 0;
    end else begin
      if (act) begin
        cntr <= 0;
      end else if (state == S_PULSE) begin
        cntr <= cntr + 1;
      end
    end
  end

  assign LED = state == S_PULSE;

endmodule
