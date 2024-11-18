import numpy as np


def mse(y_true, y_pred):
    error = y_pred - y_true
    square_error = np.power(error, 2)
    return np.mean(square_error)
