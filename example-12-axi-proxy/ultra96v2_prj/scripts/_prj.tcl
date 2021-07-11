

create_project axi_proxy . -part xczu3eg-sbva484-1-i
set_property board_part avnet.com:ultra96v2:part0:1.1 [current_project]
set_property ip_repo_paths ../../ip_cores [current_project]
update_ip_catalog
