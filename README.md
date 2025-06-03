creedengo-python
===========

_creedengo_ is a collective project aiming to reduce environmental footprint of software at the code level. The goal of
the project is to provide a list of static code analyzers to highlight code structures that may have a negative
ecological impact: energy and resources over-consumption, "fatware", shortening terminals' lifespan, etc.

_creedengo_ is based on evolving catalogs
of [good practices](https://github.com/green-code-initiative/creedengo-rules-specifications/tree/main/docs/rules), for various technologies.
This
SonarQube plugin then implements these catalogs as rules for scanning your Python projects.

> ⚠️ This is still a very early stage project. Any feedback or contribution will be highly appreciated. Please
> refer to the contribution section.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/CODE_OF_CONDUCT.md)

🌿 SonarQube Plugins
-------------------

This plugin is part of the creedengo project.\
You can find a list of all our other plugins in
the [creedengo repository](https://github.com/green-code-initiative/creedengo#-sonarqube-plugins)

🚀 Getting Started
------------------

You can give a try with a one command docker :

```sh
docker run -ti --rm \
       -p 9000:9000 \
       --name sonarqube-creedengo-python ghcr.io/green-code-initiative/sonarqube-creedengo-python:latest
```

or (with logs and data locally stored) :

```sh
docker run -ti --rm \
       -v sq_creedengo_logs:/opt/sonarqube/logs \
       -v sq_creedengo_data:/opt/sonarqube/data \
       -p 9000:9000 \
       --name sonarqube-creedengo-python ghcr.io/green-code-initiative/sonarqube-creedengo-python:latest
```

... and configure local SonarQube (security config and quality profile : see [configuration](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/HOWTO.md#howto-install-sonarqube-dev-environment) for more details).

To install other `creedengo` plugins, you can also :

- download each plugin separatly and copy the plugin (jar file) to `$SONAR_INSTALL_DIR/extensions/plugins` and restart SonarQube.
- install different creedengo plugins with Marketplace (inside admin panel of SonarQube)

Then you can use Python test project repository to test the environment : see README.md of [Python test project](https://github.com/green-code-initiative/creedengo-python-test-project)

🛒 Distribution
------------------

Ready to use binaries are available [from GitHub](https://github.com/green-code-initiative/creedengo-python/releases).

🧩 Compatibility
------------------

| Plugin version | SonarQube version   | Java version |
|----------------|---------------------|--------------|
| 1.4.+          | 9.4.+ LTS to 10.4.1 | 11 / 17      |
| 1.5.+          | 9.9.+ LTS to 10.7.0 | 11 / 17      |
| 2.0.+          | 9.9.+ LTS to 25.5.0 | 17           |

> Compatibility table of versions lower than 1.4.+ are available from the
> main [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications#-plugins-version-compatibility).

🤝 Contribution
---------------

check [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications#-contribution)

🤓 Main contributors
--------------------

check [creedengo repository](https://github.com/green-code-initiative/creedengo-rules-specifications#-main-contributors)

Links
-----

- https://docs.sonarqube.org/latest/analysis/overview/
