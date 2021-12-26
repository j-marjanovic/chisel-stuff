

create_project axi_tg . -part xczu3eg-sbva484-1-i
set_property board_part avnet.com:ultra96v2:part0:1.1 [current_project]
set_property ip_repo_paths [list ../../ip_cores ../../../example-12-axi-proxy/ultra96v2_prj/ip_cores] [current_project]
update_ip_catalog

add_files -fileset constrs_1 -norecurse ../xdc/pin_assignments.xdc
