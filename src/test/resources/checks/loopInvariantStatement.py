def process_data(data):
    x = (1, 2, 3, 4)

    results = []
    for i in range(10_000):
        n = len(x)  # Noncompliant {{Loop invariant statement detected. Consider moving this computation outside the loop to improve performance.}}
        results.append(n * i)

    return results