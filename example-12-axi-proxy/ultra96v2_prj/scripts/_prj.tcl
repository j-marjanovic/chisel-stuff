

create_project axi_proxy . -part xczu3eg-sbva484-1-i
set_property board_part avnet.com:ultra96v2:part0:1.2 [current_project]
set_property ip_repo_paths [list ../../ip_cores ../ip_cores] [current_project]
update_ip_catalog

add_files -fileset constrs_1 -norecurse ../xdc/pin_assignments.xdc
