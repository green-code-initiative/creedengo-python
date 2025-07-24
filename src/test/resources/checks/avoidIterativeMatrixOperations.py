import numpy as np

# Test 1: Simple dot product
a = [1, 2, 3, 4]
b = [2, 3, 4, 5]

dot = 0
for i in range(len(a)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    dot += a[i] * b[i]

dot_numpy = np.dot(a, b) # Compliant

# Test 2: Matrix dot product
A = [[1, 2], [3, 4]]
B = [[5, 6], [7, 8]]

def iterative_matrix_product(A, B):
    results = [[0 for _ in range(len(B[0]))] for _ in range(len(A))]

    for i in range(len(A)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
        for j in range(len(B[0])):
            for k in range(len(B)):
                results[i][j] += A[i][k] * B[k][j]

    return results

results = iterative_matrix_product(A, B)

results_numpy = np.dot(A, B) # Compliant

# Test 3: Outer product
x = np.random.rand(100)
y = np.random.rand(100)

o = np.zeros((len(x), len(y)))
for i in range(len(x)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for j in range(len(y)):
        o[i][j] = x[i] * y[j]

outer_numpy = np.outer(x, y) # Compliant

# Test 4: Dot product with different variable names
vec1 = [1, 2, 3]
vec2 = [4, 5, 6]
res = 0
for idx in range(3): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    res += vec1[idx] * vec2[idx]

# Test 5: False positive - scalar addition in loop
total = 0
for i in range(10):
    total += i  # Compliant

# Test 6: False positive - unrelated list indexing
c = [10, 20, 30]
d = [5, 6, 7]
e = []
for i in range(len(c)):
    e.append(c[i] + d[i])  # Compliant

# Test 7: Dot product in list comprehension (should be compliant)
dp = sum([a[i] * b[i] for i in range(len(a))])  # Compliant

# Test 8: Double subscription but not matrix op
m = [[1, 2], [3, 4]]
n = [[5, 6], [7, 8]]
for i in range(len(m)):
    for j in range(len(n)):
        print(m[i][j] + n[i][j])  # Compliant

# Test 9: Outer product with extra operation
x = [1, 2]
y = [3, 4]
result = [[0]*len(y) for _ in range(len(x))]
for i in range(len(x)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for j in range(len(y)):
        result[i][j] = x[i] * y[j] + 1 

# Test 9: Outer product with extra operation
x = [1, 2]
y = [3, 4]
result = [[0]*len(y) for _ in range(len(x))]
for i in range(len(x)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for j in range(len(y)):
        result[i][j] = x[i] * y[j] -10

# Test 10: 3-level nested matrix product with aliases
X = [[1, 2], [3, 4]]
Y = [[5, 6], [7, 8]]
Z = [[0, 0], [0, 0]]
for r in range(2): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for c in range(2):
        for t in range(2):
            Z[r][c] += X[r][t] * Y[t][c] 

# Test 11: False positive - nested loops without multiplication
total = 0
for i in range(10):
    for j in range(5):
        total += i + j  # Compliant

# Test 12: Matrix dot with transpose
M1 = [[1, 2], [3, 4]]
M2 = [[5, 7], [6, 8]]
out = [[0 for _ in range(len(M2))] for _ in range(len(M1))]
for i in range(len(M1)): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for j in range(len(M2)):
        for k in range(len(M1[0])):
            out[i][j] += M1[i][k] * M2[j][k]  # Transposed multiplication

# Test 13: Outer product with offset indexing (still counts)
x = [1, 2]
y = [3, 4]
res = [[0, 0], [0, 0]]
for i in range(2): # Noncompliant {{Avoid iterative matrix operations, use numpy dot or outer function instead}}
    for j in range(2):
        res[i][j] = x[i] * y[j]


# Test 15: Matrix dot using zip (compliant)
res = sum(i * j for i, j in zip(a, b))  # Compliant
