#! /usr/bin/env python3

"""
Copyright (c) 2021 Jan Marjanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
"""

import ctypes
import logging
import mmap
import os
import struct
import time
from typing import Optional, Tuple


def get_uio_dev_file(search_name: str) -> Optional[Tuple[str, int]]:
    for dev in os.listdir("/sys/class/uio/"):
        name = open(os.path.join("/sys/class/uio", dev, "name"), "r").read().strip()
        if name == search_name:
            size_str = (
                open(os.path.join("/sys/class/uio/", dev, "maps/map0/size"))
                .read()
                .strip()
            )
            size_int = int(size_str, 0)
            return dev, size_int


class UioDev:
    def __init__(self, filename: str, size: int):
        self.fd = os.open(os.path.join("/dev", filename), os.O_RDWR | os.O_SYNC)
        self.mem = mmap.mmap(self.fd, size)

    def __del__(self):
        pass


class _StatusReg(ctypes.Structure):
    _pack_ = 1
    _fields_ = [
        ("DONE", ctypes.c_uint32, 1),
        ("RSVD_31_1", ctypes.c_uint32, 31),
    ]


class _ControlReg(ctypes.Structure):
    _pack_ = 1
    _fields = [
        ("CLEAR", ctypes.c_uint32, 1),
        ("RSVD_30_1", ctypes.c_uint32, 30),
        ("ENABLE", ctypes.c_uint32, 1),
    ]


class _IlaAreaMap(ctypes.Structure):
    _pack_ = 1
    _fields_ = [
        ("ID", ctypes.c_uint32),
        ("VERSION", ctypes.c_uint32),
        ("RSVD_0x8", ctypes.c_uint32),
        ("SCRATCH", ctypes.c_uint32),
        ("STATUS", ctypes.c_uint32),  # TODO: use Structs
        ("CONTROL", ctypes.c_uint32),  # TODO: use Structs
        ("RSVD_0x18", ctypes.c_uint32),
        ("RSVD_0x1c", ctypes.c_uint32),
        ("RSVD_0x20", ctypes.c_uint32),
        ("TRIG_CTRL", ctypes.c_uint32),
        ("RSVD_0x28", ctypes.c_uint32 * ((0x1000 - 0x28) // 4)),
        ("DATA", ctypes.c_uint32 * 4096),
    ]


class _PoorMansSystemILA(_IlaAreaMap):
    def _create_logger(self):
        self.logger = logging.getLogger(self.__class__.__name__)

    def _reg_uio(self, fd):
        self.logger.debug("_reg_uio(fd = %d)", fd)
        self.fd = fd

    def print_info(self):
        id_reg = self.ID
        ver = self.VERSION
        print(f"PoorMansSystemILA, id  = {id_reg:08x}")
        print(f"PoorMansSystemILA, ver = {ver:08x}")

    def wait_for_irq(self):
        self.logger.debug("wait_for_irq()")
        rc = os.write(self.fd, b"\x01\x00\x00\x00")
        self.logger.debug("  write() = %d", rc)
        ret_bs = os.read(self.fd, 4)
        ret = struct.unpack("I", ret_bs)[0]
        self.logger.debug("  read() = %d", ret)

    def clear_and_disable(self):
        self.logger.debug("clear()")
        self.CONTROL = 1

    def set_trigger(self, trig):
        self.logger.debug("set_trigger(%x)", trig)
        self.TRIG_CTRL = trig

    def force_trigger(self):
        self.logger.debug("force_trigger()")
        self.TRIG_CTRL = 0x80000000

    def enable(self):
        self.logger.debug("enable()")
        self.CONTROL = 0x80000000

    def read_buf(self):
        return bytes(self.DATA)


def PoorMansSystemILA(dev: UioDev) -> _PoorMansSystemILA:
    ila = _PoorMansSystemILA.from_buffer(dev.mem)
    ila._create_logger()
    ila._reg_uio(dev.fd)
    return ila


def main():
    logging.basicConfig(level=logging.DEBUG)

    dev_file = get_uio_dev_file("PoorMansSystemILA")
    dev = UioDev(dev_file[0], 64 * 1024)  # TODO: update .dtb

    ila = PoorMansSystemILA(dev)
    ila.print_info()
    ila.enable()
    ila.force_trigger()
    ila.wait_for_irq()
    ila.clear_and_disable()

    data = ila.read_buf()
    filename = f"ila_{int(time.time())}.bin"
    bs_written = open(filename, "wb").write(data)
    print(f"Written {bs_written} to {filename}")


if __name__ == "__main__":
    main()
