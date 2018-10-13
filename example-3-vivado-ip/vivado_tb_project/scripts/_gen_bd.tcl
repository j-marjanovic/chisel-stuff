
################################################################
# This is a generated script based on design: example_pipeline_with_axi
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
set scripts_vivado_version 2018.1
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
# source example_pipeline_with_axi_script.tcl

# If there is no project opened, this script will create a
# project, but make sure you do not have an existing project
# <./myproj/project_1.xpr> in the current working folder.

set list_projs [get_projects -quiet]
if { $list_projs eq "" } {
   create_project project_1 myproj -part xcku5p-ffvb676-2-e
   set_property BOARD_PART xilinx.com:kcu116:part0:1.3 [current_project]
}


# CHANGE DESIGN NAME HERE
variable design_name
set design_name example_pipeline_with_axi

# This script was generated for a remote BD. To create a non-remote design,
# change the variable <run_remote_bd_flow> to <0>.

set run_remote_bd_flow 1
if { $run_remote_bd_flow == 1 } {
  # Set the reference directory for source file relative paths (by default 
  # the value is script directory path)
  set origin_dir ./sim

  # Use origin directory path location variable, if specified in the tcl shell
  if { [info exists ::origin_dir_loc] } {
     set origin_dir $::origin_dir_loc
  }

  set str_bd_folder [file normalize ${origin_dir}]
  set str_bd_filepath ${str_bd_folder}/${design_name}/${design_name}.bd

  # Check if remote design exists on disk
  if { [file exists $str_bd_filepath ] == 1 } {
     catch {common::send_msg_id "BD_TCL-110" "ERROR" "The remote BD file path <$str_bd_filepath> already exists!"}
     common::send_msg_id "BD_TCL-008" "INFO" "To create a non-remote BD, change the variable <run_remote_bd_flow> to <0>."
     common::send_msg_id "BD_TCL-009" "INFO" "Also make sure there is no design <$design_name> existing in your current project."

     return 1
  }

  # Check if design exists in memory
  set list_existing_designs [get_bd_designs -quiet $design_name]
  if { $list_existing_designs ne "" } {
     catch {common::send_msg_id "BD_TCL-111" "ERROR" "The design <$design_name> already exists in this project! Will not create the remote BD <$design_name> at the folder <$str_bd_folder>."}

     common::send_msg_id "BD_TCL-010" "INFO" "To create a non-remote BD, change the variable <run_remote_bd_flow> to <0> or please set a different value to variable <design_name>."

     return 1
  }

  # Check if design exists on disk within project
  set list_existing_designs [get_files -quiet */${design_name}.bd]
  if { $list_existing_designs ne "" } {
     catch {common::send_msg_id "BD_TCL-112" "ERROR" "The design <$design_name> already exists in this project at location:
    $list_existing_designs"}
     catch {common::send_msg_id "BD_TCL-113" "ERROR" "Will not create the remote BD <$design_name> at the folder <$str_bd_folder>."}

     common::send_msg_id "BD_TCL-011" "INFO" "To create a non-remote BD, change the variable <run_remote_bd_flow> to <0> or please set a different value to variable <design_name>."

     return 1
  }

  # Now can create the remote BD
  # NOTE - usage of <-dir> will create <$str_bd_folder/$design_name/$design_name.bd>
  create_bd_design -dir $str_bd_folder $design_name
} else {

  # Create regular design
  if { [catch {create_bd_design $design_name} errmsg] } {
     common::send_msg_id "BD_TCL-012" "INFO" "Please set a different value to variable <design_name>."

     return 1
  }
}

current_bd_design $design_name

set bCheckIPsPassed 1
##################################################################
# CHECK IPs
##################################################################
set bCheckIPs 1
if { $bCheckIPs == 1 } {
   set list_check_ips "\ 
user.org:user:PipelineWithAxiLite:1.0\
xilinx.com:ip:axi4stream_vip:1.1\
xilinx.com:ip:axi_vip:1.1\
xilinx.com:ip:clk_vip:1.0\
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

  # Create instance: PipelineWithAxiLite_0, and set properties
  set PipelineWithAxiLite_0 [ create_bd_cell -type ip -vlnv user.org:user:PipelineWithAxiLite:1.0 PipelineWithAxiLite_0 ]

  # Create instance: axi4stream_vip_master, and set properties
  set axi4stream_vip_master [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi4stream_vip:1.1 axi4stream_vip_master ]
  set_property -dict [ list \
   CONFIG.HAS_TREADY {0} \
   CONFIG.INTERFACE_MODE {MASTER} \
   CONFIG.TDATA_NUM_BYTES {2} \
 ] $axi4stream_vip_master

  # Create instance: axi4stream_vip_slave, and set properties
  set axi4stream_vip_slave [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi4stream_vip:1.1 axi4stream_vip_slave ]
  set_property -dict [ list \
   CONFIG.INTERFACE_MODE {SLAVE} \
 ] $axi4stream_vip_slave

  # Create instance: axi_vip_0, and set properties
  set axi_vip_0 [ create_bd_cell -type ip -vlnv xilinx.com:ip:axi_vip:1.1 axi_vip_0 ]
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
   CONFIG.INTERFACE_MODE {MASTER} \
 ] $rst_vip_0

  # Create interface connections
  connect_bd_intf_net -intf_net PipelineWithAxiLite_0_M_AXIS [get_bd_intf_pins PipelineWithAxiLite_0/M_AXIS] [get_bd_intf_pins axi4stream_vip_slave/S_AXIS]
  connect_bd_intf_net -intf_net axi4stream_vip_0_M_AXIS [get_bd_intf_pins PipelineWithAxiLite_0/S_AXIS] [get_bd_intf_pins axi4stream_vip_master/M_AXIS]
  connect_bd_intf_net -intf_net axi_vip_0_M_AXI [get_bd_intf_pins PipelineWithAxiLite_0/S_AXI] [get_bd_intf_pins axi_vip_0/M_AXI]

  # Create port connections
  connect_bd_net -net clk_vip_0_clk_out [get_bd_pins PipelineWithAxiLite_0/clock] [get_bd_pins axi4stream_vip_master/aclk] [get_bd_pins axi4stream_vip_slave/aclk] [get_bd_pins axi_vip_0/aclk] [get_bd_pins clk_vip_0/clk_out] [get_bd_pins proc_sys_reset_0/slowest_sync_clk]
  connect_bd_net -net proc_sys_reset_0_peripheral_aresetn [get_bd_pins axi4stream_vip_master/aresetn] [get_bd_pins axi4stream_vip_slave/aresetn] [get_bd_pins axi_vip_0/aresetn] [get_bd_pins proc_sys_reset_0/peripheral_aresetn]
  connect_bd_net -net proc_sys_reset_0_peripheral_reset [get_bd_pins PipelineWithAxiLite_0/reset] [get_bd_pins proc_sys_reset_0/peripheral_reset]
  connect_bd_net -net rst_vip_0_rst_out [get_bd_pins proc_sys_reset_0/ext_reset_in] [get_bd_pins rst_vip_0/rst_out]

  # Create address segments
  create_bd_addr_seg -range 0x00001000 -offset 0x00001000 [get_bd_addr_spaces axi_vip_0/Master_AXI] [get_bd_addr_segs PipelineWithAxiLite_0/S_AXI/regs] SEG_PipelineWithAxiLite_0_regs

  # Perform GUI Layout
  regenerate_bd_layout -layout_string {
   ExpandedHierarchyInLayout: "",
   guistr: "# # String gsaved with Nlview 6.8.5  2018-01-30 bk=1.4354 VDI=40 GEI=35 GUI=JA:1.6 TLS
#  -string -flagsOSRD
preplace inst axi4stream_vip_master -pg 1 -lvl 3 -y 60 -defaultsOSRD
preplace inst rst_vip_0 -pg 1 -lvl 1 -y 250 -defaultsOSRD
preplace inst clk_vip_0 -pg 1 -lvl 1 -y 150 -defaultsOSRD
preplace inst proc_sys_reset_0 -pg 1 -lvl 2 -y 270 -defaultsOSRD
preplace inst axi_vip_0 -pg 1 -lvl 3 -y 200 -defaultsOSRD
preplace inst PipelineWithAxiLite_0 -pg 1 -lvl 4 -y 230 -defaultsOSRD
preplace inst axi4stream_vip_slave -pg 1 -lvl 5 -y 250 -defaultsOSRD
preplace netloc axi4stream_vip_0_M_AXIS 1 3 1 830
preplace netloc PipelineWithAxiLite_0_M_AXIS 1 4 1 N
preplace netloc rst_vip_0_rst_out 1 1 1 NJ
preplace netloc clk_vip_0_clk_out 1 1 4 210 150 590 130 820 320 1110J
preplace netloc axi_vip_0_M_AXI 1 3 1 N
preplace netloc proc_sys_reset_0_peripheral_reset 1 2 2 NJ 270 830
preplace netloc proc_sys_reset_0_peripheral_aresetn 1 2 3 600 330 NJ 330 1120
levelinfo -pg 1 -30 110 410 710 970 1230 1340 -top -170 -bot 370
",
}

  # Restore current instance
  current_bd_instance $oldCurInst

  save_bd_design
}
# End of create_root_design()


##################################################################
# MAIN FLOW
##################################################################

create_root_design ""


