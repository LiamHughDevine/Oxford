import matplotlib.pyplot as plt
import numpy as np


def test_on_set(nbc, lr, XTrain, yTrain, XTest, yTest):
    nbc.fit(XTrain, yTrain)
    nbc_yhat = nbc.predict(XTest)
    nbc_accuracy = np.mean(nbc_yhat == yTest)
    nbc_error = 1 - nbc_accuracy

    lr.fit(XTrain, yTrain)
    lr_yhat = lr.predict(XTest)
    lr_accuracy = np.mean(lr_yhat == yTest)
    lr_error = 1 - lr_accuracy

    return nbc_error, lr_error


def single_run(nbc, lr, X, y):
    N, _ = X.shape
    Ntrain = int(0.8 * N)

    nbc_error = np.empty(10)
    lr_error = np.empty(10)

    shuffler = np.random.permutation(N)
    XTrain = X[shuffler[:Ntrain]]
    yTrain = y[shuffler[:Ntrain]]
    XTest = X[shuffler[Ntrain:]]
    yTest = y[shuffler[Ntrain:]]

    for k in range(10):
        kTrain = int(0.1 * (k + 1) * Ntrain)
        XKTrain = XTrain[:kTrain]
        yKTrain = yTrain[:kTrain]

        nbc_error[k], lr_error[k] = test_on_set(nbc, lr, XKTrain, yKTrain, XTest, yTest)

    return nbc_error, lr_error


def run_test(nbc, lr, X, y):

    nbc_error = np.zeros(10)
    lr_error = np.zeros(10)

    runs = 20
    for _ in range(runs):
        new_nbc_error, new_lr_error = single_run(nbc, lr, X, y)
        nbc_error += new_nbc_error
        lr_error += new_lr_error

    nbc_average_error = nbc_error / runs
    lr_average_error = lr_error / runs

    keys = list(np.arange(10, 101, 10).tolist())
    nbc_values = list(nbc_average_error)
    lr_values = list(lr_average_error)

    plt.plot(keys, nbc_values, color="blue", label="NBC")
    plt.plot(keys, lr_values, color="orange", label="LR")

    plt.xlabel("Training data percent")
    plt.ylabel("Error")
    plt.legend(loc="upper right")
    plt.title("NBC vs Linear Regression")

    plt.show()
