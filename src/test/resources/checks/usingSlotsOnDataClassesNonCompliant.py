@dataclass # Noncompliant {{From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)}}
class MyClass:
    a: int
    b: int
    c: int

@dataclass() # Noncompliant {{From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)}}
class MyClass2:
    a: int
    b: int
    c: int

@dataclass(frozen=True) # Noncompliant {{From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)}}
class MyClass3:
    a: int
    b: int
    c: int

@dataclass(slots=False) # Noncompliant {{From python >= 3.10, reduce memory footprint by using @dataclass(slots=True)}}
class MyClass4:
    a: int
    b: int
    c: int