

if {[file exists work]} {
    vdel -lib work -all
}

vlib work

vlog -work work ../../ip_cores/pipeline_with_axi/hdl/PipelineWithAxiLite.v
vcom -work work -2008 ../hdl/pipelinewithaxilite_th.vhd
vcom -work work -2008 ../hdl/pipelinewithaxilite_tb.vhd

vsim work.pipelinewithaxilite_tb


#
add wave -divider "Clock and reset"
add wave \
    sim:/pipelinewithaxilite_tb/i_test_harness/clock \
    sim:/pipelinewithaxilite_tb/i_test_harness/reset

add wave -divider "AXI4-Lite slave"
add wave -radix hex  \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_AWREADY \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_AWVALID \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_AWADDR \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_WREADY \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_WVALID \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_WDATA \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_WSTRB \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_BREADY \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_BVALID \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_BRESP \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_ARREADY \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_ARVALID \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_ARADDR \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_RREADY \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_RVALID \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_RDATA \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXI_RRESP

add wave -divider "AXI4-Stream input"
add wave -radix hex \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXIS_IN_TDATA \
    sim:/pipelinewithaxilite_tb/i_test_harness/S_AXIS_IN_TVALID

add wave -divider "AXI4-Stream output"
add wave -radix hex  \
    sim:/pipelinewithaxilite_tb/i_test_harness/M_AXIS_OUT_TDATA \
    sim:/pipelinewithaxilite_tb/i_test_harness/M_AXIS_OUT_TVALID

run -all

wave zoom full
