import logging
logger = logging.getLogger()
name = "world"

#logging.info("Hello {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.debug(f'Hello {name}')  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.error(f"Hello {name}")  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}