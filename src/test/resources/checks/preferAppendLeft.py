from collections import deque

# Cas non conformes
numbers = []
numbers.insert(0, val)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

items = []
items.insert(0, "start")  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

lst = []
(lst).insert(0, 'x')  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

x = []
(x).insert(0, value)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

def insert_first(l, v):
    l.insert(0, v)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

some_list = []
some_list.insert(index=0, object=val)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

deque_like = []
deque_like.insert(0, "bad")  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

[1, 2, 3].insert(0, 9)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

class MyList(list):
    pass

custom_list = MyList()
custom_list.insert(0, 'z')  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

def wrapper():
    lst = []
    lst.insert(0, "wrapped")  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}


dq = deque()
dq.appendleft("start")

mylist = []
mylist.insert(0.0, val) # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}


data = []
data.insert(1, "something")

real_deque = deque()
real_deque.appendleft("good")


other_list = []
position = 1
other_list.insert(position, "ok")



val = "new"
queue = deque()
queue.appendleft(val)

