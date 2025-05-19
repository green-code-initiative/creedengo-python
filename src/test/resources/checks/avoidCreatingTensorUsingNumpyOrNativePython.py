import numpy as np
import torch

def non_compliant_random_rand():
    tensor = torch.tensor(np.random.rand(1000, 1000)) # Noncompliant {{Directly create tensors as torch.Tensor. Use torch.rand instead of numpy.random.rand.}}

def compliant_random_rand():
    tensor = torch.rand([1000, 1000])

def compliant_zeros():
    tensor_ = torch.zeros(1, 2)
    print(tensor_)

def non_compliant_zeros():
    tensor_ = torch.IntTensor(np.zeros(1, 2)) # Noncompliant {{Directly create tensors as torch.Tensor. Use torch.zeros instead of numpy.zeros.}}
    print(tensor_)

def non_compliant_eye():
    tensor = torch.cuda.LongTensor(np.eye(5)) # Noncompliant {{Directly create tensors as torch.Tensor. Use torch.eye instead of numpy.eye.}}

def non_compliant_ones():
    import numpy
    from torch import FloatTensor
    tensor = FloatTensor(data=np.ones(shape=(1, 5))) # Noncompliant {{Directly create tensors as torch.Tensor. Use torch.ones instead of numpy.ones.}}
