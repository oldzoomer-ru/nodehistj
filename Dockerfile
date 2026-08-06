#
# Unified Dockerfile for all NodehistJ services (GraalVM Native Image + Distroless)
#
# Features:
# - Multi-stage сборка: GraalVM native compilation + Distroless cc runtime
# - Поддержка всех сервисов NodehistJ
# - Пропуск тестов при сборке (по умолчанию)
# - Поддержка GitHub credentials через Docker secrets
# - Кэширование зависимостей Gradle
# - PGO-оптимизации (опционально, через сборочные аргументы)
#
# Требования:
# - Docker 20.10+
# - Доступно ≥ 8 GB RAM для native-сборки
#
# Использование:
#   docker build --build-arg SERVICE_NAME=nodehistj-download-nodelists -t nodehistj-download .
#
# С PGO (двухпроходная сборка):
#   # Шаг 1 — инструментированная сборка
#   docker build --build-arg SERVICE_NAME=... --build-arg PGO_MODE=instrument -t nodehistj-instrumented .
#   # Шаг 2 — сбор профиля запуском контейнера с тестовой нагрузкой
#   # Шаг 3 — финальная сборка с профилем
#   docker build --build-arg SERVICE_NAME=... --build-arg PGO_MODE=optimized \
#     --secret id=default_iprof,src=default.iprof -t nodehistj-optimized .
#
# Переменные сборки:
#   SERVICE_NAME  — обязательный, имя сервиса (например nodehistj-download-nodelists)
#   SKIP_TESTS    — пропускать тесты (по умолчанию true)
#   PGO_MODE      — режим PGO: 'instrument' | 'optimized' (по умолчанию пусто — без PGO)
#
# Secrets:
#   github_username — логин GitHub для доступа к приватным репозиториям
#   github_token    — токен GitHub с правами чтения
#   default_iprof   — PGO-профиль (только при PGO_MODE=optimized)
#
ARG BUILD_HOME=/build

#
# Stage 1: GraalVM Native Image compilation
#
FROM ghcr.io/graalvm/native-image-community:25 AS build-image

ARG SERVICE_NAME
ARG BUILD_HOME
ARG PGO_MODE
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy only build files first to cache dependencies
#
COPY gradle $APP_HOME/gradle/
COPY gradlew $APP_HOME/
RUN ./gradlew --no-daemon --version

COPY settings.gradle build.gradle $APP_HOME/
COPY lib/s3/build.gradle $APP_HOME/lib/s3/
COPY nodehistj-download-nodelists/build.gradle $APP_HOME/nodehistj-download-nodelists/
COPY nodehistj-historic-nodelists/build.gradle $APP_HOME/nodehistj-historic-nodelists/
COPY nodehistj-history-diff/build.gradle $APP_HOME/nodehistj-history-diff/

RUN ./gradlew :dependencies --no-daemon

#
# Build the native image for the specified service
#
COPY config/ $APP_HOME/config/
COPY lib/s3/src/main/ $APP_HOME/lib/s3/src/main/
COPY nodehistj-download-nodelists/src/main/ $APP_HOME/nodehistj-download-nodelists/src/main/
COPY nodehistj-historic-nodelists/src/main/ $APP_HOME/nodehistj-historic-nodelists/src/main/
COPY nodehistj-history-diff/src/main/ $APP_HOME/nodehistj-history-diff/src/main/

RUN --mount=type=secret,id=default_iprof \
    export NATIVE_BUILD_ARGS=""; \
    if [ "${PGO_MODE}" = "instrument" ]; then \
        export NATIVE_BUILD_ARGS="-Pbootstrap"; \
    elif [ "${PGO_MODE}" = "optimized" ] && [ -f /run/secrets/default_iprof ]; then \
        mkdir -p /tmp/pgo; \
        cp /run/secrets/default_iprof /tmp/pgo/default.iprof; \
        export NATIVE_BUILD_ARGS="-Ppgo=/tmp/pgo/default.iprof"; \
    fi; \
    ./gradlew :${SERVICE_NAME}:nativeCompile --no-daemon -x check ${NATIVE_BUILD_ARGS}

#
# Stage 2: Distroless base runtime
#
FROM gcr.io/distroless/base-debian13:nonroot

ARG BUILD_HOME
ARG SERVICE_NAME
ENV APP_HOME=$BUILD_HOME

#
# Copy the native executable
#
COPY --from=build-image $APP_HOME/${SERVICE_NAME}/build/native/nativeCompile/${SERVICE_NAME} /app

#
# The command to run when the container starts.
# The native image is a standalone executable — no JVM needed.
#
ENTRYPOINT ["/app"]