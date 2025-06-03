import math
import numpy as np

results = []
numbers = [1, 2, 3, 4, 5]
for n in numbers:
    results.append(math.sqrt(n))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}

results = []
for x in [9, 16, 25]:
    results.append(np.sqrt(x))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}

results = []
i = 0
while i < 3:
    results.append(np.sqrt(i))  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}
    i += 1

results = []
for n in range(10):
    val = math.sqrt(n)  # Noncompliant {{Avoid using scalar sqrt functions in loops. Apply vectorized sqrt operations on arrays directly.}}
    results.append(val)

class DataProcessor:
    numbers = np.array([1, 4, 9])
    sqrt_values = np.sqrt(numbers)

x = math.sqrt(25)

class Analyzer:
    def __init__(self, data):
        self.results = np.sqrt(np.array(data))

intermediate = []
for n in range(3):
    intermediate.append(n)
final = np.sqrt(np.array(intermediate))

results = np.sqrt([x for x in range(5)])

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
    results = np.sqrt(numbers)
    return results

def compliant_list_comprehension_with_vectorized_operation():
    numbers = [1, 2, 3, 4, 5]
    return np.sqrt(np.array(numbers))

def compliant_math_sqrt_outside_loop():
    value = 16
    return math.sqrt(value)