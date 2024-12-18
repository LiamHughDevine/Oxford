import torch
import torch.nn as nn
import torch.nn.functional as F


class Convolutional(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv1 = nn.Conv2d(1, 25, 12, stride=2)
        self.conv2 = nn.Conv2d(25, 64, 5, stride=1, padding="valid")
        self.pool = nn.MaxPool2d(2, 2)
        self.fc1 = nn.Linear(64 * 2 * 2, 1024)
        self.fc2 = nn.Linear(1024, 10)
        self.dropout = nn.Dropout(p=0.2)

    def forward(self, x):
        x = F.relu(self.conv1(x))
        x = self.pool(F.relu(self.conv2(x)))
        x = torch.flatten(x, 1)
        x = F.relu(self.fc1(x))
        x = self.dropout(x)
        x = self.fc2(x)
        return x
