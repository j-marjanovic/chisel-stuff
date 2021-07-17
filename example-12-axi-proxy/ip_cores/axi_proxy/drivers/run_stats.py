#! /usr/bin/env python3

import json
import sys

from AxiProxyTest import AxiProxyTest
from configs import configs


def main():

    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} OUT_FILENAME")
        return False

    meas = dict()

    for name, config in configs.items():
        print(name)
        test = AxiProxyTest(config)

        durs_rd = []
        durs_wr = []
        durs_ilvd_rd = []
        durs_ilvd_wr = []

        print("HW write")
        for i in range(100):
            _, duration = test.hw_read()
            durs_rd.append(duration)

        print("HW read)")
        for i in range(100):
            _, duration = test.hw_write()
            durs_wr.append(duration)

        print("Interleaved")
        for i in range(100):
            _, duration = test.hw_read()
            durs_ilvd_rd.append(duration)
            _, duration = test.hw_write()
            durs_ilvd_wr.append(duration)

        meas[name] = dict()
        meas[name]["rd"] = durs_rd
        meas[name]["wr"] = durs_wr
        meas[name]["rd_ilvd"] = durs_ilvd_rd
        meas[name]["wr_ilvd"] = durs_ilvd_wr

    print("meas", meas)
    with open(sys.argv[1], "w") as f:
        json.dump(meas, f)


if __name__ == "__main__":
    main()
