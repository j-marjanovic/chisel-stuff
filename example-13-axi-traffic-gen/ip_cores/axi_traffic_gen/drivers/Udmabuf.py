import os
import mmap
import struct


class Udmabuf:
    def __init__(self, name, extra_flags=None):
        self.fd = os.open(f"/dev/{name}", os.O_RDWR | (extra_flags or 0))
        self.class_path = "/sys/class/u-dma-buf/%s" % name
        self.buf_size = self._get_value("size")
        print("size", self.buf_size)
        self.mem = mmap.mmap(self.fd, self.buf_size)
        self.phys_addr = self._get_value("phys_addr")
        
    def __del__(self):
        os.close(self.fd)

    def rd32(self, addr):
        return struct.unpack("I", self.mem[addr : addr + 4])[0]

    def wr32(self, addr, val):
        bs = struct.pack("I", val)
        self.mem[addr : addr + 4] = bs

    def _get_value(self, name):
        return int(open(self.class_path + "/" + name).read(), 0)
