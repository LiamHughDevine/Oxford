import _pickle as cp
import numpy as np

def main():
    X, y = cp.load(open('winequality-white.pickle', 'rb'))

    N, D = X.shape
    N_train = int(0.8 * N)
    N_test = N - N_train
    X_train = X[:N_train]
    y_train = y[:N_train]
    X_test = X[N_train:]
    y_test = y[N_train:]

if __name__ == "__main__":
    main()
