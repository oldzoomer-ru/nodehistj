#
# Unified Dockerfile for all NodehistJ services
# Use --build-arg SERVICE_NAME to specify which service to build
#
ARG BUILD_HOME=/build

#
# Gradle image for the build stage.
#
FROM gradle:jdk21-alpine as build-image

#
# Set the working directory.
#
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy the root Gradle files and all source code.
#
COPY --chown=gradle:gradle settings.gradle build.gradle $APP_HOME/
COPY --chown=gradle:gradle gradle $APP_HOME/gradle/
COPY --chown=gradle:gradle gradlew $APP_HOME/

# Copy all service directories and common libs
COPY --chown=gradle:gradle nodehistj-download-nodelists/ $APP_HOME/nodehistj-download-nodelists/
COPY --chown=gradle:gradle nodehistj-historic-nodelists/ $APP_HOME/nodehistj-historic-nodelists/
COPY --chown=gradle:gradle nodehistj-newest-nodelists/ $APP_HOME/nodehistj-newest-nodelists/
COPY --chown=gradle:gradle nodehistj-history-diff/ $APP_HOME/nodehistj-history-diff/
COPY --chown=gradle:gradle lib/ $APP_HOME/lib/
COPY --chown=gradle:gradle config/ $APP_HOME/config/

#
# Build argument to specify which service to build
#
ARG SERVICE_NAME
ARG SKIP_TESTS=true

#
# Build the specified service
#
RUN --mount=type=secret,id=github_username \
    --mount=type=secret,id=github_token \
    if [ -f /run/secrets/github_username ] && [ -f /run/secrets/github_token ]; then \
        export USERNAME=$(cat /run/secrets/github_username); \
        export TOKEN=$(cat /run/secrets/github_token); \
    fi; \
    if [ "$SKIP_TESTS" = "true" ]; then \
        ./gradlew :${SERVICE_NAME}:build --no-daemon -x test; \
    else \
        ./gradlew :${SERVICE_NAME}:build --no-daemon; \
    fi

#
# Java image for the application to run in.
#
FROM eclipse-temurin:21-jre-alpine

#
# Install curl for health checks
#
RUN apk add --no-cache curl

#
# Build arguments
#
ARG BUILD_HOME
ARG SERVICE_NAME
ENV APP_HOME=$BUILD_HOME

#
# Copy the jar file and name it app.jar
#
COPY --from=build-image $APP_HOME/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar app.jar

#
# The command to run when the container starts.
#
CMD ["java", "-jar", "app.jar"]