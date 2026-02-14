@dataclass
class MyClass:
    a: int
    b: int
    c: int

@dataclass()
class MyClass2:
    a: int
    b: int
    c: int

@dataclass(frozen=True)
class MyClass3:
    a: int
    b: int
    c: int