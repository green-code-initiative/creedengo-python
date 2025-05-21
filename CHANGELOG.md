# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

- compatibility updates for SonarQube 25.5.0
- upgrade libraries versions
- correction of technical problem with Integration tests (because of Maven format in technical answer to "sonar-orchestrator-junit5" library)
- update from jdk 11 to 17
- Add Lombok annotation processing inside `maven-compiler` plugin, to fix compile error on Arm64 architecture

### Deleted

## [2.0.1] - 2025-03-14

### Changed

- [#42](https://github.com/green-code-initiative/creedengo-python/issues/42) Correction of dead link
- update some libraries versions / TU corrections / README.md correction (compatibility)
- update sonarqube version to 25.3.0
- upgrade actions/upload-artifact and actions/download-artifact from v3 to v4

## [2.0.0] - 2024-12-12

### Changed

- [#43](https://github.com/green-code-initiative/creedengo-python/pull/43) Strong renaming plugin from `ecocode-python` to `creedengo-python` (and maven groupid from `io.ecocode` to `org.green-code-initiative`)

## [1.5.0] - 2024-10-04

### Changed

- refactoring docker system
- [#29](https://github.com/green-code-initiative/creedengo-python/issues/29) Add test to ensure all Rules are registered
- [#24](https://github.com/green-code-initiative/creedengo-python/issues/24) Set correct required language because the
  plugin wasn't loaded anymore - retro-compatibility modifications (9.9.0 to 10.7 and not compatible before 9.9.0) AND
  add support for > 10.5 Sonarqube version (up to 10.7.0)
- update some maven plugin versions and library versions to be up-to-date
- correction of SonarCloud issues

### Deleted

- deletion of EC69 rule because of already deprecated (see RULES.md file)

## [1.4.4] - 2024-07-18

### Added

- [#26](https://github.com/green-code-initiative/creedengo-python/issues/26) [EC89] Avoid unlimited cache

### Changed

- [#22](https://github.com/green-code-initiative/creedengo-python/issues/22) Depreciation of EC69 rule for python because
  not relevant (after analysis)

### Deleted

- [#22](https://github.com/green-code-initiative/creedengo-python/issues/22) Delete deprecated EC66 rule for Python

## [1.4.3] - 2024-05-15

### Added

- [#18](https://github.com/green-code-initiative/creedengo-python/issues/18) Add support for SonarQube 10.4 "
  DownloadOnlyWhenRequired" feature
- Add Support for SonarQube 10.4.1

### Changed

- [#17](https://github.com/green-code-initiative/creedengo-python/issues/17) EC7 - correction setter problem on
  constructor method
- check Sonarqube 10.4.1 compatibility + update docker files and README.md / NOT OK with 10.5.x (issue created)

## [1.4.2] - 2024-01-11

### Changed

- [#14](https://github.com/green-code-initiative/creedengo-python/issues/14) Correction of error with deprecated EC34 rule
- Update ecocode-rules-specifications to 1.4.7

## [1.4.1] - 2024-01-05

### Added

- Add 10.3 SonarQube compatibility

### Changed

- [#5](https://github.com/green-code-initiative/creedengo-python/pull/5) Upgrade licence system and licence headers of
  Java files
- [#6](https://github.com/green-code-initiative/creedengo-python/pull/6) Adding EC35 rule : EC35 rule replaces EC34 with a
  specific use case ("file not found" specific)
- [#7](https://github.com/green-code-initiative/creedengo-python/issues/7) Add build number to manifest
- [#123](https://github.com/green-code-initiative/creedengo/issues/123) Improve unit tests for EC7 rule
- Update ecocode-rules-specifications to 1.4.6
- README.md upgrade : docker test environment
- [#10](https://github.com/green-code-initiative/creedengo-python/issues/10) Correction of NullPointException in EC2 rule

### Deleted

- [#4](https://github.com/green-code-initiative/creedengo-python/issues/4) Deprecate rule EC66 for Python because not
  applicable (see details inside issue)

## [1.4.0] - 2023-08-08

### Added

- Python rules moved from `ecocode` repository to current repository
- [#142](https://github.com/green-code-initiative/creedengo/issues/142) new Python rule : Multiple if-else statement +
  refactoring implementation
- [#205](https://github.com/green-code-initiative/creedengo/issues/205) compatibility with SonarQube 10.1

## Comparison list

[unreleased](https://github.com/green-code-initiative/creedengo-python/compare/2.0.1...HEAD)
[2.0.1](https://github.com/green-code-initiative/creedengo-python/compare/2.0.0...2.0.1)
[2.0.0](https://github.com/green-code-initiative/creedengo-python/compare/1.4.4...2.0.0)
[1.5.0](https://github.com/green-code-initiative/creedengo-python/compare/1.4.4...1.5.0)
[1.4.4](https://github.com/green-code-initiative/creedengo-python/compare/1.4.3...1.4.4)
[1.4.3](https://github.com/green-code-initiative/creedengo-python/compare/1.4.2...1.4.3)
[1.4.2](https://github.com/green-code-initiative/creedengo-python/compare/1.4.1...1.4.2)
[1.4.1](https://github.com/green-code-initiative/creedengo-python/compare/1.4.0...1.4.1)
[1.4.0](https://github.com/green-code-initiative/creedengo-python/releases/tag/1.4.0)
