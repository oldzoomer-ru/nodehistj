# NodehistJ — Contributor Guide

## Структура проекта

NodehistJ — мульти-модульный Gradle-проект на Spring Boot 4.1.1 (Java 25).

```
├── lib/s3/                          # Библиотека для работы с S3 (jar, не bootJar)
├── nodehistj-historic-nodelists/    # Сервис historic (/historic)
├── nodehistj-download-nodelists/    # Сервис download (/newest)
├── nodehistj-history-diff/          # Сервис diff (/diff)
├── config/checkstyle/               # Checkstyle-конфигурация
├── .github/workflows/               # GitHub Actions CI/CD
├── Dockerfile                       # Единый Dockerfile для всех сервисов (GraalVM Native)
├── compose-dev.yml                  # Dev-окружение (MinIO + сервисы)
└── compose-traefik.yml              # Продакшн (Traefik routing)
```

Каждый сервис следует стандартной Spring Boot структуре:

- `controller/` — REST-контроллеры
- `service/` и `service/impl/` — бизнес-логика (interface + implementation)
- `repo/` — Spring Data JPA репозитории
- `entity/` — JPA-сущности (Lombok `@Builder`, `@RequiredArgsConstructor`)
- `dto/` — DTO для API
- `mapper/` — MapStruct-мапперы
- `kafka/` — Kafka-консьюмеры/продюсеры
- `config/` — конфигурации Spring
- `mcp/` — MCP-сервисы
- `util/` — утилиты

Тесты находятся в `src/test/java` рядом с основным кодом. Интеграционные тесты наследуются от `BaseIntegrationTest` (
Testcontainers: PostgreSQL, Redpanda, Redis, MinIO).

## Команды сборки и запуска

| Команда                           | Описание                                                  |
|-----------------------------------|-----------------------------------------------------------|
| `./gradlew build`                 | Собрать все модули, запустить тесты, проверить checkstyle |
| `./gradlew test`                  | Запустить только unit-тесты                               |
| `./gradlew check`                 | Проверка кода (checkstyle + тесты)                        |
| `./gradlew :module:bootRun`       | Запустить сервис локально                                 |
| `./gradlew :module:nativeCompile` | Собрать GraalVM Native Image                              |
| `./gradlew jacocoTestReport`      | Сгенерировать отчёт по покрытию                           |
| `./gradlew dependencies`          | Показать дерево зависимостей                              |

**PGO-оптимизация:**

```bash
# Шаг 1: инструментированная сборка
./gradlew :module:nativeCompile -Pbootstrap

# Шаг 2: сбор профиля (запуск контейнера с нагрузкой)

# Шаг 3: финальная сборка с профилем
./gradlew :module:nativeCompile -Ppgo=/path/to/default.iprof
```

**Быстрая сборка:** `./gradlew :module:nativeCompile -PquickBuild`

## Стиль кода и правила

- **Отступы:** 4 пробела (Checkstyle `Indentation`)
- **Макс. длина строки:** 120 символов
- **Макс. длина файла:** 1000 строк
- **Именование методов/параметров:** camelCase, начинается со строчной (`^[a-z][a-zA-Z0-9]*$`)
- **Именование классов:** PascalCase
- **Checkstyle:** запускается автоматически при `build`/`check`
- **Jacoco:** отчёт генерируется автоматически после тестов

### Паттерны кода

- Сущности: Lombok `@Builder` + `@RequiredArgsConstructor` + `@Entity`
- Сервисы: `@Service`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true)` на классе
- Мапперы: MapStruct (`@Mapper`)
- DTO:record или классы с Lombok
- Логирование: `@Log4j2` (Lombok)

### Тестирование

- **Фреймворк:** JUnit 5 + JUnit Platform
- **Интеграционные тесты:** `@Testcontainers` с `BaseIntegrationTest`
- **Название тестовых классов:** `*Test` (unit), `*IntegrationTest` (integration)
- **Запуск конкретного теста:** `./gradlew :module:test --tests "ClassName"`
- **Покрытие:** JaCoCo (автоматически после `test`)

## Ветви и коммиты

- **Основная ветвь:** `master`
- **Conventional Commits:** `type(scope): description`
  - `feat:` — новая функциональность
  - `fix:` — исправление багов
  - `refactor:` — рефакторинг (без изменения поведения)
  - `test:` — изменения в тестах
  - `docs:` — документация
  - `chore:` — рутинные изменения (deps, config)
  - `build:` — изменения системы сборки
  - `ci:` — изменения CI/CD
- **PR от Dependabot:** `Bump <dependency> from X to Y`
- **Слияние PR:** `Merge pull request #NNN from ...`

## Docker и развёртывание

### Локальная разработка
```bash
# Запуск зависимостей (MinIO + сервисы)
docker compose -f compose-dev.yml up -d

# Запуск конкретного сервиса
./gradlew :nodehistj-historic-nodelists:bootRun
```

### Сборка образов

```bash
# Базовая сборка
docker compose -f compose.yml up -d

# С PGO-оптимизацией (3 шага)
# 1. docker build --build-arg SERVICE_NAME=... --build-arg PGO_MODE=instrument -t image-instrumented .
# 2. Запустить контейнер для сбора профиля
# 3. docker build --build-arg SERVICE_NAME=... --build-arg PGO_MODE=optimized --secret id=default_iprof,src=default.iprof -t image-optimized .
```

### Переменные окружения

| Переменная               | Описание            | По умолчанию    |
|--------------------------|---------------------|-----------------|
| `S3_USER`                | MinIO user          | —               |
| `S3_PASSWORD`            | MinIO password      | —               |
| `POSTGRES_PASSWORD`      | PostgreSQL password | —               |
| `KAFKA_BOOTSTRAP_SERVER` | Kafka адрес         | `redpanda:9092` |
| `REDIS_HOST`             | Redis адрес         | `redis`         |
| `DOMAIN`                 | Домен для Traefik   | —               |

## Зависимости

- **Spring Boot** 4.1.1
- **GraalVM Native Image** plugin 1.1.10
- **PostgreSQL** — основная БД
- **Redis** — кэширование
- **Redpanda** (Kafka-compatible) — messaging
- **MinIO** (S3-compatible) — хранение архивов
- **MapStruct** 1.6.3 — маппинг DTO
- **Lombok** — генерация boilerplate-кода
- **SpringDoc OpenAPI** — Swagger UI

## DevContainer

Проект поддерживает VS Code DevContainers (`.devcontainer/devcontainer.json`):

- Образ: `mcr.microsoft.com/devcontainers/base:trixie`
- Features: Docker-in-Docker, GraalVM (asdf)
- Для запуска: откройте проект в VS Code → "Reopen in Container"

## Полезные команды

```bash
# Остановить все сервисы
docker compose -f compose.yml down

# Просмотр логов
docker compose -f compose.yml logs -f

# Пересобрать образы
docker compose -f compose.yml build

# Проверка checkstyle отдельно
./gradlew checkstyleMain

# Запуск одного интеграционного теста
./gradlew :nodehistj-historic-nodelists:test --tests "HistoricNodelistControllerIntegrationTest"
```
