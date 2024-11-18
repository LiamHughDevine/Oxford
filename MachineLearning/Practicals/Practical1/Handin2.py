import numpy as np
from GetData import get_data
from MSE import mse


def main():
    _, y_train, _, y_test = get_data()
    mean = np.mean(y_train)
    M = y_train.size
    N = y_test.size
    train_y_pred = np.full((M), mean)
    test_y_pred = np.full((N), mean)
    train_error = mse(y_train, train_y_pred)
    print(f"Error: {train_error}")
    test_error = mse(y_test, test_y_pred)
    print(f"Error: {test_error}")


if __name__ == "__main__":
    main()
