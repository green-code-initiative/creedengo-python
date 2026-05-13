from loguru import logger

name = "world"
status = "success"

### Non-compliant cases ###

# Loguru with .format() - Bad format
logger.trace("Hello {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.debug("Hello {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.info("Hello {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.success("Status: {}".format(status))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.warning("Warning: {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.error("Error: {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.critical("Critical: {}".format(name))  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# Loguru with f-strings - Bad format
logger.info(f"Info: {name}")  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.success(F"Success: {status}")  # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
