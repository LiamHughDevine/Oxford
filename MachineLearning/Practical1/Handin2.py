import numpy as np
from GetData import get_data
from MSE import mse


def main():
    _, y_train, _, y_test = get_data()
    mean = np.mean(y_train)
    N = y_test.size
    y_pred = np.full((N), mean)
    error = mse(y_test, y_pred)
    print(f"Error: {error}")


if __name__ == "__main__":
    main()
