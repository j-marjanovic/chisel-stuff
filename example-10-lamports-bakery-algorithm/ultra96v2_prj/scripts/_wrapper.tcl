
update_compile_order -fileset sources_1
set wrapper_file [make_wrapper -files [get_files system.bd] -top]
add_files -norecurse $wrapper_file
