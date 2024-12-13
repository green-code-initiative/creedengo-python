ARG MAVEN_BUILDER=3-openjdk-17-slim
ARG SONARQUBE_VERSION=10.7.0-community

FROM maven:${MAVEN_BUILDER} AS builder

COPY . /usr/src/creedengo

WORKDIR /usr/src/creedengo
COPY src src/
COPY pom.xml tool_build.sh ./

RUN ./tool_build.sh

FROM sonarqube:${SONARQUBE_VERSION}
COPY --from=builder /usr/src/creedengo/target/creedengo-*.jar /opt/sonarqube/extensions/plugins/
USER sonarqube
