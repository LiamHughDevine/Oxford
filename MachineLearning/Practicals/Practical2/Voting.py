import pickle as cp
from NBC import NBC
from RunTest import run_test
from sklearn.linear_model import LogisticRegression


def main():
    X, y = cp.load(open("voting.pickle", "rb"))
    feature_types = "b" * 16
    num_classes = 2

    nbc = NBC(feature_types=feature_types, num_classes=num_classes)
    #lr = LogisticRegression(max_iter=200)
    lmd = 5
    lr = LogisticRegression(max_iter=500, penalty="l1", C=lmd, solver="liblinear")

    run_test(nbc, lr, X, y)


if __name__ == "__main__":
    main()
