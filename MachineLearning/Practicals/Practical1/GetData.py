import _pickle as cp
import numpy as np


def get_data():
    X, y = cp.load(open("winequality-white.pickle", "rb"))

    N, D = X.shape
    N_train = int(0.8 * N)
    N_test = N - N_train
    X_train = X[:N_train]
    y_train = y[:N_train]
    X_test = X[N_train:]
    y_test = y[N_train:]
    return (X_train, y_train, X_test, y_test)
