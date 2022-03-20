
export_ip_user_files -of_objects [get_files system.bd] -no_script -sync -force -quiet

export_simulation \
  -of_objects [get_files system.bd] \
  -directory ../project/gunzip_tb.ip_user_files/sim_scripts \
  -ip_user_files_dir ../project/gunzip_tb.ip_user_files \
  -ipstatic_source_dir ../project/gunzip_tb.ip_user_files/ipstatic \
  -lib_map_path [list {modelsim=../project/gunzip_tb.cache/compile_simlib/modelsim} \
    {questa=../project/gunzip_tb.cache/compile_simlib/questa} \
    {ies=../project/gunzip_tb.cache/compile_simlib/ies} \
    {xcelium=../project/gunzip_tb.cache/compile_simlib/xcelium} \
    {vcs=../project/gunzip_tb.cache/compile_simlib/vcs} \
    {riviera=../project/gunzip_tb.cache/compile_simlib/riviera}] \
  -use_ip_compiled_libs -force -quiet

launch_simulation


run 100 us
