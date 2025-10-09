# using KeyError for control flow
try:
    value = my_dict[key]
except KeyError:  # Noncompliant {{Avoid using exceptions for control flow}}
    value = default

# using IndexError for control flow
try:
    item = my_list[index]
except IndexError:  # Noncompliant {{Avoid using exceptions for control flow}}
    item = None

# using AttribteError for control flow
try:
    value = obj.attribute
except AttributeError:  # Noncompliant {{Avoid using exceptions for control flow}}
    value = None

#  using StopIteration for control flow
try:
    value = next(iterator)
except StopIteration:  # Noncompliant {{Avoid using exceptions for control flow}}
    value = None

# KeyError in loop
for key in keys:
    try:
        values.append(my_dict[key])
    except KeyError:  # Noncompliant {{Avoid using exceptions for control flow}}
        values.append(default)



### Compliant cases ###
value = my_dict.get(key, default)



if 0 <= index < len(my_list):
    item = my_list[index]
else:
    item = None



value = getattr(obj, 'attribute', None)


value = next(iterator, None)


try:
    result = risky_operation()
except ValueError:
    result = None



try:
    process_file()
except (IOError, OSError):
    handle_error()



for key in keys:
    values.append(my_dict.get(key, default))
