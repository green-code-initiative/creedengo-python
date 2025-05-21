
for a, b in my_dict.items():
    print(a, b)

for key, value in my_dict.items():  # Noncompliant {{Use dict.keys() or dict.values() instead of dict.items() when only one part of the key-value pair is used}}
    result.append(key)

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
