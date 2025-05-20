@dataclass # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

@dataclass() # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

@dataclass(frozen=True) # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c