from loguru import logger

name = "world"

# Loguru usage (compliant - using %s format)
logger.trace("Hello %s", name)
logger.debug("Hello %s", name)
logger.info("Hello %s", name)
logger.success("Hello %s", name)
logger.warning("Hello %s", name)
logger.error("Hello %s", name)
logger.critical("Hello %s", name)

# Alternative import style
from loguru import logger as log

log.info("Hello %s", name)
log.success("Status: %s", name)
