my_list = [1, 2, 3, 4, 5]

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

compliant_comp = [val for val in my_list]

compliant_enum = [val for i, val in enumerate(my_list)]

compliant_range = [i for i in range(10)]
