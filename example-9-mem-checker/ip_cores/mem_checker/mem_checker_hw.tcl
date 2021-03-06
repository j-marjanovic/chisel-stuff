# TCL File Generated by Component Editor 19.1
# Sun Jan 31 12:10:58 CET 2021
# DO NOT MODIFY


# 
# mem_checker "Memory Checker" v0.1
# Jan Marjanovic 2021.01.31.12:10:58
# 
# 

# 
# request TCL package from ACDS 16.1
# 
package require -exact qsys 16.1


# 
# module mem_checker
# 
set_module_property DESCRIPTION ""
set_module_property NAME mem_checker
set_module_property VERSION 0.1
set_module_property INTERNAL false
set_module_property OPAQUE_ADDRESS_MAP true
set_module_property GROUP Verification
set_module_property AUTHOR "Jan Marjanovic"
set_module_property DISPLAY_NAME "Memory Checker"
set_module_property INSTANTIATE_IN_SYSTEM_MODULE true
set_module_property EDITABLE true
set_module_property REPORT_TO_TALKBACK false
set_module_property ALLOW_GREYBOX_GENERATION false
set_module_property REPORT_HIERARCHY false


# 
# file sets
# 
add_fileset QUARTUS_SYNTH QUARTUS_SYNTH "" ""
set_fileset_property QUARTUS_SYNTH TOP_LEVEL MemChecker
set_fileset_property QUARTUS_SYNTH ENABLE_RELATIVE_INCLUDE_PATHS false
set_fileset_property QUARTUS_SYNTH ENABLE_FILE_OVERWRITE_MODE false
add_fileset_file MemChecker.sv SYSTEM_VERILOG PATH hdl/MemChecker.sv TOP_LEVEL_FILE

add_fileset SIM_VERILOG SIM_VERILOG "" ""
set_fileset_property SIM_VERILOG ENABLE_RELATIVE_INCLUDE_PATHS false
set_fileset_property SIM_VERILOG ENABLE_FILE_OVERWRITE_MODE true
set_fileset_property SIM_VERILOG TOP_LEVEL MemChecker
add_fileset_file MemChecker.sv SYSTEM_VERILOG PATH hdl/MemChecker.sv


# 
# parameters
# 


# 
# display items
# 


# 
# connection point clock
# 
add_interface clock clock end
set_interface_property clock clockRate 0
set_interface_property clock ENABLED true
set_interface_property clock EXPORT_OF ""
set_interface_property clock PORT_NAME_MAP ""
set_interface_property clock CMSIS_SVD_VARIABLES ""
set_interface_property clock SVD_ADDRESS_GROUP ""

add_interface_port clock clock clk Input 1


# 
# connection point reset
# 
add_interface reset reset end
set_interface_property reset associatedClock clock
set_interface_property reset synchronousEdges DEASSERT
set_interface_property reset ENABLED true
set_interface_property reset EXPORT_OF ""
set_interface_property reset PORT_NAME_MAP ""
set_interface_property reset CMSIS_SVD_VARIABLES ""
set_interface_property reset SVD_ADDRESS_GROUP ""

add_interface_port reset reset reset Input 1


# 
# connection point ctrl
# 
add_interface ctrl axi4lite end
set_interface_property ctrl associatedClock clock
set_interface_property ctrl associatedReset reset
set_interface_property ctrl readAcceptanceCapability 1
set_interface_property ctrl writeAcceptanceCapability 1
set_interface_property ctrl combinedAcceptanceCapability 1
set_interface_property ctrl readDataReorderingDepth 1
set_interface_property ctrl bridgesToMaster ""
set_interface_property ctrl ENABLED true
set_interface_property ctrl EXPORT_OF ""
set_interface_property ctrl PORT_NAME_MAP ""
set_interface_property ctrl CMSIS_SVD_VARIABLES ""
set_interface_property ctrl SVD_ADDRESS_GROUP ""

add_interface_port ctrl io_ctrl_AR_bits_addr araddr Input 8
add_interface_port ctrl io_ctrl_AR_ready arready Output 1
add_interface_port ctrl io_ctrl_AR_valid arvalid Input 1
add_interface_port ctrl io_ctrl_AW_bits_addr awaddr Input 8
add_interface_port ctrl io_ctrl_AW_ready awready Output 1
add_interface_port ctrl io_ctrl_AW_valid awvalid Input 1
add_interface_port ctrl io_ctrl_B_ready bready Input 1
add_interface_port ctrl io_ctrl_B_bits bresp Output 2
add_interface_port ctrl io_ctrl_B_valid bvalid Output 1
add_interface_port ctrl io_ctrl_R_bits_rdata rdata Output 32
add_interface_port ctrl io_ctrl_R_bits_rresp rresp Output 2
add_interface_port ctrl io_ctrl_R_valid rvalid Output 1
add_interface_port ctrl io_ctrl_R_ready rready Input 1
add_interface_port ctrl io_ctrl_W_bits_wdata wdata Input 32
add_interface_port ctrl io_ctrl_W_bits_wstrb wstrb Input 4
add_interface_port ctrl io_ctrl_W_ready wready Output 1
add_interface_port ctrl io_ctrl_W_valid wvalid Input 1
add_interface_port ctrl io_ctrl_AR_bits_prot arprot Input 3
add_interface_port ctrl io_ctrl_AW_bits_prot awprot Input 3


# 
# connection point master
# 
add_interface master axi4 start
set_interface_property master associatedClock clock
set_interface_property master associatedReset reset
set_interface_property master readIssuingCapability 1
set_interface_property master writeIssuingCapability 1
set_interface_property master combinedIssuingCapability 1
set_interface_property master ENABLED true
set_interface_property master EXPORT_OF ""
set_interface_property master PORT_NAME_MAP ""
set_interface_property master CMSIS_SVD_VARIABLES ""
set_interface_property master SVD_ADDRESS_GROUP ""

add_interface_port master io_m_AR_bits_addr araddr Output 48
add_interface_port master io_m_AR_bits_burst arburst Output 2
add_interface_port master io_m_AR_bits_cache arcache Output 4
add_interface_port master io_m_AR_bits_len arlen Output 8
add_interface_port master io_m_AR_bits_prot arprot Output 3
add_interface_port master io_m_AR_bits_qos arqos Output 4
add_interface_port master io_m_AR_bits_lock arlock Output 1
add_interface_port master io_m_AR_ready arready Input 1
add_interface_port master io_m_AR_valid arvalid Output 1
add_interface_port master io_m_AR_bits_size arsize Output 3
add_interface_port master io_m_AW_bits_addr awaddr Output 48
add_interface_port master io_m_AW_bits_burst awburst Output 2
add_interface_port master io_m_AW_bits_len awlen Output 8
add_interface_port master io_m_AW_bits_lock awlock Output 1
add_interface_port master io_m_AW_bits_prot awprot Output 3
add_interface_port master io_m_AW_bits_qos awqos Output 4
add_interface_port master io_m_AW_bits_cache awcache Output 4
add_interface_port master io_m_AW_ready awready Input 1
add_interface_port master io_m_AW_valid awvalid Output 1
add_interface_port master io_m_AW_bits_size awsize Output 3
add_interface_port master io_m_B_ready bready Output 1
add_interface_port master io_m_B_bits_resp bresp Input 2
add_interface_port master io_m_AR_bits_id arid Output 6
add_interface_port master io_m_AW_bits_id awid Output 6
add_interface_port master io_m_B_bits_id bid Input 6
add_interface_port master io_m_B_valid bvalid Input 1
add_interface_port master io_m_R_ready rready Output 1
add_interface_port master io_m_R_valid rvalid Input 1
add_interface_port master io_m_R_bits_data rdata Input 128
add_interface_port master io_m_R_bits_resp rresp Input 2
add_interface_port master io_m_R_bits_last rlast Input 1
add_interface_port master io_m_R_bits_id rid Input 6
add_interface_port master io_m_W_bits_data wdata Output 128
add_interface_port master io_m_W_bits_strb wstrb Output 16
add_interface_port master io_m_W_bits_last wlast Output 1
add_interface_port master io_m_W_ready wready Input 1
add_interface_port master io_m_W_valid wvalid Output 1

