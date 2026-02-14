@dataclass # Noncompliant
class MyClass:
    a: int
    b: int
    c: int

@dataclass() # Noncompliant
class MyClass2:
    a: int
    b: int
    c: int

@dataclass(frozen=True) # Noncompliant
class MyClass3:
    a: int
    b: int
    c: int

@dataclass(slots=False) # Noncompliant
class MyClass4:
    a: int
    b: int
    c: int