set_property SOURCE_SET sources_1 [get_filesets sim_1]
add_files -fileset sim_1 ../sim/gunzip_system_tb.sv
add_files -fileset sim_1 ../sim/resources/out.tar
add_files -fileset sim_1 ../sim/resources/in.tar.gz

set_property top gunzip_system_tb [get_filesets sim_1]
