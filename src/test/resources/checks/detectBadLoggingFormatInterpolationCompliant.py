import logging
logger = logging.getLogger()
name = "world"

logging.info("Hello %s", name)  # Correct
logger.debug("Hello %s", name)  # Correct