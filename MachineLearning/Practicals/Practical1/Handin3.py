import numpy as np
from GetData import get_data
from MSE import mse
from numpy.linalg import inv


def main():
    X_train, y_train, X_test, y_test = get_data()

    mean = np.mean(X_train, axis=0)
    std = np.std(X_train, axis=0)
    std[std == 0] = 1
    print(f"Mean: {mean}")
    print(f"Standard Deviation: {std}")
    print()

    X_norm = X_train - mean
    X_test_norm = X_test - mean
    X_norm /= std
    X_test_norm /= std
    X_normT = X_norm.transpose()

    w0 = np.mean(y_train, axis=0)
    w = np.matmul(np.matmul(inv(np.matmul(X_normT, X_norm)), X_normT), y_train)

    y_train_pred = np.matmul(X_norm, w)
    y_train_pred += w0
    train_error = mse(y_train, y_train_pred)
    print(f"Train Error: {train_error}")

    y_test_pred = np.matmul(X_test_norm, w)
    y_test_pred += w0
    test_error = mse(y_test, y_test_pred)
    print(f"Test Error: {test_error}")


if __name__ == "__main__":
    main()
