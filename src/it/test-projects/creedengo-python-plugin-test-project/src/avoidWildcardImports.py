### Non-compliant cases ###
from math import *  # Noncompliant {{Avoid wildcard imports}}
from my_module import *  # Noncompliant {{Avoid wildcard imports}}
def some_function():
    from collections import *  # Noncompliant {{Avoid wildcard imports}}
    return deque()
if True:
    from json import *  # Noncompliant {{Avoid wildcard imports}}


### Compliant cases ###
from math import sqrt, pi, sin, cos
import os

import my_module

from datetime import datetime as dt

def another_function():
    from collections import deque
    return deque()

