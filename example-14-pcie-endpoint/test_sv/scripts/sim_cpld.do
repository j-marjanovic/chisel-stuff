
.main clear

if {[file exists work]} {
  vdel -lib work -all
}

# BFMs
set INTEL_QUARUTS_PATH /opt/intelFPGA/20.1

vlog $INTEL_QUARUTS_PATH/ip/altera/sopc_builder_ip/verification/lib/verbosity_pkg.sv
vlog $INTEL_QUARUTS_PATH/ip/altera/sopc_builder_ip/verification/lib/avalon_utilities_pkg.sv
vlog $INTEL_QUARUTS_PATH/ip/altera/sopc_builder_ip/verification/altera_avalon_clock_reset_source/altera_avalon_clock_reset_source.sv
vlog $INTEL_QUARUTS_PATH/ip/altera/sopc_builder_ip/verification/altera_avalon_st_source_bfm/altera_avalon_st_source_bfm.sv
vlog $INTEL_QUARUTS_PATH/ip/altera/sopc_builder_ip/verification/altera_avalon_st_sink_bfm/altera_avalon_st_sink_bfm.sv

# DUT
vlog ../../ip_cores/pcie_endpoint/hdl/PcieEndpointWrapper256.sv

# TB
vlog ../tb/PciePackets.sv
vlog ../tb/pp_sp_pcie_endpoint_th.sv
vlog ../tb/pp_sp_pcie_endpoint_tb_cpld.sv


vsim work.pp_sp_pcie_endpoint_tb_cpld

add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/coreclkout_hip \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/reset_status

add wave -divider "RX"
add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_data \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_sop \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_eop \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_empty \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_ready \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_err \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_mask \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/rx_st_bar

add wave -divider "TX"
add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_data \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_sop \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_eop \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_ready \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_empty \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/tx_st_err

add wave -divider "Data Out"
add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/dma_out_data \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/dma_out_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/dma_out_empty

add wave -divider "Internal - TX arb"
add wave \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_tx_arbiter/state \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_tx_arbiter/io_bm_hint

add wave -divider "Internal - engine"
add wave \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_bus_master/mod_engine/state
add wave \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_bus_master/mod_engine/io_tx_st_ready

add wave -divider "Internal - regs "
add wave \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_bus_master/mod_regs/reg_dma_desc_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_bus_master/mod_regs/reg_dma_desc__addr32_0 \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/pcie_endpoint/mod_bus_master/mod_regs/reg_dma_desc_control

add wave -divider "Internal - Ready corr"
add wave \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/tx_ready_corr/app_ready \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/DUT/tx_ready_corr/core_ready \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/st_sink_tx/sink_ready_qualified \
  sim:/pp_sp_pcie_endpoint_tb_cpld/th/st_sink_tx/sink_valid_qualified \


run -all

wave zoom full
