from loguru import logger

name = "world"

# Loguru usage (non-compliant - using .format())
logger.trace("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.debug("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.success("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.warning("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.error("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.critical("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# Loguru usage (non-compliant - using f-strings)
logger.info(f"Hello {name}") # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.success(F'Status: {name}') # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# Alternative import style
from loguru import logger as log

log.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
log.error(f"Error: {name}") # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
