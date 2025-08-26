import numpy as np
import torch

def non_compliant_random_rand():
    tensor = torch.tensor(np.random.rand(1000, 1000)) # Noncompliant {{Directly create tensors as torch.Tensor instead of using numpy functions.}}

def compliant_random_rand():
    tensor = torch.rand([1000, 1000])

def compliant_zeros():
    tensor_ = torch.zeros(1, 2)
    print(tensor_)

def non_compliant_zeros():
    tensor_ = torch.IntTensor(np.zeros(1, 2)) # Noncompliant {{Directly create tensors as torch.Tensor instead of using numpy functions.}}
    print(tensor_)

def non_compliant_eye():
    tensor = torch.cuda.LongTensor(np.eye(5)) # Noncompliant {{Directly create tensors as torch.Tensor instead of using numpy functions.}}

def non_compliant_ones():
    import numpy
    from torch import FloatTensor
    tensor = FloatTensor(data=np.ones(shape=(1, 5))) # Noncompliant {{Directly create tensors as torch.Tensor instead of using numpy functions.}}
