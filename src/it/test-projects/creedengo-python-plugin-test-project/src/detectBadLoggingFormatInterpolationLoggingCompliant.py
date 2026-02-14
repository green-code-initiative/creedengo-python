import logging
from logging import getLogger, Logger

name = "world"
level = logging.INFO

### Compliant cases ###

# Utilisation directe du module logging - Good format (using %s)
logging.debug("Hello %s", name)
logging.info("Hello %s", name)
logging.warning("Hello %s", name)
logging.warn("Hello %s", name)
logging.error("Hello %s", name)
logging.critical("Hello %s", name)
logging.fatal("Hello %s", name)
logging.exception("Hello %s", name)
logging.log(logging.INFO, "Hello %s", name)

# Via getLogger - Good format
logger = logging.getLogger(__name__)
logger.debug("Hello %s", name)
logger.info("Hello %s", name)
logger.warning("Hello %s", name)
logger.error("Hello %s", name)
logger.critical("Hello %s", name)
logger.exception("Hello %s", name)
logger.log(logging.INFO, "Hello %s", name)

# Via getLogger importé - Good format
log = getLogger(__name__)
log.info("Hello %s", name)

# Via Logger directement - Good format
LOGGER = Logger(__name__)
LOGGER.info("Hello %s", name)

# Messages sans interpolation - OK
logging.info("Simple message without variables")
logger.warning("Another simple message")

# Messages avec plusieurs arguments - Good format
logging.info("User %s logged in at %s", name, "2026-02-07")
logger.error("Error code %s: %s", 404, "Not found")
