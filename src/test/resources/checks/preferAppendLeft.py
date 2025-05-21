from collections import deque

numbers = []
numbers.insert(0, val)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

items = []
items.insert(0, "start")  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

mylist = []
mylist.insert(0.0, val)  

a = 0
mylist.insert(a, val)  

dq = deque()
dq.appendleft("start") 

data = []
data.insert(1, "something")  

lst = []
(lst).insert(0, 'x')  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

x = []
(x).insert(0, value)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

def insert_first(l, v):
    l.insert(0, v)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

def add_to_front(dq, item):
    dq.appendleft(item) 

some_list = []
some_list.insert(index=0, object=val)   # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

deque_like = []
deque_like.insert(0, "bad")  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}

real_deque = deque()
real_deque.appendleft("good")  

fn = getattr(mylist, "insert")
fn(0, val) 

[1, 2, 3].insert(0, 9)  # Noncompliant {{Use appendleft with deque instead of .insert(0, val) for modification at the beginning of a list}}
