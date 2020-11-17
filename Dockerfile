ARG JAVA_VERSION=8
ARG MAVEN_VERSION=3.6.3

FROM maven:${MAVEN_VERSION}-jdk-${JAVA_VERSION}-slim as builder

# maven config
ARG USER_HOME_DIR="/home/maven"
ENV MAVEN_CONFIG="$USER_HOME_DIR/.m2"

# create a non-root user
RUN adduser maven --home /home/maven

# switch to non-root user
USER maven

# copy the local package files into the container's workspace.
COPY --chown=maven:maven . /home/maven/src

# switch into workspace
WORKDIR /home/maven/src

# build application 
RUN mvn package

FROM openjdk:${JAVA_VERSION}-jdk-alpine

# application config
ENV WORKSPACE_DIR="data"
ENV CRAWL_INTERVAL_MS="12000000"

# switch into workspace
WORKDIR /app

# install cURL & maven dependencies
RUN apk --no-cache add curl maven

# copy jar-application from build
COPY --from=builder /home/maven/src/target/jcef-maven-0.0.1-SNAPSHOT-jar-with-dependencies.jar /app/jcef-maven.jar

# copy the config file into the container's workspace.
COPY --from=builder /home/maven/src/config.template.json /app/config.template.json

# copy entrypoint shell script into image
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint

# make entrypoint shell script executable
RUN chmod +x /usr/local/bin/docker-entrypoint

ENTRYPOINT ["docker-entrypoint"]

CMD ["java", "-jar", "jcef-maven.jar"]