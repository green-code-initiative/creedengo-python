def process_data(data):
    x = (1, 2, 3, 4)
    n = len(x)  # Compliant {{Computed once outside the loop}}
    
    results = []
    for i in range(10_000):
        results.append(n * i)
    
    return results