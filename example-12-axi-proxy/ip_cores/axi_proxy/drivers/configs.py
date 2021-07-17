import os

from TestConfig import TestConfig

configs = {
    "hp": TestConfig(
        "hp",
        udmabuf_flags=os.O_SYNC,
        axi_conf={"cache": 0x0, "prot": 0x0, "user": 0x0},
    ),
    "hpc": TestConfig(
        "hpc", udmabuf_flags=None, axi_conf={"cache": 0xF, "prot": 0x2, "user": 0x1}
    ),
    "apc": TestConfig(
        "acp", udmabuf_flags=None, axi_conf={"cache": 0xF, "prot": 0x2, "user": 0x1}
    ),
}
