# auto-generated with AxiLiteSubordinateGenerator from chisel-bfm-tester

import ctypes


class _AxiTrafficGenMap(ctypes.Structure):
    _pack_ = 1
    _fields_ = [
        ("ID_REG", ctypes.c_uint32),
        ("VERSION", ctypes.c_uint32),
        ("rsvd0x8", ctypes.c_uint32),
        ("SCRATCH", ctypes.c_uint32),
        ("STATUS", ctypes.c_uint32),
        ("CONTROL", ctypes.c_uint32),
        ("rsvd0x18", ctypes.c_uint32),
        ("rsvd0x1c", ctypes.c_uint32),
        ("CONFIG_AXI", ctypes.c_uint32),
        ("rsvd0x24", ctypes.c_uint32),
        ("rsvd0x28", ctypes.c_uint32),
        ("rsvd0x2c", ctypes.c_uint32),
        ("LENGTH", ctypes.c_uint32),
        ("rsvd0x34", ctypes.c_uint32),
        ("rsvd0x38", ctypes.c_uint32),
        ("rsvd0x3c", ctypes.c_uint32),
        ("ADDR_LO", ctypes.c_uint32),
        ("ADDR_HI", ctypes.c_uint32),
        ("rsvd0x48", ctypes.c_uint32),
        ("rsvd0x4c", ctypes.c_uint32),
        ("CNTR_RD_CYC", ctypes.c_uint32),
        ("CNTR_WR_CYC", ctypes.c_uint32),
        ("CNTR_RD_OK", ctypes.c_uint32),
    ]
