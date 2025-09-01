def non_compliant_len_inside_for(data):
    x = (1, 2, 3, 4)
    results = []
    for i in range(10_000):
        n = len(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(n * i)
    return results

def non_compliant_list_inside_for():
    d = {"a": 1, "b": 2, "c": 3}
    results = []
    for i in range(1000):
        keys = list(d.keys()) # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(keys[i % len(keys)])
    return results

def non_compliant_len_outside_for_with_if(data):
    x = (1, 2, 3, 4)
    results = []
    for i in range(10_000):
        if i % 2 == 0 :
            n = len(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
            results.append(n * i)
    return results

def non_compliant_sum_inside_double_for(data):
    x = (1, 2, 3, 4)
    results = []
    for i in range(10_000):
        n = sum(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(n * i)
    for j in range(10_000):
        n = sum(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(n * j)
    return results

def non_compliant_max_inside_nested_for(data):
    x = (1, 2, 3, 4)
    results = []
    for i in range(10_000):
        for j in range(10_000):
            n = max(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
            results.append(n + i + j)
    return results
    
def non_compliant_min_inside_while(data):
    x = (1, 2, 3, 4)
    results = []
    i = 0
    while i < 10_000:
        n = min(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(n * i)
        i += 1
    return results

def non_compliant_two_abs_inside_for(data):
    x = 25
    y = -66
    results = []
    for i in range(10_000):
        n = abs(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        m = abs(y)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
        results.append(n + m + i)
    return results

def non_compliant_min_inside_nested_for_and_while(data):
    x = (1, 2, 3, 4)
    results = []
    for i in range(10_000):
        j = 0
        while j < 10_000:
            n = min(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
            results.append(n + i + j)
            j += 1
    return results

# # Exemple fonction définie auparavant qui ne modifie pas la variable
# def calcul_len(a):
#     return len(a)
#
# def non_compliant_len_method_inside_for(data):
#     x = (1, 2, 3, 4)
#     results = []
#     for i in range(10_000):
#         n = calcul_len(x)  # Noncompliant {{Loop invariant detected. Move outside loop for better performance.}}
#         results.append(n * i)
#     return results