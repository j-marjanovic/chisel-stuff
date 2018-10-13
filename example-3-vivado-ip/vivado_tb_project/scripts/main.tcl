
# recreate the project from the start
proc prj_init {args} {
    set origin_dir "."

    source [file normalize "$origin_dir/../scripts/_project.tcl"]
    source [file normalize "$origin_dir/../scripts/_gen_bd.tcl"]
}

prj_init
