def compliant_len_outside_for(data):
    x = (1, 2, 3, 4)
    n = len(x)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        results.append(n * i)
    return results

def compliant_list_outside_for():
    d = {"a": 1, "b": 2, "c": 3}
    keys = list(d.keys()) # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(1000):
        results.append(keys[i % len(keys)])
    return results

def compliant_len_outside_for_with_if(data):
    x = (1, 2, 3, 4)
    n = len(x)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        if i % 2 == 0 :
            results.append(n * i)
    return results

def compliant_sum_outside_double_for(data):
    x = (1, 2, 3, 4)
    n = sum(x)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        results.append(n * i)
    for j in range(10_000):
        results.append(n * j)
    return results

def compliant_max_inside_nested_for(data):
    x = (1, 2, 3, 4)
    n = max(x)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        for j in range(10_000):
            results.append(n + i + j)
    return results

def compliant_min_inside_while(data):
    x = (1, 2, 3, 4)
    n = min(x)  # Compliant {{Computed once outside the loop}}
    results = []
    i = 0
    while i < 10_000:
        results.append(n * i)
        i += 1
    return results

def compliant_two_abs_inside_for(data):
    x = 25
    y = -66
    n = abs(x)  # Compliant {{Computed once outside the loop}}
    m = abs(y)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        results.append(n + m + i)
    return results

def compliant_min_inside_nested_for_and_while(data):
    x = (1, 2, 3, 4)
    n = min(x)  # Compliant {{Computed once outside the loop}}
    results = []
    for i in range(10_000):
        j = 0
        while j < 10_000:
            results.append(n + i + j)
            j += 1
    return results

# # Exemple fonction définie auparavant qui ne modifie pas la variable
# def calcul_len(a):
#     return len(a)
#
# def compliant_len_method_outside_for(data):
#     x = (1, 2, 3, 4)
#     n = calcul_len(x)  # Compliant {{Computed once outside the loop}}
#     results = []
#     for i in range(10_000):
#         results.append(n * i)
#     return results