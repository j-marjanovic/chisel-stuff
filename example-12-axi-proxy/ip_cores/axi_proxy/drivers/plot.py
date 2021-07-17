#! /usr/bin/env python3

import argparse
import json
from dataclasses import dataclass

import numpy as np
import matplotlib.pyplot as plt


@dataclass
class MeasData:
    hp: np.array
    hpc: np.array
    acp: np.array


def process_meas(data: dict, tx_type: str):
    hp = np.array(data["hp"][tx_type])
    hpc = np.array(data["hpc"][tx_type])
    acp = np.array(data["apc"][tx_type])

    return MeasData(hp=hp, hpc=hpc, acp=acp)


def plot_graph_hist(
    meas_hp: np.array,
    meas_hpc: np.array,
    meas_acp: np.array,
    title: str,
    out_filename: str,
):
    fig = plt.figure(figsize=(12, 8))
    ax1, ax2 = fig.subplots(1, 2, gridspec_kw={"width_ratios": [3, 1]}, sharey=True)

    ax1.plot(meas_hp, label="HP")
    ax1.plot(meas_hpc, label="HPC")
    ax1.plot(meas_acp, label="ACP")
    ax1.grid(True)
    ax1.legend(loc="upper right")
    ax1.set_ylabel("delay [clk cycles]")
    ax1.set_xlabel("iteration")

    delay_max = np.max([np.max(meas_hp), np.max(meas_hpc), np.max(meas_acp)])
    delay_min = np.min([np.min(meas_hp), np.min(meas_hpc), np.min(meas_acp)])

    HIST_CONF = {
        "orientation": "horizontal",
        "alpha": 0.7,
    }
    ax2.hist(meas_hp, label="HP", bins=30, range=(delay_min, delay_max), **HIST_CONF)
    ax2.hist(meas_hpc, label="HPC", bins=30, range=(delay_min, delay_max), **HIST_CONF)
    ax2.hist(meas_acp, label="ACP", bins=30, range=(delay_min, delay_max), **HIST_CONF)
    ax2.grid(True)
    ax2.legend(loc="upper right")
    ax2.set_xlabel("counts")

    fig.suptitle(title)
    plt.savefig(out_filename, dpi=300)


def plot(data):
    data_read = process_meas(data, "rd")
    plot_graph_hist(
        data_read.hp, data_read.hpc, data_read.acp, "Read latency", "latency_rd.png"
    )
    data_read = process_meas(data, "wr")
    plot_graph_hist(
        data_read.hp, data_read.hpc, data_read.acp, "Write latency", "latency_wr.png"
    )


def main():
    parser = argparse.ArgumentParser(description="Plot measurements")

    parser.add_argument("json_file", type=str, help="File with the measurement data")
    args = parser.parse_args()

    with open(args.json_file, "r") as f:
        data = json.load(f)
        plot(data)


if __name__ == "__main__":
    main()
