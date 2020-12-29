#! /usr/bin/env python3

import random

import numpy as np

N = 128
fill_factor = 0.2

random.seed(1234)

m = np.zeros((N, N), dtype=np.float16)
non_zero_els = int(N * N * fill_factor)

for _ in range(non_zero_els):
    while True:
        i = random.randint(0, N) - 1
        j = random.randint(0, N - 1)
        if m[i, j] != 0:
            continue
        v = random.random()
        m[i, j] = v
        break

np.save("dense_matrix_with_sparse_els.npy", m)
