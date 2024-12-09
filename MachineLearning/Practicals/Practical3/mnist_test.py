import torch
from accuracy import accuracy
from torchvision import datasets, transforms
from convolutional import Convolutional

PATH = "./mnist.pth"
BATCH_SIZE = 64


def main():

    transform = transforms.Compose(
        [transforms.ToTensor(), transforms.Normalize((0.1307,), (0.3081,))]
    )
    dataset_test = datasets.MNIST("./mnist", train=False, transform=transform)
    test_loader = torch.utils.data.DataLoader(
        dataset_test, batch_size=BATCH_SIZE, shuffle=True, num_workers=2
    )

    net = Convolutional()
    net.load_state_dict(torch.load(PATH, weights_only=True))

    acc = accuracy(net, test_loader)

    print(
        f"Accuracy of the network on the 10000 test images: {acc} %"
    )


if __name__ == "__main__":
    main()
