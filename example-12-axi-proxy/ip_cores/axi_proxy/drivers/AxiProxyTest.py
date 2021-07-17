#! /usr/bin/env python3

import random
import time

from AxiProxy import AxiProxy
from TestConfig import TestConfig
from Udmabuf import Udmabuf
from UioDev import get_uio_dev_file


class AxiProxyTest:
    OFFS = 0x100

    def __init__(self, test_config: TestConfig):
        dev = get_uio_dev_file("AxiProxy", search_note=test_config.port_name)
        self.ap = AxiProxy(dev)

        if test_config.udmabuf_flags is not None:
            print(f"udmabuf opened with extra flag = 0x{test_config.udmabuf_flags:x}")
        self.udmabuf = Udmabuf(
            "amba:udmabuf@0x0", extra_flags=test_config.udmabuf_flags
        )
        assert self.udmabuf._get_value("dma_coherent") == 1

        if test_config.axi_conf is not None:
            print(f"AXI config = {test_config.axi_conf}")
            self.ap.config_axi(**test_config.axi_conf)

        seed = int(time.time())
        print(f"seed for the random gen = {seed}")
        self.rnd = random.Random(seed)

    def test_hw_write_hw_read(self, read_repeats=None):
        print("HW writes, HW reads")

        data_wr, _ = self.hw_write()
        for _ in range(read_repeats or 1):
            data_rbv, _ = self.hw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_hw_write_sw_read(self, read_repeats=None):
        print("HW writes, SW reads")

        data_wr, _ = self.hw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._sw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_sw_write_hw_read(self, read_repeats=None):
        print("SW writes, HW reads")

        data_wr = self._sw_write()
        for _ in range(read_repeats or 1):
            data_rbv, _ = self.hw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_sw_write_sw_read(self, read_repeats=None):
        print("SW writes, SW reads")

        data_wr = self._sw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._sw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def hw_write(self):
        data = self.rnd.randint(0, 2 ** 32 - 1)
        duration = self.ap.write(self.udmabuf.phys_addr + self.OFFS, [data, 0, 0, 0])
        print(f"AxiProxy: write data = {data:08x}, duration = {duration}")

        return data, duration

    def hw_read(self):
        data_rbv, duration = self.ap.read(self.udmabuf.phys_addr + self.OFFS)
        print(f"AxiProxy: read data  = {data_rbv[0]:08x}, duration = {duration}")

        return data_rbv[0], duration

    def _sw_write(self):
        data = self.rnd.randint(0, 2 ** 32 - 1)
        self.udmabuf.wr32(self.OFFS, data)
        print(f"Udmabuf : write data = {data:08x}")

        return data

    def _sw_read(self):
        data_rd = self.udmabuf.rd32(self.OFFS)
        print(f"Udmabuf : read data  = {data_rd:08x}")

        return data_rd
