import matplotlib.pyplot as plt
import numpy as np
from NBC import NBC
from sklearn.datasets import load_iris
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler


def main():
    X, y = load_iris(return_X_y=True)
    feature_types = ["r", "r", "r", "r"]
    N, D = X.shape
    num_classes = D
    Ntrain = int(0.8 * N)

    runs = 20
    lmd = 10

    nbc_error = np.zeros(10)
    lr_error = np.zeros(10)
    nbc = NBC(feature_types=feature_types, num_classes=num_classes)
    lr = LogisticRegression(max_iter=200, penalty="l1", C=lmd, solver="liblinear")
    #lr = LogisticRegression(max_iter=200)
    lr_pipe = make_pipeline(StandardScaler(), lr)
    for _ in range(runs):
        shuffler = np.random.permutation(N)
        Xtrain = X[shuffler[:Ntrain]]
        ytrain = y[shuffler[:Ntrain]]
        Xtest = X[shuffler[Ntrain:]]
        ytest = y[shuffler[Ntrain:]]
        for k in range(10):
            kTrain = int(0.1 * (k + 1) * Ntrain)
            XKTrain = Xtrain[:kTrain]
            yKTrain = ytrain[:kTrain]

            nbc.fit(XKTrain, yKTrain)
            nbc_yhat = nbc.predict(Xtest)
            nbc_accuracy = np.mean(nbc_yhat == ytest)
            nbc_error[k] += 1 - nbc_accuracy

            lr_pipe.fit(XKTrain, yKTrain)
            lr_yhat = lr_pipe.predict(Xtest)
            lr_accuracy = np.mean(lr_yhat == ytest)
            lr_error[k] += 1 - lr_accuracy

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


if __name__ == "__main__":
    main()
