from loguru import logger

name = "world"
status = "success"
error_code = 500

### Compliant cases ###

# Loguru with correct format (using %s)
logger.trace("Hello %s", name)
logger.debug("Hello %s", name)
logger.info("Hello %s", name)
logger.success("Status: %s", status)
logger.warning("Warning: %s", name)
logger.error("Error: %s", name)
logger.critical("Critical: %s", name)

# Messages sans interpolation - OK
logger.info("Simple message without variables")
logger.success("Task completed successfully")

# Messages avec plusieurs arguments - Good format
logger.info("User %s logged in with status %s", name, status)
logger.error("Error code %s occurred: %s", error_code, "Internal server error")
