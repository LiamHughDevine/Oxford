import torch
import torch.nn as nn
import torch.optim as optim
from accuracy import accuracy
from convolutional import Convolutional
from torchvision import datasets, transforms

PATH = "./mnist.pth"
BATCH_SIZE = 64


def main():
    transform = transforms.Compose(
        [transforms.ToTensor(), transforms.Normalize((0.1307,), (0.3081,))]
    )
    dataset = datasets.MNIST("./mnist", train=True, download=True, transform=transform)
    dataset_train, dataset_valid = torch.utils.data.random_split(
        dataset, [50000, 10000]
    )
    train_loader = torch.utils.data.DataLoader(
        dataset_train, batch_size=BATCH_SIZE, shuffle=True, num_workers=2
    )
    valid_loader = torch.utils.data.DataLoader(
        dataset_valid, batch_size=BATCH_SIZE, shuffle=True, num_workers=2
    )

    net = Convolutional()
    net.load_state_dict(torch.load(PATH, weights_only=True))
    criterion = nn.CrossEntropyLoss()
    optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)

    total_epoch = 10
    for epoch in range(total_epoch):
        for inputs, labels in train_loader:

            outputs = net(inputs)
            loss = criterion(outputs, labels)

            optimizer.zero_grad()
            loss.backward()
            optimizer.step()

        train_acc = accuracy(net, train_loader)
        print(f"[{epoch + 1}] train accuracy: {train_acc}")

        valid_acc = accuracy(net, valid_loader)
        print(f"[{epoch + 1}] validate accuracy: {valid_acc}")

        torch.save(net.state_dict(), PATH)

    print("finished training")
    torch.save(net.state_dict(), PATH)


if __name__ == "__main__":
    main()
