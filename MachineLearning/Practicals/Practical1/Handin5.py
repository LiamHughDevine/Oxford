import matplotlib.pyplot as plt
import numpy as np
from GetData import get_data
from MSE import mse
from numpy.linalg import inv
from sklearn.linear_model import Lasso
from sklearn.linear_model import Ridge
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import PolynomialFeatures
from sklearn.preprocessing import StandardScaler


def get_ridge_pipe(X_train, y_train, lmd):
    pipe = make_pipeline(StandardScaler(), PolynomialFeatures(2), Ridge(alpha=lmd))
    pipe.fit(X_train, y_train)
    return pipe


def get_lasso_pipe(X_train, y_train, lmd):
    pipe = make_pipeline(StandardScaler(), PolynomialFeatures(2), Lasso(alpha=lmd))
    pipe.fit(X_train, y_train)
    return pipe


def main():
    X_train, y_train, X_test, y_test = get_data()

    N = len(X_train)
    validation = N // 5

    X_hyper_train = X_train[: 4 * validation]
    y_hyper_train = y_train[: 4 * validation]
    X_validation = X_train[4 * validation :]
    y_validation = y_train[4 * validation :]

    best_ridge_error = float("inf")
    best_ridge_lmd = -1
    best_lasso_error = float("inf")
    best_lasso_lmd = -1

    for i in range(5):
        lmd = pow(10, i - 2)

        ridge_pipe = get_ridge_pipe(X_hyper_train, y_hyper_train, lmd)
        ridge_pred = ridge_pipe.predict(X_validation)
        ridge_error = mse(ridge_pred, y_validation)
        if ridge_error < best_ridge_error:
            best_ridge_error = ridge_error
            best_ridge_lmd = lmd

        lasso_pipe = get_lasso_pipe(X_hyper_train, y_hyper_train, lmd)
        lasso_pred = lasso_pipe.predict(X_validation)
        lasso_error = mse(lasso_pred, y_validation)
        if lasso_error < best_lasso_error:
            best_lasso_error = lasso_error
            best_lasso_lmd = lmd

    print(f"Best ridge lambda = {best_ridge_lmd}")
    ridge_pipe = get_ridge_pipe(X_train, y_train, best_ridge_lmd)
    ridge_pred = ridge_pipe.predict(X_test)
    ridge_error = mse(ridge_pred, y_test)
    print(f"Ridge error: {ridge_error}")

    print(f"Best lasso lambda = {best_lasso_lmd}")
    lasso_pipe = get_lasso_pipe(X_train, y_train, best_lasso_lmd)
    lasso_pred = lasso_pipe.predict(X_test)
    lasso_error = mse(lasso_pred, y_test)
    print(f"Lasso error: {lasso_error}")


if __name__ == "__main__":
    main()
