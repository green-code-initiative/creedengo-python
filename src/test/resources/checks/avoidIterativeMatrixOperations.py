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
