import torch.nn as nn
import torch.nn.functional as F


class RandomClass(Object):
    def __init__(self, a):
        self.a = a
    def forward(self, x):
        return self.a + x

class WeirdModelWithoutForward(nn.Module):
    def __init__(self, a):
        self.a = a

class WeirdModelWithoutInit(nn.Module):

    def forward(x):
        return x

class NetWithConvBiasSetToTrueWithARandomChange(nn.Module):
    def __init__(self):
        super(NetWithConvBiasSetToTrueWithARandomChange, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=True)
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)

    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = self.conv2(x)
        x = x / 2
        x = self.conv2_bn(x)
        x = F.relu(F.max_pool2d(x, 2))
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))


class NetWithConvBiasSetToTrueWithARandomAddedLineBetweenConvAndBN(nn.Module):
    def __init__(self):
        super(NetWithConvBiasSetToTrueWithARandomAddedLineBetweenConvAndBN, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1, bias=False)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=True) # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
        self.idx = 0
    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = self.conv2(x)
        self.idx += 1
        x = self.conv2_bn(x)
        x = F.relu(F.max_pool2d(x, 2))
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))

class NetWithConvBiasSetToTrueWithDiffVariableName(nn.Module):
    def __init__(self):
        super(NetWithConvBiasSetToTrueWithDiffVariableName, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1, bias=False)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=True) # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
        self.idx = 0
    def forward(self, x):
        x1 = F.relu(F.max_pool2d(self.conv1(x), 2))
        x2 = self.conv2(x1)
        self.idx += 1
        x3 = self.conv2_bn(x2)
        x4 = F.relu(F.max_pool2d(x3, 2))
        x5 = x4.view(-1, 320)
        x6 = F.relu(self.dense1_bn(self.dense1(x5)))
        return F.relu(self.dense2(x6))

class CompNetWithConvBiasSetToTrueWithDiffVariableName(nn.Module):
    def __init__(self):
        super(CompNetWithConvBiasSetToTrueWithDiffVariableName, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1, bias=False)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=False)
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
        self.idx = 0
    def forward(self, x):
        x1 = F.relu(F.max_pool2d(self.conv1(x), 2))
        x2 = self.conv2(x1)
        self.idx += 1
        x3 = self.conv2_bn(x2)
        x4 = F.relu(F.max_pool2d(x3, 2))
        x5 = x4.view(-1, 320)
        x6 = F.relu(self.dense1_bn(self.dense1(x5)))
        return F.relu(self.dense2(x6))

class NetWithConvBiasSetToTrue(nn.Module):
    def __init__(self):
        super(NetWithConvBiasSetToTrue, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=True) # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)

    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = self.conv2(x)
        x = self.conv2_bn(x)
        x = F.relu(F.max_pool2d(x, 2))
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))

class NetWithDefaultConvBias(nn.Module):
    def __init__(self):
        super(NetWithDefaultConvBias, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=True) # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)

    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = F.relu(F.max_pool2d(self.conv2_bn(self.conv2(x)), 2))
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))

class NonCompliantNetWithSequentialKeywordParam(nn.Module):
    def __init__(self):
        super(NonCompliantNetWithSequentialKeywordParam, self).__init__()
        self.encoder = nn.Sequential(
                        nn.Conv2d(in_channels=1, out_channels=10, kernel_size=5, stride=1),
                        nn.MaxPool2d(2),
                        nn.ReLU(),
                        nn.Conv2d(10, 20, kernel_size=5, bias=True), # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
                        nn.BatchNorm2d(20),
                        nn.MaxPool2d(2),
                        nn.ReLU()
            )
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
    def forward(self, x):
        x = self.encoder(x)
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))


class NonCompliantNetWithSequentialPosParam(nn.Module):
    def __init__(self):
        super(NonCompliantNetWithSequentialPosParam, self).__init__()
        self.encoder = nn.Sequential(
                        nn.Conv2d(in_channels=1, out_channels=10, kernel_size=5, stride=1, bias=False),
                        nn.MaxPool2d(2),
                        nn.ReLU(),
                        nn.Conv2d(10, 20, kernel_size=5), # Noncompliant {{Remove bias for convolutions before batch norm layers to save time and memory.}}
                        nn.BatchNorm2d(20),
                        nn.MaxPool2d(2),
                        nn.ReLU()
            )
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
    def forward(self, x):
        x = self.encoder(x)
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))



class CompliantNet(nn.Module):
    def __init__(self):
        super(CompliantNet, self).__init__()
        self.conv1 = nn.Conv2d(in_channels=1, out_channels=10,
                               kernel_size=5,
                               stride=1)
        self.conv2 = nn.Conv2d(10, 20, kernel_size=5, bias=False)
        self.conv2_bn = nn.BatchNorm2d(20)
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)

    def forward(self, x):
        x = F.relu(F.max_pool2d(self.conv1(x), 2))
        x = F.relu(F.max_pool2d(self.conv2_bn(self.conv2(x)), 2))
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))


class CompliantNetWithSequential(nn.Module):
    def __init__(self):
        super(CompliantNetWithSequential, self).__init__()
        self.encoder = nn.Sequential(
                        nn.Conv2d(in_channels=1, out_channels=10, kernel_size=5, stride=1),
                        nn.MaxPool2d(2),
                        nn.ReLU(),
                        nn.Conv2d(10, 20, kernel_size=5, bias=False),
                        nn.BatchNorm2d(20),
                        nn.MaxPool2d(2),
                        nn.ReLU()
            )
        self.dense1 = nn.Linear(in_features=320, out_features=50)
        self.dense1_bn = nn.BatchNorm1d(50)
        self.dense2 = nn.Linear(50, 10)
    def forward(self, x):
        x = self.encoder(x)
        x = x.view(-1, 320)
        x = F.relu(self.dense1_bn(self.dense1(x)))
        return F.relu(self.dense2(x))



