import logging
import time
from dataclasses import dataclass

from UioDev import UioDev
from _AxiTrafficGenMap import _AxiTrafficGenMap


@dataclass
class AxiTrafficGenStats:
    wr_cyc: int
    rd_cyc: int
    rd_ok: int


class _AxiTrafficGen(_AxiTrafficGenMap):
    BURST_LEN_BEATS = 4  # each burst is 4 beats long
    BEAT_LEN_BYTES = 16  # each beat is 16 bytes
    BURST_LEN_BYTES = BURST_LEN_BEATS * BEAT_LEN_BYTES

    def _create_logger(self):
        self.logger = logging.getLogger(self.__class__.__name__)

    def _reg_uio(self, fd):
        self.logger.debug("_reg_uio(fd = %d)", fd)
        self.fd = fd

    def print_info(self):
        id_reg = self.ID_REG
        ver = self.VERSION
        self.logger.info(f"id  = {id_reg:08x}")
        self.logger.info(f"ver = {ver:08x}")

    def config_axi(self, cache, prot, user):
        config_axi = cache | (prot << 8) | (user << 16)
        self.CONFIG_AXI = config_axi

    def config_addr(self, addr):
        self.ADDR_LO = addr & ((1 << 32) - 1)
        self.ADDR_HI = addr >> 32

    def config_len(self, len_bursts):
        self.LENGTH = len_bursts

    def start_write(self):
        self.CONTROL |= 1

    def start_read(self):
        self.CONTROL |= 2

    def status_get(self):
        return self.STATUS

    def done_clear(self):
        self.CONTROL |= 0x100

    def wait_write_done(self):
        for _ in range(1000):
            if self.STATUS & 1:
                break
            time.sleep(1e-3)
        else:
            raise RuntimeError("Timeout waiting for write to finish")

    def wait_read_done(self):
        for _ in range(1000):
            if self.STATUS & 2:
                break
            time.sleep(1e-3)
        else:
            raise RuntimeError("Timeout waiting for read to finish")

    def get_stats(self):
        wr_cyc = self.CNTR_WR_CYC
        rd_cyc = self.CNTR_RD_CYC
        rd_ok = self.CNTR_RD_OK
        return AxiTrafficGenStats(wr_cyc=wr_cyc, rd_cyc=rd_cyc, rd_ok=rd_ok)


def AxiTrafficGen(dev: UioDev) -> _AxiTrafficGen:
    axi_tg = _AxiTrafficGen.from_buffer(dev.mem)
    axi_tg._create_logger()
    axi_tg._reg_uio(dev.fd)
    return axi_tg
