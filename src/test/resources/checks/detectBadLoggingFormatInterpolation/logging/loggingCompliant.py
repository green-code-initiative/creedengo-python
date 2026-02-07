import logging
from logging import getLogger, Logger

name = "world"

# Direct usage of logging module
logging.debug("Hello %s", name)
logging.info("Hello %s", name)
logging.warning("Hello %s", name)
logging.warn("Hello %s", name)  # Deprecated but still valid
logging.error("Hello %s", name)
logging.critical("Hello %s", name)
logging.fatal("Hello %s", name)  # Alias of critical
logging.exception("Hello %s", name)  # Inside an except bloc
logging.log(logging.INFO, "Hello %s", name)

# With getLogger
logger = logging.getLogger(__name__)
logger.debug("Hello %s", name)
logger.info("Hello %s", name)
logger.warning("Hello %s", name)
logger.warn("Hello %s", name)
logger.error("Hello %s", name)
logger.critical("Hello %s", name)
logger.fatal("Hello %s", name)
logger.exception("Hello %s", name)
logger.log(logging.INFO, "Hello %s", name)

# With getLogger
log = getLogger(__name__)
log.info("Hello %s", name)

# With Logger
LOGGER = Logger(__name__)
LOGGER.info("Hello %s", name)
