@dataclass
class MyClass1:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

@dataclass()
class MyClass2:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

@dataclass(frozen=True)
class MyClass3:
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c