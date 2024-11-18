import matplotlib.pyplot as plt
from GetData import get_data


def main():
    _, y_train, _, _ = get_data()
    y_values = {}
    for pnt in y_train:
        if pnt not in y_values:
            y_values[pnt] = 0
        y_values[pnt] += 1

    keys = list(y_values.keys())
    values = list(y_values.values())

    plt.bar(keys, values, color="blue", width=0.4)

    plt.xlabel("Wine quality")
    plt.ylabel("Frequency")
    plt.title("Data label distribution")

    plt.show()


if __name__ == "__main__":
    main()
