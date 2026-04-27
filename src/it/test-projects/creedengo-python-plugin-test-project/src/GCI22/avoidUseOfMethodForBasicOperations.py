a = 10
b = 3
x = 5
y = 8
n1 = 0b1010
n2 = 0b1100

#Arithmetic operations:
result_add = a.__add__(b)        # Noncompliant {{Avoid using methods for simple basic operations.}}
result_sub = a.__sub__(b)        # Noncompliant {{Avoid using methods for simple basic operations.}}
result_mul = a.__mul__(b)        # Noncompliant {{Avoid using methods for simple basic operations.}}
result_div = a.__truediv__(b)    # Noncompliant {{Avoid using methods for simple basic operations.}}
result_mod = a.__mod__(b)        # Noncompliant {{Avoid using methods for simple basic operations.}}
result_pow = a.__pow__(b)        # Noncompliant {{Avoid using methods for simple basic operations.}}

#Comparisons:
is_equal   = x.__eq__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}
is_greater = x.__gt__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}
is_less    = x.__lt__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}
is_gte     = x.__ge__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}
is_lte     = x.__le__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}
is_not_eq  = x.__ne__(y)         # Noncompliant {{Avoid using methods for simple basic operations.}}

#Collection size:
my_list = [1, 2, 3, 4, 5]
size = my_list.__len__()         # Noncompliant {{Avoid using methods for simple basic operations.}}

#Found element:
words = ["This", "is", "a", "test"]
found = list.__contains__(words, "test")  # Noncompliant {{Avoid using methods for simple basic operations.}}

#String concatenation:
first = "Hello"
last  = "World"
greeting = first.__add__(", " + last)        # Noncompliant {{Avoid using methods for simple basic operations.}}
greeting = "".join([first, ", ", last])      # Noncompliant {{Avoid using methods for simple basic operations.}}

#Operations:
flag_a = True
flag_b = False
result_and = flag_a.__and__(flag_b)          # Noncompliant {{Avoid using methods for simple basic operations.}}
result_or  = flag_a.__or__(flag_b)           # Noncompliant {{Avoid using methods for simple basic operations.}}

bit_and    = n1.__and__(n2)      # Noncompliant {{Avoid using methods for simple basic operations.}}
bit_or     = n1.__or__(n2)       # Noncompliant {{Avoid using methods for simple basic operations.}}
bit_xor    = n1.__xor__(n2)      # Noncompliant {{Avoid using methods for simple basic operations.}}
bit_lshift = n1.__lshift__(2)    # Noncompliant {{Avoid using methods for simple basic operations.}}
bit_rshift = n1.__rshift__(2)    # Noncompliant {{Avoid using methods for simple basic operations.}}

#Sequence repetition:
repeated_str  = "abc".__mul__(3) # Noncompliant {{Avoid using methods for simple basic operations.}}
repeated_list = [0].__mul__(5)   # Noncompliant {{Avoid using methods for simple basic operations.}}

#Access / modification / deletion of elements:
data     = {"key": 42}
my_list2 = [10, 20, 30]
val = data.__getitem__("key")    # Noncompliant {{Avoid using methods for simple basic operations.}}
my_list2.__setitem__(1, 99)      # Noncompliant {{Avoid using methods for simple basic operations.}}
my_list2.__delitem__(0)          # Noncompliant {{Avoid using methods for simple basic operations.}}

#Accumulation in a loop:
numbers = range(10)
squares = []
for n in numbers:
    squares.append(n ** 2)       # Noncompliant {{Avoid using methods for simple basic operations.}}

#Trivial user-defined functions wrapping a basic operation:

def add(a, b):
    return a + b                 # Noncompliant {{Avoid using methods for simple basic operations.}}

def multiply(a, b):
    return a * b                 # Noncompliant {{Avoid using methods for simple basic operations.}}

def is_eq(a, b):
    return a == b                # Noncompliant {{Avoid using methods for simple basic operations.}}

def is_gt(a, b):
    return a > b                 # Noncompliant {{Avoid using methods for simple basic operations.}}

def contains(collection, item):
    return item in collection    # Noncompliant {{Avoid using methods for simple basic operations.}}

def concat(a, b):
    return a + b                 # Noncompliant {{Avoid using methods for simple basic operations.}}


# ===========================================================================

a = 10
b = 3
x = 5
y = 8
n1 = 0b1010
n2 = 0b1100

result_add = a + b               # Compliant {{Native operator.}}
result_sub = a - b               # Compliant {{Native operator.}}
result_mul = a * b               # Compliant {{Native operator.}}
result_div = a / b               # Compliant {{Native operator.}}
result_mod = a % b               # Compliant {{Native operator.}}
result_pow = a ** b              # Compliant {{Native operator.}}

is_equal   = x == y              # Compliant {{Native operator.}}
is_greater = x > y               # Compliant {{Native operator.}}
is_less    = x < y               # Compliant {{Native operator.}}
is_gte     = x >= y              # Compliant {{Native operator.}}
is_lte     = x <= y              # Compliant {{Native operator.}}
is_not_eq  = x != y              # Compliant {{Native operator.}}

my_list = [1, 2, 3, 4, 5]
size = len(my_list)              # Compliant {{use of len().}}

words = ["This", "is", "a", "test"]
found = "test" in words      # Compliant {{Native operator.}}

first = "Hello"
last  = "World"
greeting = first + ", " + last   # Compliant {{Native operator.}}
greeting = f"{first}, {last}"    # Compliant {{use of f-string.}}

words = ["word"] * 1000
sentence = " ".join(words)       # Compliant {{use of join().}}

flag_a = True
flag_b = False
result_and = flag_a and flag_b   # Compliant {{Native operator.}}
result_or  = flag_a or flag_b    # Compliant {{Native operator.}}

bit_and    = n1 & n2             # Compliant {{Native operator.}}
bit_or     = n1 | n2             # Compliant {{Native operator.}}
bit_xor    = n1 ^ n2             # Compliant {{Native operator.}}
bit_lshift = n1 << 2             # Compliant {{Native operator.}}
bit_rshift = n1 >> 2             # Compliant {{Native operator.}}

repeated_str  = "abc" * 3        # Compliant {{Native operator.}}
repeated_list = [0] * 5          # Compliant {{Native operator.}}

data     = {"key": 42}
my_list2 = [10, 20, 30]
val         = data["key"]        # Compliant {{Native subscript syntax.}}
my_list2[1] = 99                 # Compliant {{Native subscript syntax.}}
del my_list2[0]                  # Compliant {{Native operator.}}

numbers = range(10)
squares = [n ** 2 for n in numbers]  # Compliant {{Native syntax.}}

values = [3, 1, 7, 2]
total   = sum(values)            # Compliant {Use of sum().}}
minimum = min(values)            # Compliant {{Use of min().}}
maximum = max(values)            # Compliant {{Use of max().}}
flags   = [True, False, True]
check   = all(flags)             # Compliant {{Use of all().}}
check   = any(flags)             # Compliant {{Use of any().}}

def is_adult(age):
    return age >= 18             # Compliant {{Not a trivial operator wrapper.}}

def tax_rate_applies(amount):
    return amount > 1000         # Compliant {{Not a trivial operator wrapper.}}