#! /usr/bin/env python3

import argparse
import os

from AxiProxyTest import AxiProxyTest
from configs import configs


def main():
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
