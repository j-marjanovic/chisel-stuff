
################################################################
# This is a generated script based on design: system
#
# Though there are limitations about the generated script,
# the main purpose of this utility is to make learning
# IP Integrator Tcl commands easier.
################################################################

namespace eval _tcl {
proc get_script_folder {} {
   set script_path [file normalize [info script]]
   set script_folder [file dirname $script_path]
   return $script_folder
}
}
variable script_folder
set script_folder [_tcl::get_script_folder]

################################################################
# Check if script is running in correct Vivado version.
################################################################
set scripts_vivado_version 2021.1
set current_vivado_version [version -short]

if { [string first $scripts_vivado_version $current_vivado_version] == -1 } {
   puts ""
   catch {common::send_gid_msg -ssname BD::TCL -id 2041 -severity "ERROR" "This script was generated using Vivado <$scripts_vivado_version> and is being run in <$current_vivado_version> of Vivado. Please run the script in Vivado <$scripts_vivado_version> then open the design in Vivado <$current_vivado_version>. Upgrade the design by running \"Tools => Report => Report IP Status...\", then run write_bd_tcl to create an updated script."}

   return 1
}

################################################################
# START
################################################################

# To test this script, run the following commands from Vivado Tcl console:
# source system_script.tcl

# If there is no project opened, this script will create a
# project, but make sure you do not have an existing project
# <./myproj/project_1.xpr> in the current working folder.

set list_projs [get_projects -quiet]
if { $list_projs eq "" } {
   create_project project_1 myproj -part xczu3eg-sbva484-1-i
   set_property BOARD_PART avnet.com:ultra96v2:part0:1.1 [current_project]
}


# CHANGE DESIGN NAME HERE
variable design_name
set design_name system

# If you do not already have an existing IP Integrator design open,
# you can create a design using the following command:
#    create_bd_design $design_name

# Creating design if needed
set errMsg ""
set nRet 0

set cur_design [current_bd_design -quiet]
set list_cells [get_bd_cells -quiet]

if { ${design_name} eq "" } {
   # USE CASES:
   #    1) Design_name not set

   set errMsg "Please set the variable <design_name> to a non-empty value."
   set nRet 1

} elseif { ${cur_design} ne "" && ${list_cells} eq "" } {
   # USE CASES:
   #    2): Current design opened AND is empty AND names same.
   #    3): Current design opened AND is empty AND names diff; design_name NOT in project.
   #    4): Current design opened AND is empty AND names diff; design_name exists in project.

   if { $cur_design ne $design_name } {
      common::send_gid_msg -ssname BD::TCL -id 2001 -severity "INFO" "Changing value of <design_name> from <$design_name> to <$cur_design> since current design is empty."
      set design_name [get_property NAME $cur_design]
   }
   common::send_gid_msg -ssname BD::TCL -id 2002 -severity "INFO" "Constructing design in IPI design <$cur_design>..."

} elseif { ${cur_design} ne "" && $list_cells ne "" && $cur_design eq $design_name } {
   # USE CASES:
   #    5) Current design opened AND has components AND same names.

   set errMsg "Design <$design_name> already exists in your project, please set the variable <design_name> to another value."
   set nRet 1
} elseif { [get_files -quiet ${design_name}.bd] ne "" } {
   # USE CASES: 
   #    6) Current opened design, has components, but diff names, design_name exists in project.
   #    7) No opened design, design_name exists in project.

   set errMsg "Design <$design_name> already exists in your project, please set the variable <design_name> to another value."
   set nRet 2

} else {
   # USE CASES:
   #    8) No opened design, design_name not in project.
   #    9) Current opened design, has components, but diff names, design_name not in project.

   common::send_gid_msg -ssname BD::TCL -id 2003 -severity "INFO" "Currently there is no design <$design_name> in project, so creating one..."

   create_bd_design $design_name

   common::send_gid_msg -ssname BD::TCL -id 2004 -severity "INFO" "Making design <$design_name> as current_bd_design."
   current_bd_design $design_name

}

common::send_gid_msg -ssname BD::TCL -id 2005 -severity "INFO" "Currently the variable <design_name> is equal to \"$design_name\"."

if { $nRet != 0 } {
   catch {common::send_gid_msg -ssname BD::TCL -id 2006 -severity "ERROR" $errMsg}
   return $nRet
}

set bCheckIPsPassed 1
##################################################################
# CHECK IPs
##################################################################
set bCheckIPs 1
if { $bCheckIPs == 1 } {
   set list_check_ips "\ 
xilinx.com:ip:axi_dma:7.1\
xilinx.com:ip:axi_vip:1.1\
xilinx.com:ip:axis_dwidth_converter:1.1\
user.org:user:byte2bit:1.0\
xilinx.com:ip:clk_vip:1.0\
user.org:user:gunzip:1.0\
xilinx.com:ip:proc_sys_reset:5.0\
xilinx.com:ip:rst_vip:1.0\
"

   set list_ips_missing ""
   common::send_gid_msg -ssname BD::TCL -id 2011 -severity "INFO" "Checking if the following IPs exist in the project's IP catalog: $list_check_ips ."

   foreach ip_vlnv $list_check_ips {
      set ip_obj [get_ipdefs -all $ip_vlnv]
      if { $ip_obj eq "" } {
         lappend list_ips_missing $ip_vlnv
      }
   }

   if { $list_ips_missing ne "" } {
      catch {common::send_gid_msg -ssname BD::TCL -id 2012 -severity "ERROR" "The following IPs are not found in the IP Catalog:\n  $list_ips_missing\n\nResolution: Please add the repository containing the IP(s) to the project." }
      set bCheckIPsPassed 0
   }

}

if { $bCheckIPsPassed != 1 } {
  common::send_gid_msg -ssname BD::TCL -id 2023 -severity "WARNING" "Will not continue with creation of design due to the error(s) above."
  return 3
}

##################################################################
# DESIGN PROCs
##################################################################



# Procedure to create entire design; Provide argument to make
# procedure reusable. If parentCell is "", will use root.
proc create_root_design { parentCell } {

  variable script_folder
  variable design_name

  if { $parentCell eq "" } {
     set parentCell [get_bd_cells /]
  }

  # Get object for parentCell
  set parentObj [get_bd_cells $parentCell]
  if { $parentObj == "" } {
     catch {common::send_gid_msg -ssname BD::TCL -id 2090 -severity "ERROR" "Unable to find parent cell <$parentCell>!"}
     return
  }

  # Make sure parentObj is hier blk
  set parentType [get_property TYPE $parentObj]
  if { $parentType ne "hier" } {
     catch {common::send_gid_msg -ssname BD::TCL -id 2091 -severity "ERROR" "Parent <$parentObj> has TYPE = <$parentType>. Expected to be <hier>."}
     return
  }

  # Save current instance; Restore later
  set oldCurInst [current_bd_instance .]

  # Set parent object as current
  current_bd_instance $parentObj


  # Create interface ports

  # Create ports

  # Create instance: axi_dma_mm2s, and set properties
  set axi_dma_mm2s [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_dma:7.1 axi_dma_mm2s ]
  set_property -dict [ list \
   CONFIG.c_include_mm2s {1} \
   CONFIG.c_include_s2mm {0} \
   CONFIG.c_include_sg {0} \
   CONFIG.c_sg_include_stscntrl_strm {0} \
   CONFIG.c_sg_length_width {23} \
 ] $axi_dma_mm2s

  # Create instance: axi_dma_s2mm, and set properties
  set axi_dma_s2mm [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_dma:7.1 axi_dma_s2mm ]
  set_property -dict [ list \
   CONFIG.c_include_mm2s {0} \
   CONFIG.c_include_s2mm {1} \
   CONFIG.c_include_sg {0} \
   CONFIG.c_sg_include_stscntrl_strm {0} \
   CONFIG.c_sg_length_width {23} \
 ] $axi_dma_s2mm

  # Create instance: axi_vip_0_axi_periph, and set properties
  set axi_vip_0_axi_periph [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_interconnect:2.1 axi_vip_0_axi_periph ]
  set_property -dict [ list \
   CONFIG.NUM_MI {2} \
 ] $axi_vip_0_axi_periph

  # Create instance: axi_vip_ctrl, and set properties
  set axi_vip_ctrl [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_vip:1.1 axi_vip_ctrl ]
  set_property -dict [ list \
   CONFIG.ADDR_WIDTH {32} \
   CONFIG.ARUSER_WIDTH {0} \
   CONFIG.AWUSER_WIDTH {0} \
   CONFIG.BUSER_WIDTH {0} \
   CONFIG.DATA_WIDTH {32} \
   CONFIG.HAS_BRESP {1} \
   CONFIG.HAS_BURST {0} \
   CONFIG.HAS_CACHE {0} \
   CONFIG.HAS_LOCK {0} \
   CONFIG.HAS_PROT {1} \
   CONFIG.HAS_QOS {0} \
   CONFIG.HAS_REGION {0} \
   CONFIG.HAS_RRESP {1} \
   CONFIG.HAS_WSTRB {1} \
   CONFIG.ID_WIDTH {0} \
   CONFIG.INTERFACE_MODE {MASTER} \
   CONFIG.PROTOCOL {AXI4LITE} \
   CONFIG.READ_WRITE_MODE {READ_WRITE} \
   CONFIG.RUSER_BITS_PER_BYTE {0} \
   CONFIG.RUSER_WIDTH {0} \
   CONFIG.SUPPORTS_NARROW {0} \
   CONFIG.WUSER_BITS_PER_BYTE {0} \
   CONFIG.WUSER_WIDTH {0} \
 ] $axi_vip_ctrl

  # Create instance: axi_vip_orig, and set properties
  set axi_vip_orig [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_vip:1.1 axi_vip_orig ]
  set_property -dict [ list \
   CONFIG.ADDR_WIDTH {32} \
   CONFIG.DATA_WIDTH {32} \
   CONFIG.INTERFACE_MODE {SLAVE} \
   CONFIG.PROTOCOL {AXI4} \
   CONFIG.READ_WRITE_MODE {READ_WRITE} \
 ] $axi_vip_orig

  # Create instance: axi_vip_res, and set properties
  set axi_vip_res [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_vip:1.1 axi_vip_res ]
  set_property -dict [ list \
   CONFIG.ADDR_WIDTH {32} \
   CONFIG.DATA_WIDTH {32} \
   CONFIG.INTERFACE_MODE {SLAVE} \
   CONFIG.PROTOCOL {AXI4} \
   CONFIG.READ_WRITE_MODE {READ_WRITE} \
 ] $axi_vip_res

  # Create instance: axis_dwidth_converter_0, and set properties
  set axis_dwidth_converter_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axis_dwidth_converter:1.1 axis_dwidth_converter_0 ]
  set_property -dict [ list \
   CONFIG.M_TDATA_NUM_BYTES {1} \
 ] $axis_dwidth_converter_0

  # Create instance: axis_dwidth_converter_1, and set properties
  set axis_dwidth_converter_1 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axis_dwidth_converter:1.1 axis_dwidth_converter_1 ]
  set_property -dict [ list \
   CONFIG.HAS_MI_TKEEP {1} \
   CONFIG.M_TDATA_NUM_BYTES {4} \
 ] $axis_dwidth_converter_1

  # Create instance: byte2bit_0, and set properties
  set byte2bit_0 [ create_bd_cell -type ip -vlnv user.org:user:byte2bit:1.0 byte2bit_0 ]

  # Create instance: clk_vip_0, and set properties
  set clk_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:clk_vip:1.0 clk_vip_0 ]
  set_property -dict [ list \
   CONFIG.FREQ_HZ {200000000} \
   CONFIG.INTERFACE_MODE {MASTER} \
 ] $clk_vip_0

  # Create instance: gunzip_0, and set properties
  set gunzip_0 [ create_bd_cell -type ip -vlnv user.org:user:gunzip:1.0 gunzip_0 ]

  # Create instance: rst_clk_vip_0_200M, and set properties
  set rst_clk_vip_0_200M [ create_bd_cell -type ip -vlnv xilinx.com:ip:proc_sys_reset:5.0 rst_clk_vip_0_200M ]

  # Create instance: rst_vip_0, and set properties
  set rst_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:rst_vip:1.0 rst_vip_0 ]
  set_property -dict [ list \
   CONFIG.INTERFACE_MODE {MASTER} \
 ] $rst_vip_0

  # Create interface connections
  connect_bd_intf_net -intf_net axi_dma_0_M_AXI_S2MM [get_bd_intf_pins axi_dma_s2mm/M_AXI_S2MM] [get_bd_intf_pins axi_vip_res/S_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_dma_0_M_AXI_S2MM]
  connect_bd_intf_net -intf_net axi_dma_1_M_AXIS_MM2S [get_bd_intf_pins axi_dma_mm2s/M_AXIS_MM2S] [get_bd_intf_pins axis_dwidth_converter_0/S_AXIS]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_dma_1_M_AXIS_MM2S]
  connect_bd_intf_net -intf_net axi_dma_1_M_AXI_MM2S [get_bd_intf_pins axi_dma_mm2s/M_AXI_MM2S] [get_bd_intf_pins axi_vip_orig/S_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_dma_1_M_AXI_MM2S]
  connect_bd_intf_net -intf_net axi_vip_0_M_AXI [get_bd_intf_pins axi_vip_0_axi_periph/S00_AXI] [get_bd_intf_pins axi_vip_ctrl/M_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_vip_0_M_AXI]
  connect_bd_intf_net -intf_net axi_vip_0_axi_periph_M00_AXI [get_bd_intf_pins axi_dma_s2mm/S_AXI_LITE] [get_bd_intf_pins axi_vip_0_axi_periph/M00_AXI]
  connect_bd_intf_net -intf_net axi_vip_0_axi_periph_M01_AXI [get_bd_intf_pins axi_dma_mm2s/S_AXI_LITE] [get_bd_intf_pins axi_vip_0_axi_periph/M01_AXI]
  connect_bd_intf_net -intf_net axis_dwidth_converter_0_M_AXIS [get_bd_intf_pins axis_dwidth_converter_0/M_AXIS] [get_bd_intf_pins byte2bit_0/S_AXIS]
  connect_bd_intf_net -intf_net axis_dwidth_converter_1_M_AXIS [get_bd_intf_pins axi_dma_s2mm/S_AXIS_S2MM] [get_bd_intf_pins axis_dwidth_converter_1/M_AXIS]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axis_dwidth_converter_1_M_AXIS]
  connect_bd_intf_net -intf_net byte2bit_0_M_AXIS [get_bd_intf_pins byte2bit_0/M_AXIS] [get_bd_intf_pins gunzip_0/S_IN]
  connect_bd_intf_net -intf_net gunzip_0_M_OUT [get_bd_intf_pins axis_dwidth_converter_1/S_AXIS] [get_bd_intf_pins gunzip_0/M_OUT]

  # Create port connections
  connect_bd_net -net clk_vip_0_clk_out [get_bd_pins axi_dma_mm2s/m_axi_mm2s_aclk] [get_bd_pins axi_dma_mm2s/s_axi_lite_aclk] [get_bd_pins axi_dma_s2mm/m_axi_s2mm_aclk] [get_bd_pins axi_dma_s2mm/s_axi_lite_aclk] [get_bd_pins axi_vip_0_axi_periph/ACLK] [get_bd_pins axi_vip_0_axi_periph/M00_ACLK] [get_bd_pins axi_vip_0_axi_periph/M01_ACLK] [get_bd_pins axi_vip_0_axi_periph/S00_ACLK] [get_bd_pins axi_vip_ctrl/aclk] [get_bd_pins axi_vip_orig/aclk] [get_bd_pins axi_vip_res/aclk] [get_bd_pins axis_dwidth_converter_0/aclk] [get_bd_pins axis_dwidth_converter_1/aclk] [get_bd_pins byte2bit_0/clock] [get_bd_pins clk_vip_0/clk_out] [get_bd_pins gunzip_0/clock] [get_bd_pins rst_clk_vip_0_200M/slowest_sync_clk]
  connect_bd_net -net rst_clk_vip_0_200M_interconnect_aresetn [get_bd_pins axi_vip_0_axi_periph/ARESETN] [get_bd_pins rst_clk_vip_0_200M/interconnect_aresetn]
  connect_bd_net -net rst_clk_vip_0_200M_peripheral_aresetn [get_bd_pins axi_dma_mm2s/axi_resetn] [get_bd_pins axi_dma_s2mm/axi_resetn] [get_bd_pins axi_vip_0_axi_periph/M00_ARESETN] [get_bd_pins axi_vip_0_axi_periph/M01_ARESETN] [get_bd_pins axi_vip_0_axi_periph/S00_ARESETN] [get_bd_pins axi_vip_ctrl/aresetn] [get_bd_pins axi_vip_orig/aresetn] [get_bd_pins axi_vip_res/aresetn] [get_bd_pins axis_dwidth_converter_0/aresetn] [get_bd_pins axis_dwidth_converter_1/aresetn] [get_bd_pins rst_clk_vip_0_200M/peripheral_aresetn]
  connect_bd_net -net rst_clk_vip_0_200M_peripheral_reset [get_bd_pins byte2bit_0/reset] [get_bd_pins gunzip_0/reset] [get_bd_pins rst_clk_vip_0_200M/peripheral_reset]
  connect_bd_net -net rst_vip_0_rst_out1 [get_bd_pins rst_clk_vip_0_200M/ext_reset_in] [get_bd_pins rst_vip_0/rst_out]

  # Create address segments
  assign_bd_address -offset 0x00000000 -range 0x00100000 -target_address_space [get_bd_addr_spaces axi_dma_mm2s/Data_MM2S] [get_bd_addr_segs axi_vip_orig/S_AXI/Reg] -force
  assign_bd_address -offset 0x00000000 -range 0x00100000 -target_address_space [get_bd_addr_spaces axi_dma_s2mm/Data_S2MM] [get_bd_addr_segs axi_vip_res/S_AXI/Reg] -force
  assign_bd_address -offset 0x41E00000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_ctrl/Master_AXI] [get_bd_addr_segs axi_dma_s2mm/S_AXI_LITE/Reg] -force
  assign_bd_address -offset 0x41E10000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_ctrl/Master_AXI] [get_bd_addr_segs axi_dma_mm2s/S_AXI_LITE/Reg] -force


  # Restore current instance
  current_bd_instance $oldCurInst

  validate_bd_design
  save_bd_design
}
# End of create_root_design()


##################################################################
# MAIN FLOW
##################################################################

create_root_design ""


