#
# Unified Dockerfile for all NodehistJ services (optimized)
#
# Features:
# - Multi-stage сборка (build + runtime)
# - Поддержка всех сервисов NodehistJ
# - Пропуск тестов при сборке (по умолчанию)
# - Поддержка GitHub credentials через Docker secrets
# - Кэширование зависимостей Gradle
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
FROM eclipse-temurin:21-jdk-alpine as build-image

#
# Set the working directory.
#
ARG SERVICE_NAME
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy only build files first to cache dependencies
#
COPY gradle $APP_HOME/gradle/
COPY gradlew $APP_HOME/
RUN ./gradlew --no-daemon --version

COPY settings.gradle build.gradle $APP_HOME/
COPY config/ $APP_HOME/config/
COPY lib/ $APP_HOME/lib/
COPY ${SERVICE_NAME}/ $APP_HOME/${SERVICE_NAME}/

#
# Build the specified service
#
RUN --mount=type=secret,id=github_username \
    --mount=type=secret,id=github_token \
    if [ -f /run/secrets/github_username ] && [ -f /run/secrets/github_token ]; then \
        export GITHUB_USERNAME=$(cat /run/secrets/github_username); \
        export GITHUB_TOKEN=$(cat /run/secrets/github_token); \
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
