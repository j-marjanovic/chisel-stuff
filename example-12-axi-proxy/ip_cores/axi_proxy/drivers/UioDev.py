#! /usr/bin/env python3

import mmap
import os
from typing import Optional


class UioDev:
    def __init__(self, filename: str, size: int, addr: int):
        self.fd = os.open(os.path.join("/dev", filename), os.O_RDWR | os.O_SYNC)
        self.mem = mmap.mmap(self.fd, size)
        self.addr = addr

    def __del__(self):
        pass


def get_uio_dev_file(search_name: str, search_note: Optional[str]) -> UioDev:
    for dev in os.listdir("/sys/class/uio/"):
        name = open(os.path.join("/sys/class/uio", dev, "name"), "r").read().strip()

        addr_str = (
            open(os.path.join("/sys/class/uio/", dev, "maps/map0/addr")).read().strip()
        )
        addr = int(addr_str, 0)

        try:
            note_str = (
                open(os.path.join("/sys/class/uio/", dev, "device/of_node/jan-note"))
                .read()
                .strip()
                .rstrip("\x00")
            )
        except FileNotFoundError:
            note_str = None

        if name == search_name and (
            search_note is not None and search_note == note_str
        ):
            size_str = (
                open(os.path.join("/sys/class/uio/", dev, "maps/map0/size"))
                .read()
                .strip()
            )
            size = int(size_str, 0)

            return UioDev(dev, size, addr)

    raise RuntimeError(f"Uio device '{search_name}' not found")
