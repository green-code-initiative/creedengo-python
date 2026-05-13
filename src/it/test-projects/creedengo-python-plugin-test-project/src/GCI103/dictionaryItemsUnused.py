
for a, b in my_dict.items():
    print(a, b)

for key, value in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    result.append(key)

for key, value in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    result.append(value)

for key in my_dict.keys():
    result.append(key)

for value in my_dict.values():
    result.append(value)

for item in my_dict.items():
    result.append(item)  

entries = []
for k, v in my_dict.items():
    entries.append((k, v))  

for key, value in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    do_something_with(key)

for k, v in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    do_something_with(v)

for key, value in my_dict.items():
    print(f"{key}: {value}")

for k, v in my_dict.items():
    some_list.append((k, v))

for k, v in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    used_keys.append(k)

if True:
    for k, v in my_dict.items():
        print(k)
        print(v)

copied_dict = dict(my_dict.items())

for i, (k, v) in enumerate(my_dict.items()):
    print(i, k, v)

{(k, v) for k, v in my_dict.items()}
