
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
set scripts_vivado_version 2020.2
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
j-marjanovic.io:user:LamportsBakeryAlgo:1.1\
xilinx.com:ip:axi_bram_ctrl:4.1\
xilinx.com:ip:blk_mem_gen:8.4\
xilinx.com:ip:axi_vip:1.1\
xilinx.com:ip:clk_vip:1.0\
xilinx.com:ip:proc_sys_reset:5.0\
xilinx.com:ip:rst_vip:1.0\
xilinx.com:ip:smartconnect:1.0\
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

  # Create instance: LamportsBakeryAlgori_0, and set properties
  set LamportsBakeryAlgori_0 [ create_bd_cell -type ip -vlnv j-marjanovic.io:user:LamportsBakeryAlgo:1.1 LamportsBakeryAlgori_0 ]

  # Create instance: LamportsBakeryAlgori_1, and set properties
  set LamportsBakeryAlgori_1 [ create_bd_cell -type ip -vlnv j-marjanovic.io:user:LamportsBakeryAlgo:1.1 LamportsBakeryAlgori_1 ]

  # Create instance: LamportsBakeryAlgori_2, and set properties
  set LamportsBakeryAlgori_2 [ create_bd_cell -type ip -vlnv j-marjanovic.io:user:LamportsBakeryAlgo:1.1 LamportsBakeryAlgori_2 ]

  # Create instance: LamportsBakeryAlgori_3, and set properties
  set LamportsBakeryAlgori_3 [ create_bd_cell -type ip -vlnv j-marjanovic.io:user:LamportsBakeryAlgo:1.1 LamportsBakeryAlgori_3 ]

  # Create instance: axi_bram_ctrl_0, and set properties
  set axi_bram_ctrl_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_bram_ctrl:4.1 axi_bram_ctrl_0 ]
  set_property -dict [ list \
   CONFIG.SINGLE_PORT_BRAM {1} \
 ] $axi_bram_ctrl_0

  # Create instance: axi_bram_ctrl_0_bram, and set properties
  set axi_bram_ctrl_0_bram [ create_bd_cell -type ip -vlnv xilinx.com:ip:blk_mem_gen:8.4 axi_bram_ctrl_0_bram ]
  set_property -dict [ list \
   CONFIG.Memory_Type {True_Dual_Port_RAM} \
 ] $axi_bram_ctrl_0_bram

  # Create instance: axi_interconnect_1, and set properties
  set axi_interconnect_1 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_interconnect:2.1 axi_interconnect_1 ]
  set_property -dict [ list \
   CONFIG.NUM_MI {5} \
   CONFIG.NUM_SI {1} \
 ] $axi_interconnect_1

  # Create instance: axi_vip_0, and set properties
  set axi_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_vip:1.1 axi_vip_0 ]
  set_property -dict [ list \
   CONFIG.ADDR_WIDTH {32} \
   CONFIG.ARUSER_WIDTH {0} \
   CONFIG.AWUSER_WIDTH {0} \
   CONFIG.BUSER_WIDTH {0} \
   CONFIG.DATA_WIDTH {32} \
   CONFIG.HAS_BRESP {1} \
   CONFIG.HAS_BURST {1} \
   CONFIG.HAS_CACHE {1} \
   CONFIG.HAS_LOCK {1} \
   CONFIG.HAS_PROT {1} \
   CONFIG.HAS_QOS {1} \
   CONFIG.HAS_REGION {1} \
   CONFIG.HAS_RRESP {1} \
   CONFIG.HAS_WSTRB {1} \
   CONFIG.ID_WIDTH {0} \
   CONFIG.INTERFACE_MODE {MASTER} \
   CONFIG.PROTOCOL {AXI4} \
   CONFIG.READ_WRITE_MODE {READ_WRITE} \
   CONFIG.RUSER_BITS_PER_BYTE {0} \
   CONFIG.RUSER_WIDTH {0} \
   CONFIG.SUPPORTS_NARROW {1} \
   CONFIG.WUSER_BITS_PER_BYTE {0} \
   CONFIG.WUSER_WIDTH {0} \
 ] $axi_vip_0

  # Create instance: clk_vip_0, and set properties
  set clk_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:clk_vip:1.0 clk_vip_0 ]
  set_property -dict [ list \
   CONFIG.INTERFACE_MODE {MASTER} \
 ] $clk_vip_0

  # Create instance: proc_sys_reset_0, and set properties
  set proc_sys_reset_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:proc_sys_reset:5.0 proc_sys_reset_0 ]

  # Create instance: rst_vip_0, and set properties
  set rst_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:rst_vip:1.0 rst_vip_0 ]
  set_property -dict [ list \
   CONFIG.ASYNCHRONOUS {NO} \
   CONFIG.INTERFACE_MODE {MASTER} \
 ] $rst_vip_0

  # Create instance: smartconnect_0, and set properties
  set smartconnect_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:smartconnect:1.0 smartconnect_0 ]
  set_property -dict [ list \
   CONFIG.NUM_MI {1} \
   CONFIG.NUM_SI {5} \
 ] $smartconnect_0

  # Create interface connections
  connect_bd_intf_net -intf_net LamportsBakeryAlgori_0_M [get_bd_intf_pins LamportsBakeryAlgori_0/M] [get_bd_intf_pins smartconnect_0/S01_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /LamportsBakeryAlgori_0_M]
  connect_bd_intf_net -intf_net LamportsBakeryAlgori_1_M [get_bd_intf_pins LamportsBakeryAlgori_1/M] [get_bd_intf_pins smartconnect_0/S02_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /LamportsBakeryAlgori_1_M]
  connect_bd_intf_net -intf_net LamportsBakeryAlgori_2_M [get_bd_intf_pins LamportsBakeryAlgori_2/M] [get_bd_intf_pins smartconnect_0/S03_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /LamportsBakeryAlgori_2_M]
  connect_bd_intf_net -intf_net LamportsBakeryAlgori_3_M [get_bd_intf_pins LamportsBakeryAlgori_3/M] [get_bd_intf_pins smartconnect_0/S04_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /LamportsBakeryAlgori_3_M]
  connect_bd_intf_net -intf_net S00_AXI_1 [get_bd_intf_pins axi_interconnect_1/S00_AXI] [get_bd_intf_pins axi_vip_0/M_AXI]
  connect_bd_intf_net -intf_net axi_bram_ctrl_0_BRAM_PORTA [get_bd_intf_pins axi_bram_ctrl_0/BRAM_PORTA] [get_bd_intf_pins axi_bram_ctrl_0_bram/BRAM_PORTA]
  connect_bd_intf_net -intf_net axi_interconnect_1_M00_AXI [get_bd_intf_pins axi_interconnect_1/M00_AXI] [get_bd_intf_pins smartconnect_0/S00_AXI]
  connect_bd_intf_net -intf_net axi_interconnect_1_M01_AXI [get_bd_intf_pins LamportsBakeryAlgori_0/CTRL] [get_bd_intf_pins axi_interconnect_1/M01_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_interconnect_1_M01_AXI]
  connect_bd_intf_net -intf_net axi_interconnect_1_M02_AXI [get_bd_intf_pins LamportsBakeryAlgori_1/CTRL] [get_bd_intf_pins axi_interconnect_1/M02_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_interconnect_1_M02_AXI]
  connect_bd_intf_net -intf_net axi_interconnect_1_M03_AXI [get_bd_intf_pins LamportsBakeryAlgori_2/CTRL] [get_bd_intf_pins axi_interconnect_1/M03_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_interconnect_1_M03_AXI]
  connect_bd_intf_net -intf_net axi_interconnect_1_M04_AXI [get_bd_intf_pins LamportsBakeryAlgori_3/CTRL] [get_bd_intf_pins axi_interconnect_1/M04_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /axi_interconnect_1_M04_AXI]
  connect_bd_intf_net -intf_net smartconnect_0_M00_AXI [get_bd_intf_pins axi_bram_ctrl_0/S_AXI] [get_bd_intf_pins smartconnect_0/M00_AXI]
  set_property SIM_ATTRIBUTE.MARK_SIM "true" [get_bd_intf_nets /smartconnect_0_M00_AXI]

  # Create port connections
  connect_bd_net -net clk_vip_0_clk_out [get_bd_pins LamportsBakeryAlgori_0/clock] [get_bd_pins LamportsBakeryAlgori_1/clock] [get_bd_pins LamportsBakeryAlgori_2/clock] [get_bd_pins LamportsBakeryAlgori_3/clock] [get_bd_pins axi_bram_ctrl_0/s_axi_aclk] [get_bd_pins axi_interconnect_1/ACLK] [get_bd_pins axi_interconnect_1/M00_ACLK] [get_bd_pins axi_interconnect_1/M01_ACLK] [get_bd_pins axi_interconnect_1/M02_ACLK] [get_bd_pins axi_interconnect_1/M03_ACLK] [get_bd_pins axi_interconnect_1/M04_ACLK] [get_bd_pins axi_interconnect_1/S00_ACLK] [get_bd_pins axi_vip_0/aclk] [get_bd_pins clk_vip_0/clk_out] [get_bd_pins proc_sys_reset_0/slowest_sync_clk] [get_bd_pins rst_vip_0/sync_clk] [get_bd_pins smartconnect_0/aclk]
  connect_bd_net -net proc_sys_reset_0_peripheral_reset [get_bd_pins LamportsBakeryAlgori_0/reset] [get_bd_pins LamportsBakeryAlgori_1/reset] [get_bd_pins LamportsBakeryAlgori_2/reset] [get_bd_pins LamportsBakeryAlgori_3/reset] [get_bd_pins proc_sys_reset_0/peripheral_reset]
  connect_bd_net -net rst_vip_0_rst_out [get_bd_pins axi_bram_ctrl_0/s_axi_aresetn] [get_bd_pins axi_interconnect_1/ARESETN] [get_bd_pins axi_interconnect_1/M00_ARESETN] [get_bd_pins axi_interconnect_1/M01_ARESETN] [get_bd_pins axi_interconnect_1/M02_ARESETN] [get_bd_pins axi_interconnect_1/M03_ARESETN] [get_bd_pins axi_interconnect_1/M04_ARESETN] [get_bd_pins axi_interconnect_1/S00_ARESETN] [get_bd_pins axi_vip_0/aresetn] [get_bd_pins proc_sys_reset_0/peripheral_aresetn] [get_bd_pins smartconnect_0/aresetn]
  connect_bd_net -net rst_vip_0_rst_out1 [get_bd_pins proc_sys_reset_0/ext_reset_in] [get_bd_pins rst_vip_0/rst_out]

  # Create address segments
  assign_bd_address -offset 0xC0000000 -range 0x00002000 -target_address_space [get_bd_addr_spaces LamportsBakeryAlgori_0/M] [get_bd_addr_segs axi_bram_ctrl_0/S_AXI/Mem0] -force
  assign_bd_address -offset 0xC0000000 -range 0x00002000 -target_address_space [get_bd_addr_spaces LamportsBakeryAlgori_1/M] [get_bd_addr_segs axi_bram_ctrl_0/S_AXI/Mem0] -force
  assign_bd_address -offset 0xC0000000 -range 0x00002000 -target_address_space [get_bd_addr_spaces LamportsBakeryAlgori_2/M] [get_bd_addr_segs axi_bram_ctrl_0/S_AXI/Mem0] -force
  assign_bd_address -offset 0xC0000000 -range 0x00002000 -target_address_space [get_bd_addr_spaces LamportsBakeryAlgori_3/M] [get_bd_addr_segs axi_bram_ctrl_0/S_AXI/Mem0] -force
  assign_bd_address -offset 0x44A00000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs LamportsBakeryAlgori_0/CTRL/reg0] -force
  assign_bd_address -offset 0x44A10000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs LamportsBakeryAlgori_1/CTRL/reg0] -force
  assign_bd_address -offset 0x44A20000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs LamportsBakeryAlgori_2/CTRL/reg0] -force
  assign_bd_address -offset 0x44A30000 -range 0x00010000 -target_address_space [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs LamportsBakeryAlgori_3/CTRL/reg0] -force
  assign_bd_address -offset 0xC0000000 -range 0x00002000 -target_address_space [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs axi_bram_ctrl_0/S_AXI/Mem0] -force


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


