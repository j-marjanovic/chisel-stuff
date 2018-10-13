# Testbench for Vivado

This project instantiates DUT (Chisel module) and (Bus Functional Models) BFMs
for AXI4-Lite and and AXI4-Stream interfaces on DUT. A testbench reads and
writes to internal registers, feeds the DUT with stimuli and captures its
response. At the end the response of the DUT is compared with the expected
values.

## Usage

### Recreating project:

In folder `<TOP>/project/` run:

```tcl
source ../script/main.tcl
```

### Committing changes to BD

In folder `<TOP>/project/` run:

```tcl
write_bd_tcl -include_layout -force ../scripts/_gen_bd.tcl
```

### Commiting changes to project

In folder `<TOP>/project/` run. Be careful when committing the file to Git,
as there are some manual changes made to this file. The manual changes should
not be overwritten.

```tcl
write_project_tcl -use_bd_files -force ../scripts/_project.tcl
```