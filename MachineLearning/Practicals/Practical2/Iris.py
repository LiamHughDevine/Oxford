from NBC import NBC
from RunTest import run_test
from sklearn.datasets import load_iris
from sklearn.linear_model import LogisticRegression


def main():
    X, y = load_iris(return_X_y=True)
    feature_types = ["r", "r", "r", "r"]
    num_classes = 3

    nbc = NBC(feature_types=feature_types, num_classes=num_classes)
    ilmd = 10
    lr = LogisticRegression(max_iter=500, penalty="l1", C=ilmd, solver="liblinear")

    run_test(nbc, lr, X, y)


if __name__ == "__main__":
    main()
