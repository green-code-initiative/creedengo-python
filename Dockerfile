# "openjdk" images are deprecated and frozen on an old JDK 17 (NPE "CgroupInfo.getMountPoint() ... anyController is null" on cgroup v2 hosts)
# => use a maintained Temurin JDK 17 (JDK 17 required by the project, see pom.xml)
ARG MAVEN_BUILDER=3-eclipse-temurin-17

#ARG SONARQUBE_VERSION=24.12.0.100206-community
#ARG SONARQUBE_VERSION=25.3.0.104237-community
#ARG SONARQUBE_VERSION=25.9.0.112764-community
#ARG SONARQUBE_VERSION=25.10.0.114319-community
ARG SONARQUBE_VERSION=25.11.0.114957-community

#ARG MAVEN_BUILDER=3-openjdk-11-slim
#ARG SONARQUBE_VERSION=9.9.8-community

FROM maven:${MAVEN_BUILDER} AS builder

COPY . /usr/src/creedengo

WORKDIR /usr/src/creedengo
COPY src src/
COPY pom.xml tool_build.sh ./

RUN ./tool_build.sh

FROM sonarqube:${SONARQUBE_VERSION}
COPY --from=builder /usr/src/creedengo/target/creedengo-*.jar /opt/sonarqube/extensions/plugins/
USER sonarqube
