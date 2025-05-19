import math
import numpy as np

def non_compliant_math_sqrt_in_for_loop():
    numbers = [1, 2, 3, 4, 5]
    results = []
    for num in numbers:
        results.append(math.sqrt(num))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}
    return results

def non_compliant_numpy_scalar_sqrt_in_for_loop():
    numbers = [1, 2, 3, 4, 5]
    results = []
    for num in numbers:
        results.append(np.sqrt(num))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}
    return results

def non_compliant_math_sqrt_in_while_loop():
    numbers = [1, 2, 3, 4, 5]
    results = []
    i = 0
    while i < len(numbers):
        results.append(math.sqrt(numbers[i]))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}
        i += 1
    return results

def compliant_numpy_vectorized_sqrt():
    numbers = np.array([1, 2, 3, 4, 5])
    # Vectorized sqrt directly on the array - efficient
    results = np.sqrt(numbers)
    return results

def compliant_list_comprehension_with_vectorized_operation():
    numbers = [1, 2, 3, 4, 5]
    # Using numpy's vectorized sqrt on the entire array at once
    return np.sqrt(np.array(numbers))

def compliant_math_sqrt_outside_loop():
    # Using math.sqrt outside of a loop is OK
    value = 16
    return math.sqrt(value)