#! /usr/bin/env python3

import argparse
import os
import random
import time
from typing import Optional, Dict
from dataclasses import dataclass

from UioDev import get_uio_dev_file
from AxiProxy import AxiProxy
from Udmabuf import Udmabuf


@dataclass
class TestConfig:
    port_name: str
    udmabuf_flags: Optional[int]
    axi_conf: Optional[Dict]


class AxiProxyTest:
    OFFS = 0x100

    def __init__(self, test_config):
        dev = get_uio_dev_file("AxiProxy", search_note=test_config.port_name)
        self.ap = AxiProxy(dev)
        if test_config.udmabuf_flags is not None:
            print(f"udmabuf opened with extra flag = 0x{test_config.udmabuf_flags:x}")
        self.udmabuf = Udmabuf(
            "amba:udmabuf@0x0", extra_flags=test_config.udmabuf_flags
        )

        if test_config.axi_conf is not None:
            print(f"AXI config = {test_config.axi_conf}")
            self.ap.config_axi(**test_config.axi_conf)

        seed = int(time.time())
        print(f"seed for the random gen = {seed}")
        self.rnd = random.Random(seed)

    def test_hw_write_hw_read(self, read_repeats=None):
        print("HW writes, HW reads")

        self.t0 = time.time()

        data_wr = self._hw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._hw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_hw_write_sw_read(self, read_repeats=None):
        print("HW writes, SW reads")

        self.t0 = time.time()

        data_wr = self._hw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._sw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_sw_write_hw_read(self, read_repeats=None):
        print("SW writes, HW reads")

        self.t0 = time.time()

        data_wr = self._sw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._hw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def test_sw_write_sw_read(self, read_repeats=None):
        print("SW writes, SW reads")

        self.t0 = time.time()

        data_wr = self._sw_write()
        for _ in range(read_repeats or 1):
            data_rbv = self._sw_read()
            time.sleep(0.2)

        assert data_wr == data_rbv

    def _hw_write(self):
        data = self.rnd.randint(0, 2 ** 32 - 1)
        duration = self.ap.write(self.udmabuf.phys_addr + self.OFFS, [data, 0, 0, 0])
        print(f"  {self._get_time_str()}", end=" ")
        print(f"AxiProxy: write data = {data:08x}, duration = {duration}")

        return data

    def _hw_read(self):
        data_rbv, duration = self.ap.read(self.udmabuf.phys_addr + self.OFFS)
        print(f"  {self._get_time_str()}", end=" ")
        print(f"AxiProxy: read data  = {data_rbv[0]:08x}, duration = {duration}")

        return data_rbv[0]

    def _sw_write(self):
        data = self.rnd.randint(0, 2 ** 32 - 1)
        self.udmabuf.wr32(self.OFFS, data)
        print(f"  {self._get_time_str()}", end=" ")
        print(f"Udmabuf : write data = {data:08x}")

        return data

    def _sw_read(self):
        data_rd = self.udmabuf.rd32(self.OFFS)
        print(f"  {self._get_time_str()}", end=" ")
        print(f"Udmabuf : read data  = {data_rd:08x}")

        return data_rd

    def _get_time_str(self):
        return f"{time.time() - self.t0:.3f}"


def main():

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

    parser = argparse.ArgumentParser(description="Run a read/write test with AXI Proxy")
    parser.add_argument(
        "port", choices=configs.keys(), help="Specify Zynq MP port to use"
    )
    parser.add_argument("--override-axi-prot", type=int, help="Override AXI AxPROT")
    parser.add_argument("--quirk-read-repeat", type=int, help="Perform repeated reads")

    args = parser.parse_args()

    config = configs[args.port]
    if args.override_axi_prot is not None:
        config.axi_conf["prot"] = args.override_axi_prot

    test = AxiProxyTest(config)
    test.test_hw_write_hw_read(args.quirk_read_repeat)
    test.test_hw_write_sw_read(args.quirk_read_repeat)
    test.test_sw_write_hw_read(args.quirk_read_repeat)
    test.test_sw_write_sw_read(args.quirk_read_repeat)
    print("Test succesfully finished")


if __name__ == "__main__":
    main()
