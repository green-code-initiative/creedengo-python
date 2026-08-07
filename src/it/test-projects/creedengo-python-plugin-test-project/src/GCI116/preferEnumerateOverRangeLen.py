my_list = [1, 2, 3, 4, 5]

for i in range(len(my_list)):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
    print(i, my_list[i])

names = ["Alice", "Bob", "Charlie"]
for i in range(len(names)):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
    print(names[i])

data = {"a": 1, "b": 2}
keys = list(data.keys())
for i in range(len(keys)):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
    print(keys[i])

def process(items):
    for i in range(len(items)):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
        items[i] = items[i] * 2

matrix = [[1, 2], [3, 4]]
for i in range(len(matrix)):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
    for j in range(len(matrix[i])):  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}
        print(matrix[i][j])

for i, val in enumerate(my_list):
    print(i, val)

for i in range(10):
    print(i)

n = 10
for i in range(n):
    print(i)

for i in range(0, len(my_list)):
    print(i)

for i in range(0, len(my_list), 2):
    print(i)

for item in my_list:
    print(item)

length = len(my_list)
for i in range(length):
    print(i)

result = [my_list[i] for i in range(len(my_list))]  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}

squared = {i: my_list[i]**2 for i in range(len(my_list))}  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}

gen = sum(my_list[i] for i in range(len(my_list)))  # Noncompliant {{Avoid range(len()) pattern, prefer direct iteration or enumerate() which avoids costly index-based access}}

compliant_comp = [val for val in my_list]

compliant_enum = [val for i, val in enumerate(my_list)]

compliant_range = [i for i in range(10)]

