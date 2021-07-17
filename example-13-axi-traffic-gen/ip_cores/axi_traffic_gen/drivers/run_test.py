#! /usr/bin/env python3

import json
import logging
import sys

from AxiTrafficGen import AxiTrafficGen
from UioDev import get_uio_dev_file
from Udmabuf import Udmabuf
from configs import configs


class TrafficGenTest:
    def __init__(self, config):
        dev = get_uio_dev_file("AxiTrafficGen", search_note=config.port_name)
        self.axi_tg = AxiTrafficGen(dev)
        self.axi_tg.print_info()

        if config.udmabuf_flags is not None:
            print(f"udmabuf opened with extra flag = 0x{config.udmabuf_flags:x}")
        self.udmabuf = Udmabuf("amba:udmabuf@0x0", extra_flags=config.udmabuf_flags)
        assert self.udmabuf._get_value("dma_coherent") == 1

        self.axi_tg.config_addr(self.udmabuf.phys_addr)
        if config.axi_conf is not None:
            print(f"AXI config = {config.axi_conf}")
            self.axi_tg.config_axi(**config.axi_conf)

    def run(self, size_burst):
        self._clean_mem(size_burst)

        self.axi_tg.config_len(size_burst)
        self.axi_tg.start_write()
        self.axi_tg.wait_write_done()

        self.axi_tg.start_read()
        self.axi_tg.wait_read_done()
        self.axi_tg.done_clear()

        stats = self.axi_tg.get_stats()
        assert stats.rd_ok == size_burst * self.axi_tg.BURST_LEN_BEATS

        self._check_mem(size_burst)
        size_bytes = size_burst * self.axi_tg.BURST_LEN_BYTES
        print(
            f"{size_bytes:8} | rd = {stats.rd_cyc}, wr = {stats.wr_cyc} ",
            flush=True,
        )

        return size_bytes, stats.rd_cyc, stats.wr_cyc

    def _clean_mem(self, size_burst):
        for byte_addr in range(size_burst * self.axi_tg.BURST_LEN_BYTES // 4):
            self.udmabuf.wr32(byte_addr * 4, -1 & 0xFFFFFFFF)

    def _check_mem(self, size_burst):
        exp_val = [0] * self.axi_tg.BURST_LEN_BEATS

        for burst_addr in range(size_burst):
            for beat_offs in range(self.axi_tg.BURST_LEN_BEATS):
                for byte_offs in range(0, self.axi_tg.BEAT_LEN_BYTES, 4):
                    byte_addr = (
                        burst_addr * self.axi_tg.BURST_LEN_BYTES
                        + beat_offs * self.axi_tg.BEAT_LEN_BYTES
                        + byte_offs
                    )
                    data = self.udmabuf.rd32(byte_addr)
                    assert data == exp_val[byte_offs // 4]

                exp_val[0] += 1  # cascading not necessary


def main():
    NR_MEAS_PER_SIZE = 5
    logging.basicConfig(level=logging.DEBUG)

    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} OUT_FILENAME")
        return False

    meas = dict()

    for name, config in configs.items():
        print(name)
        test = TrafficGenTest(config)

        sizes = [2 ** i for i in range(20)]
        sizes_bytes = []
        durs_rd = []
        durs_wr = []

        for size in sizes:
            for _ in range(NR_MEAS_PER_SIZE):
                size_bytes, dur_rd, dur_wr = test.run(size)
                sizes_bytes.append(size_bytes)
                durs_rd.append(dur_rd)
                durs_wr.append(dur_wr)

        meas[name] = dict()
        meas[name]["size"] = sizes
        meas[name]["rd"] = durs_rd
        meas[name]["wr"] = durs_wr

    print(meas)
    with open(sys.argv[1], "w") as f:
        json.dump(meas, f)


if __name__ == "__main__":
    main()
