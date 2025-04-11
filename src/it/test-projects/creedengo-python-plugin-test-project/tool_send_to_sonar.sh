#!/usr/bin/env sh

# "sonar.token" variable : private TOKEN generated in your local SonarQube during installation
# (input paramater of this script)
# "sonar.login" variable : for a SONARQUBE < 10.0
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.host.url=http://localhost:$1 -Dsonar.token=$2 -Dsonar.login=$2
