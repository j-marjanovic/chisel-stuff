#! /usr/bin/env python3

import argparse
import json
from dataclasses import dataclass

import numpy as np
import matplotlib.pyplot as plt


@dataclass
class MeasData:
    sizes: np.array
    rd_beat_per_cyc: np.array
    wr_beat_per_cyc: np.array
    sizes_avg: np.array
    rd_beat_per_cyc_avg: np.array
    wr_beat_per_cyc_avg: np.array


def process_meas(data):
    SAMP_PER_SIZE = len(data["rd"]) // len(data["size"])

    sizes_avg = np.array(data["size"])
    sizes = np.repeat(sizes_avg.reshape((-1, 1)), SAMP_PER_SIZE, axis=1).reshape(-1)

    rd = np.array(data["rd"])
    wr = np.array(data["wr"])

    avg = lambda xs: np.mean(xs.reshape((-1, SAMP_PER_SIZE)), axis=1)
    rd_avg = avg(rd)
    wr_avg = avg(wr)

    rd_beat_per_cyc = (sizes * 4) / rd
    wr_beat_per_cyc = (sizes * 4) / wr
    rd_beat_per_cyc_avg = (sizes_avg * 4) / rd_avg
    wr_beat_per_cyc_avg = (sizes_avg * 4) / wr_avg

    return MeasData(
        sizes * 4 * 16,
        rd_beat_per_cyc,
        wr_beat_per_cyc,
        sizes_avg * 4 * 16,
        rd_beat_per_cyc_avg,
        wr_beat_per_cyc_avg,
    )


def plot(data):
    data_apc = process_meas(data["apc"])
    data_hpc = process_meas(data["hpc"])
    data_hp = process_meas(data["hp"])

    fig = plt.figure(figsize=(12, 12))

    ax2, ax1 = fig.subplots(2, 1, sharex=True)

    ax1.semilogx(data_apc.sizes, data_apc.rd_beat_per_cyc, "x", color="C2", label="ACP")
    ax1.semilogx(data_apc.sizes_avg, data_apc.rd_beat_per_cyc_avg, color="C2", label="ACP (avg)")
    ax1.semilogx(data_hpc.sizes, data_hpc.rd_beat_per_cyc, "x", color="C1", label="HPC")
    ax1.semilogx(data_hpc.sizes_avg, data_hpc.rd_beat_per_cyc_avg, color="C1", label="HPC (avg)")
    ax1.semilogx(data_hp.sizes, data_hp.rd_beat_per_cyc, "x", color="C0", label="HP")
    ax1.semilogx(data_hp.sizes_avg, data_hp.rd_beat_per_cyc_avg, color="C0", label="HP (avg)")

    ax1.set_ylim((0, 1))
    ax1.grid(True)
    ax1.legend()
    ax1.set_title("read throughput")
    ax1.set_ylabel("beats / clk cycle")
    ax1.set_xscale("log", base=2)

    ax2.semilogx(data_apc.sizes, data_apc.wr_beat_per_cyc, "x", color="C2", label="ACP")
    ax2.semilogx(data_apc.sizes_avg, data_apc.wr_beat_per_cyc_avg, color="C2", label="ACP (avg)")
    ax2.semilogx(data_hpc.sizes, data_hpc.wr_beat_per_cyc, "x", color="C1", label="HPC")
    ax2.semilogx(data_hpc.sizes_avg, data_hpc.wr_beat_per_cyc_avg, color="C1", label="HPC (avg)")
    ax2.semilogx(data_hp.sizes, data_hp.wr_beat_per_cyc, "x", color="C0", label="HP")
    ax2.semilogx(data_hp.sizes_avg, data_hp.wr_beat_per_cyc_avg, color="C0", label="HP (avg)")

    ax2.set_ylim((0, 1))
    ax2.grid(True)
    ax2.legend()
    ax2.set_title("write throughput")
    ax2.set_ylabel("beats / clk cycle")
    ax2.set_xscale("log", base=2)

    ax1.set_xlabel("transfer size [bytes]")

    plt.savefig("throughput.png", dpi=150)


def main():
    parser = argparse.ArgumentParser(description="Plot measurements")

    parser.add_argument("json_file", type=str, help="File with the measurement data")
    args = parser.parse_args()

    with open(args.json_file, "r") as f:
        data = json.load(f)
        plot(data)


if __name__ == "__main__":
    main()
