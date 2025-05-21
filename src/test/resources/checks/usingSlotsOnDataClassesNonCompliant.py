@dataclass # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    a: int
    b: int
    c: int

@dataclass() # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    a: int
    b: int
    c: int

@dataclass(frozen=True) # Noncompliant {{Reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    a: int
    b: int
    c: int