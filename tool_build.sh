#!/usr/bin/env sh

rm -rf ./lib/*.jar
# use the Maven wrapper (Maven 3.9.9) : Maven >= 3.10 (floating "maven:3-*" image tag) no longer provides
# MessageUtils, required by maven-git-versioning-extension
./mvnw clean package -DskipTests
