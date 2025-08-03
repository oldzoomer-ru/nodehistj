#
# Unified Dockerfile for all NodehistJ services
#
# Features:
# - Multi-stage сборка (build + runtime)
# - Поддержка всех сервисов NodehistJ
# - Пропуск тестов при сборке (по умолчанию)
# - Поддержка GitHub credentials через Docker secrets
#
# Требования:
# - Docker 20.10+
#
# Использование:
# docker build --build-arg SERVICE_NAME=nodehistj-download-nodelists -t nodehistj-download .
#
# Переменные сборки:
# SERVICE_NAME - обязательный, имя сервиса для сборки (например nodehistj-download-nodelists)
# SKIP_TESTS - пропускать тесты (по умолчанию true)
#
# Secrets:
# github_username - логин GitHub для доступа к приватным репозиториям
# github_token - токен GitHub с правами чтения
#
ARG BUILD_HOME=/build

#
# Gradle image for the build stage.
#
FROM gradle:jdk21-alpine as build-image

#
# Set the working directory.
#
ARG SERVICE_NAME
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
COPY --chown=gradle:gradle lib/ $APP_HOME/lib/
COPY --chown=gradle:gradle config/ $APP_HOME/config/
COPY --chown=gradle:gradle ${SERVICE_NAME}/ $APP_HOME/${SERVICE_NAME}/

#
# Build the specified service
#
RUN --mount=type=secret,id=github_username \
    --mount=type=secret,id=github_token \
    if [ -f /run/secrets/github_username ] && [ -f /run/secrets/github_token ]; then \
        export USERNAME=$(cat /run/secrets/github_username); \
        export TOKEN=$(cat /run/secrets/github_token); \
    fi; \
    ./gradlew :${SERVICE_NAME}:build --no-daemon -x test;

#
# Java image for the application to run in.
#
FROM eclipse-temurin:21-jre-alpine

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