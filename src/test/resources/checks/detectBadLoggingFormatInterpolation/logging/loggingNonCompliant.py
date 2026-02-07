import logging
from logging import getLogger, Logger

name = "world"

# Direct usage of logging module
logging.debug("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.warning("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.warn("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.error("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.critical("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.fatal("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.exception("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

logging.debug(F'Hello {name}') # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logging.log(logging.INFO, "Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# With getLogger
logger = logging.getLogger(__name__)
logger.debug("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.warning("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.warn("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.error("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.critical("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.fatal("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
logger.exception("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

logger.info(f'Hello {name}') # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

logger.log(logging.INFO, "Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# With getLogger
log = getLogger(__name__)
log.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
log.error(f"Hello {name}") # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}

# With Logger
LOGGER = Logger(__name__)
LOGGER.info("Hello {}".format(name)) # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
LOGGER.critical(F"Hello {name}") # Noncompliant {{For logging format, prefer using %s with kwargs instead of builtin formatter "".format() or f""}}
