# Case 1: Standard imports
import numpy
import torch

numpy_array1 = numpy.array([1, 2, 3, 4])

compliant1 = torch.from_numpy(numpy_array1)

non_compliant1 = torch.tensor(numpy_array1)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 2: Aliased imports
import numpy as np
import torch as tt

numpy_array2 = np.array([5, 6, 7, 8])

compliant2 = tt.from_numpy(numpy_array2)

non_compliant2 = tt.tensor(numpy_array2)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 3: From imports
from numpy import array
from torch import tensor, from_numpy

numpy_array3 = array([9, 10, 11, 12])

compliant3 = from_numpy(numpy_array3)

non_compliant3 = tensor(numpy_array3)  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 4: From imports with aliases
from numpy import array as np_arr
from torch import tensor as t_tensor, from_numpy as t_from_numpy

numpy_array4 = np_arr([13, 14, 15, 16])

compliant4 = t_from_numpy(numpy_array4)

# Case 5: Direct np call as function argument
non_compliant5 = tt.tensor(np.array([1, 2, 3]))  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}

# Case 6: Alias direct np call as function argument
non_compliant6 = t_tensor(np.array([1, 2, 3]))  # Noncompliant {{Use torch.from_numpy() instead of torch.tensor() to create tensors from numpy arrays}}


