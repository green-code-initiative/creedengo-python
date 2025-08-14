import torch
from torch.utils.data import DataLoader
from torch.utils.data import DataLoader as DL
import torch.utils as utils
import nottorch

dl = torch.utils.data.DataLoader(dataset) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = torch.utils.data.DataLoader(dataset, num_workers=3, batch_size=1, shuffle=False, pin_memory=False) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = DataLoader(dataset, batch_size=1, num_workers=0, shuffle=True, pin_memory=False) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = torch.utils.data.DataLoader(num_workers=5, batch_size=2, shuffle=True) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = DataLoader(dataset, 1, False, None, None, 0, None, False, False) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = utils.data.DataLoader(dataset, batch_size=1, False, None, None, 0, None, False, True) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = utils.data.DataLoader(dataset, pin_memory=False) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}
dl = DL(dataset, pin_memory=False) # Noncompliant {{Use pinned memory to reduce data transfer in RAM.}}

dl = torch.utils.data.DataLoader(dataset, num_workers=3, batch_size=1, shuffle=False, pin_memory=True)
dl = DataLoader(dataset, num_workers=0, batch_size=1, shuffle=False, pin_memory=True)
dl = DataLoader(dataset, num_workers=0, batch_size=1, shuffle=False, pin_memory=True)
dl = utils.data.DataLoader(dataset, pin_memory=True)
dl = utils.data.DataLoader(dataset, batch_size=1, False, None, None, 0, None, True, True)
dl = DataLoader(dataset, batch_size=1, False, None, None, 0, None, True, False)
dl = torch.utils.data.DataLoader(torchvision.datasets.MNIST('/files/', train=True, download=True), pin_memory=True)
dl = DL(dataset, pin_memory=True)

dl = nottorch.utils.data.DataLoader(dataset, pin_memory=True)
dl = nottorch.utils.data.DataLoader(dataset, pin_memory=False)
