import numpy as np
import torch as tt

np_array = np.array([1, 2, 3])


torch_tensor = tt.from_numpy(np_array) # Compliant

torch = tt.tensor(np_array) # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}