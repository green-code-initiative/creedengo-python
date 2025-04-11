from functools import lru_cache


@lru_cache
def cached_function():
    print('a')


@lru_cache()
def cached_function_a():
    print('a')


@lru_cache(maxsize=30)
def cached_function_b():
    print('b')
