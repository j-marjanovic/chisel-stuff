# Definitional proc to organize widgets for parameters.
proc init_gui { IPINST } {
  ipgui::add_param $IPINST -name "Component_Name"
  #Adding Page
  set Page_0 [ipgui::add_page $IPINST -name "Page 0"]
  ipgui::add_param $IPINST -name "PULSE_LEN_CYCLES" -parent ${Page_0}


}

proc update_PARAM_VALUE.PULSE_LEN_CYCLES { PARAM_VALUE.PULSE_LEN_CYCLES } {
	# Procedure called to update PULSE_LEN_CYCLES when any of the dependent parameters in the arguments change
}

proc validate_PARAM_VALUE.PULSE_LEN_CYCLES { PARAM_VALUE.PULSE_LEN_CYCLES } {
	# Procedure called to validate PULSE_LEN_CYCLES
	return true
}


proc update_MODELPARAM_VALUE.PULSE_LEN_CYCLES { MODELPARAM_VALUE.PULSE_LEN_CYCLES PARAM_VALUE.PULSE_LEN_CYCLES } {
	# Procedure called to set VHDL generic/Verilog parameter value(s) based on TCL parameter value
	set_property value [get_property value ${PARAM_VALUE.PULSE_LEN_CYCLES}] ${MODELPARAM_VALUE.PULSE_LEN_CYCLES}
}

