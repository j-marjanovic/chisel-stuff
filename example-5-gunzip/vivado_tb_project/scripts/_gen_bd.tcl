
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
set scripts_vivado_version 2018.3
set current_vivado_version [version -short]

if { [string first $scripts_vivado_version $current_vivado_version] == -1 } {
   puts ""
   catch {common::send_msg_id "BD_TCL-109" "ERROR" "This script was generated using Vivado <$scripts_vivado_version> and is being run in <$current_vivado_version> of Vivado. Please run the script in Vivado <$scripts_vivado_version> then open the design in Vivado <$current_vivado_version>. Upgrade the design by running \"Tools => Report => Report IP Status...\", then run write_bd_tcl to create an updated script."}

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
   create_project project_1 myproj -part xczu9eg-ffvb1156-2-e
   set_property BOARD_PART xilinx.com:zcu102:part0:3.2 [current_project]
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
      common::send_msg_id "BD_TCL-001" "INFO" "Changing value of <design_name> from <$design_name> to <$cur_design> since current design is empty."
      set design_name [get_property NAME $cur_design]
   }
   common::send_msg_id "BD_TCL-002" "INFO" "Constructing design in IPI design <$cur_design>..."

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

   common::send_msg_id "BD_TCL-003" "INFO" "Currently there is no design <$design_name> in project, so creating one..."

   create_bd_design $design_name

   common::send_msg_id "BD_TCL-004" "INFO" "Making design <$design_name> as current_bd_design."
   current_bd_design $design_name

}

common::send_msg_id "BD_TCL-005" "INFO" "Currently the variable <design_name> is equal to \"$design_name\"."

if { $nRet != 0 } {
   catch {common::send_msg_id "BD_TCL-114" "ERROR" $errMsg}
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
   common::send_msg_id "BD_TCL-006" "INFO" "Checking if the following IPs exist in the project's IP catalog: $list_check_ips ."

   foreach ip_vlnv $list_check_ips {
      set ip_obj [get_ipdefs -all $ip_vlnv]
      if { $ip_obj eq "" } {
         lappend list_ips_missing $ip_vlnv
      }
   }

   if { $list_ips_missing ne "" } {
      catch {common::send_msg_id "BD_TCL-115" "ERROR" "The following IPs are not found in the IP Catalog:\n  $list_ips_missing\n\nResolution: Please add the repository containing the IP(s) to the project." }
      set bCheckIPsPassed 0
   }

}

if { $bCheckIPsPassed != 1 } {
  common::send_msg_id "BD_TCL-1003" "WARNING" "Will not continue with creation of design due to the error(s) above."
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
     catch {common::send_msg_id "BD_TCL-100" "ERROR" "Unable to find parent cell <$parentCell>!"}
     return
  }

  # Make sure parentObj is hier blk
  set parentType [get_property TYPE $parentObj]
  if { $parentType ne "hier" } {
     catch {common::send_msg_id "BD_TCL-101" "ERROR" "Parent <$parentObj> has TYPE = <$parentType>. Expected to be <hier>."}
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
  create_bd_addr_seg -range 0x00100000 -offset 0x00000000 [get_bd_addr_spaces axi_dma_mm2s/Data_MM2S] [get_bd_addr_segs axi_vip_orig/S_AXI/Reg] SEG_axi_vip_orig_Reg
  create_bd_addr_seg -range 0x00100000 -offset 0x00000000 [get_bd_addr_spaces axi_dma_s2mm/Data_S2MM] [get_bd_addr_segs axi_vip_res/S_AXI/Reg] SEG_axi_vip_res_Reg
  create_bd_addr_seg -range 0x00010000 -offset 0x41E00000 [get_bd_addr_spaces axi_vip_ctrl/Master_AXI] [get_bd_addr_segs axi_dma_s2mm/S_AXI_LITE/Reg] SEG_axi_dma_0_Reg
  create_bd_addr_seg -range 0x00010000 -offset 0x41E10000 [get_bd_addr_spaces axi_vip_ctrl/Master_AXI] [get_bd_addr_segs axi_dma_mm2s/S_AXI_LITE/Reg] SEG_axi_dma_1_Reg

  # Perform GUI Layout
  regenerate_bd_layout -layout_string {
   "ExpandedHierarchyInLayout":"",
   "guistr":"# # String gsaved with Nlview 6.8.11  2018-08-07 bk=1.4403 VDI=40 GEI=35 GUI=JA:9.0 TLS
#  -string -flagsOSRD
preplace inst rst_vip_0 -pg 1 -lvl 1 -y 160 -defaultsOSRD
preplace inst axi_vip_res -pg 1 -lvl 11 -y 230 -defaultsOSRD
preplace inst axi_dma_mm2s -pg 1 -lvl 5 -y 290 -defaultsOSRD
preplace inst clk_vip_0 -pg 1 -lvl 1 -y 60 -defaultsOSRD
preplace inst axi_vip_ctrl -pg 1 -lvl 3 -y 110 -defaultsOSRD
preplace inst byte2bit_0 -pg 1 -lvl 7 -y 320 -defaultsOSRD
preplace inst rst_clk_vip_0_200M -pg 1 -lvl 2 -y 180 -defaultsOSRD
preplace inst axis_dwidth_converter_0 -pg 1 -lvl 6 -y 300 -defaultsOSRD
preplace inst axi_vip_orig -pg 1 -lvl 6 -y 110 -defaultsOSRD
preplace inst axis_dwidth_converter_1 -pg 1 -lvl 9 -y 360 -defaultsOSRD
preplace inst axi_vip_0_axi_periph -pg 1 -lvl 4 -y 200 -defaultsOSRD
preplace inst axi_dma_s2mm -pg 1 -lvl 10 -y 230 -defaultsOSRD
preplace inst gunzip_0 -pg 1 -lvl 8 -y 280 -defaultsOSRD
preplace netloc axis_dwidth_converter_0_M_AXIS 1 6 1 N
preplace netloc axi_dma_1_M_AXIS_MM2S 1 5 1 N
preplace netloc axi_dma_1_M_AXI_MM2S 1 5 1 1430
preplace netloc axi_vip_0_axi_periph_M01_AXI 1 4 1 1050
preplace netloc byte2bit_0_M_AXIS 1 7 1 1900
preplace netloc axi_vip_0_axi_periph_M00_AXI 1 4 6 NJ 190 NJ 190 NJ 190 NJ 190 NJ 190 N
preplace netloc rst_clk_vip_0_200M_interconnect_aresetn 1 2 2 N 200 720J
preplace netloc clk_vip_0_clk_out 1 1 10 140 80 510 180 730 340 1050 380 1440 380 1680 400 1910 360 2130 250 2360 130 2740
preplace netloc rst_clk_vip_0_200M_peripheral_aresetn 1 2 9 520 210 740 350 1060 390 1450 420 N 420 N 420 2140 280 2380 330 2740
preplace netloc axi_vip_0_M_AXI 1 3 1 720
preplace netloc gunzip_0_M_OUT 1 8 1 2120
preplace netloc rst_clk_vip_0_200M_peripheral_reset 1 2 6 500J 30 NJ 30 NJ 30 NJ 30 1670 410 1920
preplace netloc axi_dma_0_M_AXI_S2MM 1 10 1 N
preplace netloc rst_vip_0_rst_out1 1 1 1 NJ
preplace netloc axis_dwidth_converter_1_M_AXIS 1 9 1 2370
levelinfo -pg 1 -70 40 320 620 900 1250 1560 1790 2020 2250 2560 2830 2930 -top -80 -bot 580
"
}

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


