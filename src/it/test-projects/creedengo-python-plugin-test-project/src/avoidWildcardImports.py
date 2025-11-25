### Non-compliant cases ###
from math import *  # Noncompliant {{Avoid wildcard imports}}
from os import *  # Noncompliant {{Avoid wildcard imports}}
from sys import *  # Noncompliant {{Avoid wildcard imports}}
from my_module import *  # Noncompliant {{Avoid wildcard imports}}
def some_function():
    from collections import *  # Noncompliant {{Avoid wildcard imports}}
    return deque()
from typing import *  # Noncompliant {{Avoid wildcard imports}}
from itertools import *  # Noncompliant {{Avoid wildcard imports}}
if True:
    from json import *  # Noncompliant {{Avoid wildcard imports}}



### Compliant cases ###
from math import sqrt, pi, sin, cos
from os import path, listdir
import math
import os

import my_module

from datetime import datetime as dt

def another_function():
    from collections import deque
    return deque()

from typing import List, Dict, Optional


import collections
