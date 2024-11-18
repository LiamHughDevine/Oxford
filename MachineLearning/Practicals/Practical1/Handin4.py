import matplotlib.pyplot as plt
import numpy as np
from GetData import get_data
from MSE import mse
from numpy.linalg import inv


def train(X_train, y_train, X_test, y_test):
    mean = np.mean(X_train, axis=0)
    std = np.std(X_train, axis=0)
    std[std == 0] = 1

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

    y_test_pred = np.matmul(X_test_norm, w)
    y_test_pred += w0
    test_error = mse(y_test, y_test_pred)

    return (train_error, test_error)


def main():
    X_train, y_train, X_test, y_test = get_data()

    N = X_train.shape[0]
    dataset_size = []
    train_error = []
    test_error = []
    for i in range(20, 600, 20):
        train_err, test_err = train(X_train[:i], y_train[:i], X_test, y_test)
        dataset_size.append(i)
        train_error.append(train_err)
        test_error.append(test_err)

    plt.plot(dataset_size, train_error, label="Train error")
    plt.plot(dataset_size, test_error, label="Test error")
    plt.legend()

    plt.xlabel("Test dataset size")
    plt.ylabel("Error")
    plt.title("Error as dataset size increases")

    plt.show()


if __name__ == "__main__":
    main()
