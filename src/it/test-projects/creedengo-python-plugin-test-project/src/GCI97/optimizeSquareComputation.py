import math

x = 5
result1 = x**2  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}

z = 7
result4 = math.pow(z, 2)  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}

a = 3
result5 = a*a

b = 4
result6 = b*3 
result7 = 5*b 
result8 = math.pow(b, 3) 
result9 = b**3 

c = 2.5
result10 = c**2  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}
result11 = math.pow(c, 2)  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}


d = 8
e = 9
result12 = math.pow(d+e, 2)  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}
result13 = (d+e)**2  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}
result14 = (d+e)*(d+e) 


def square(x):
    return x**2  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}

def better_square(x):
    return x*x


import math as m
result15 = m.pow(d, 2)  # Noncompliant {{Use x*x instead of x**2 or math.pow(x,2) to calculate the square of a value}}

result16 = math.sqrt(d)
result17 = math.sin(d)
result18 = math.pow(d, 1.5)