
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
vlog ../tb/pp_sp_pcie_endpoint_tb_cpld2.sv


vsim work.pp_sp_pcie_endpoint_tb_cpld2

add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/coreclkout_hip \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/reset_status

add wave -divider "RX"
add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_data \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_sop \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_eop \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_empty \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_ready \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_err \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_mask \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/rx_st_bar

add wave -divider "Data Out"
add wave -radix hex \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/dma_out_data \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/dma_out_valid \
  sim:/pp_sp_pcie_endpoint_tb_cpld2/th/dma_out_empty


run -all

wave zoom full
