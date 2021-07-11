import logging
from typing import List

from UioDev import UioDev
from _AxiProxyMap import _AxiProxyMap


class _AxiProxy(_AxiProxyMap):
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

    def read(self, addr):
        assert (self.STATUS >> 8) & 0x3 == 0x3, "FSM is not ready"

        self._set_addr(addr)
        self.CONTROL |= 0x2

        for _ in range(1000):
            status = self.STATUS
            if status & 2:
                break
        else:
            raise RuntimeError("timeout")

        data = [getattr(self, f"DATA_RD{i}") for i in range(4)]

        self.CONTROL |= 0x100

        duration = (self.STATS >> 16) & ((1 << 10) - 1)

        return data, duration

    def write(self, addr: int, data: List[int]):
        assert (self.STATUS >> 8) & 0x3 == 0x3, "FSM is not ready"
        assert len(data) == 4, "data should be 4 elements long"

        self._set_addr(addr)

        for idx, val in enumerate(data):
            setattr(self, f"DATA_WR{idx}", val)

        self.CONTROL |= 0x1

        for _ in range(1000):
            status = self.STATUS
            if status & 1:
                break
        else:
            raise RuntimeError("timeout")

        duration = self.STATS & ((1 << 10) - 1)
        return duration

    def config_axi(self, cache, prot, user):
        config_axi = cache | (prot << 8) | (user << 16)
        self.CONFIG_AXI = config_axi

    def _set_addr(self, addr):
        self.ADDR_HI = addr << 32
        self.ADDR_LO = addr & ((1 << 32) - 1)


def AxiProxy(dev: UioDev) -> _AxiProxy:
    axi_proxy = _AxiProxy.from_buffer(dev.mem)
    axi_proxy._create_logger()
    axi_proxy._reg_uio(dev.fd)
    return axi_proxy
