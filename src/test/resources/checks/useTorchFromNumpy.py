# Case 1: Standard imports
import numpy
import torch

numpy_array = numpy.array([1, 2, 3, 4])

compliant1 = torch.from_numpy(numpy_array) # Compliant

non_compliant1 = torch.tensor(numpy_array)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 2: Aliased imports
import numpy as np
import torch as tt

compliant2 = tt.from_numpy(numpy_array) # Compliant

non_compliant2 = tt.tensor(numpy_array)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 3: From imports
from numpy import array
from torch import tensor, from_numpy


compliant3 = from_numpy(numpy_array) # Compliant

non_compliant3 = tensor(numpy_array)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 4: From imports with aliases
from numpy import array as np_arr
from torch import tensor as t_tensor, from_numpy as t_from_numpy


compliant4 = t_from_numpy(numpy_array) # Compliant
non_compliant4 = t_tensor(numpy_array) # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 5: Direct np call as function argument
compliant5 = tt.from_numpy(np.array([1, 2, 3]))
non_compliant5 = tt.tensor(np.array([1, 2, 3]))  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 6: Alias direct np call as function argument
compliant5 = t_from_numpy(np.array([1, 2, 3]))
non_compliant6 = t_tensor(np.array([1, 2, 3]))  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}


