@dataclass
class MyClass:
    a: int
    b: int
    c: int

@dataclass()
class MyClass:
    a: int
    b: int
    c: int

@dataclass(frozen=True)
class MyClass:
    a: int
    b: int
    c: int