import torch
import torch.nn as nn
import torch.nn.functional as F
from torchvision import models

class SimpleModel(nn.Module):
    def __init__(self):
        super(SimpleModel, self).__init__()
        self.linear = nn.Linear(10, 2)
    
    def forward(self, x):
        return self.linear(x)

model = models.resnet18(pretrained=True)
model.eval()

input_tensor = torch.randn(1, 3, 224, 224, requires_grad=True)

output = model(input_tensor) # Noncompliant {{PyTorch : Disable gradient computation when evaluating a model to save memory and computation time}}

score = output[0].max()


def non_compliant_without_no_grad():
    model = SimpleModel()
    model.eval()  
    
    inputs = torch.randn(1, 10)
    outputs = model(inputs)  # Noncompliant {{PyTorch : Disable gradient computation when evaluating a model to save memory and computation time}}
    
    return outputs

def non_compliant_with_different_model_name():
    my_neural_net = SimpleModel()
    my_neural_net.eval()
    
    inputs = torch.randn(1, 10)
    outputs = my_neural_net(inputs)  # Noncompliant {{PyTorch : Disable gradient computation when evaluating a model to save memory and computation time}}
    
    return outputs

def compliant_with_no_grad():
    model = SimpleModel()
    model.eval()
    
    inputs = torch.randn(1, 10)
    with torch.no_grad():
        outputs = model(inputs)  
    
    return outputs

def compliant_without_eval():
    model = SimpleModel()
    
    inputs = torch.randn(1, 10)
    outputs = model(inputs)  
    
    return outputs

