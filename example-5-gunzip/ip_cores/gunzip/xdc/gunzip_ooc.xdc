
create_clock -period 5.000 -name clock [get_ports clock]

set_input_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports reset]
set_input_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports reset]

set_output_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_in_ready]
set_output_delay -clock [get_clocks clock] -max -add_delay 2.000 [get_ports io_data_in_ready]

set_input_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_in_valid]
set_input_delay -clock [get_clocks clock] -max -add_delay 2.000 [get_ports io_data_in_valid]

set_input_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_in_bits]
set_input_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports io_data_in_bits]

set_input_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_out_ready]
set_input_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports io_data_out_ready]

set_output_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_out_valid]
set_output_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports io_data_out_valid]

set_output_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports {io_data_out_bits_data[*]}]
set_output_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports {io_data_out_bits_data[*]}]

set_output_delay -clock [get_clocks clock] -min -add_delay 0.100 [get_ports io_data_out_bits_last]
set_output_delay -clock [get_clocks clock] -max -add_delay 2.500 [get_ports io_data_out_bits_last]
