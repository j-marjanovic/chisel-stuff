#! /usr/bin/env python3

import argparse

import matplotlib.pyplot as plt
import numpy as np


def main():
    parser = argparse.ArgumentParser(
        description="Plot Poor Man's System ILA capture binary"
    )
    parser.add_argument("bin", help="Input file")
    args = parser.parse_args()

    buf = open(args.bin, "rb").read()
    xs = np.frombuffer(buf, dtype=np.uint32)

    # find start of the buffer
    idx = xs >> 16
    am = np.argmax(np.diff(idx)) + 1
    xs = np.roll(xs, -am)
    idx = xs >> 16
    assert np.all(np.diff(idx) == np.ones(len(idx) - 1, dtype=np.uint32))

    # extract individual signals
    mbdebug_tdi = (xs >> 0) & 1
    mbdebug_tdo = (xs >> 1) & 1
    mbdebug_clk = (xs >> 2) & 1
    mbdebug_reg_en = (xs >> 3) & 1
    mbdebug_shift = (xs >> 4) & 1
    mbdebug_capture = (xs >> 5) & 1
    mbdebug_update = (xs >> 6) & 1
    mbdebug_rst = (xs >> 7) & 1
    mbdebug_disable = (xs >> 8) & 1
    debug_sys_reset = (xs >> 9) & 1
    state = (xs >> 10) & 0x3

    signals = [
        (mbdebug_tdi, "mbdebug_tdi"),
        (mbdebug_tdo, "mbdebug_tdo"),
        (mbdebug_clk, "mbdebug_clk"),
        (mbdebug_reg_en, "mbdebug_reg_en"),
        (mbdebug_shift, "mbdebug_shift"),
        (mbdebug_capture, "mbdebug_capture"),
        (mbdebug_update, "mbdebug_update"),
        (mbdebug_rst, "mbdebug_rst"),
        (mbdebug_disable, "mbdebug_disable"),
        (debug_sys_reset, "debug_sys_reset"),
        (state, "state"),
    ]

    fig = plt.figure(figsize=(12, 10))
    axes = fig.subplots(len(signals), 1, sharex=True)

    for ax, signal in zip(axes, signals):
        ax.plot(signal[0], label=signal[1])
        if signal[1] != "state":
            ax.set_ylim(-0.2, 1.2)
        ax.legend(loc=1)

    fig.suptitle(args.bin)
    plt.show()


if __name__ == "__main__":
    main()
