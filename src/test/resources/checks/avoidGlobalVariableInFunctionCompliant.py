# example from official Python documentation

from collections.abc import Sequence
from typing import TypeVar

U = TypeVar('U')                    # Declare type variable "U"

def second(l: Sequence[U]) -> U:    # Function is generic over the TypeVar "U"
    print(U)
    return l[1]