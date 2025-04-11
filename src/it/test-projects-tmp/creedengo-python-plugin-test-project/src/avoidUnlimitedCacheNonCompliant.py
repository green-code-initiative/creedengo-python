from functools import cache
from functools import lru_cache


class A:
    @cache  # Noncompliant {{Do not set cache size to unlimited}}
    def cached_method_a(self):
        print('a')

    @lru_cache(maxsize=None)  # Noncompliant {{Do not set cache size to unlimited}}
    def cached_method_b(self):
        print('b')


@cache  # Noncompliant {{Do not set cache size to unlimited}}
def cached_function():
    print('a')


@lru_cache(maxsize=None)  # Noncompliant {{Do not set cache size to unlimited}}
def cached_method():
    print('b')
