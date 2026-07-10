# NodehistJ — Руководство для контрибьютеров

NodehistJ — система управления историческими данными FidoNet nodelist. Мульти-модульный Gradle-проект на Java 25 +
Spring Boot 4.1.0.

## Структура проекта

```
nodehistj/                          # Корень проекта (Groovy-билд)
├── nodehistj-historic-nodelists/   # Сервис исторических nodelists (/historic)
├── nodehistj-history-diff/         # Сервис diff и истории узлов (/diff)
├── nodehistj-download-nodelists/   # Сервис загрузки nodelists
├── lib/s3/                         # Общая библиотека для S3-операций
├── config/checkstyle/              # Общие checkstyle-конфиги
└── build.gradle                    # Билд корня и подпроектов
```

Каждый модуль использует стандартную Maven-структуру: `src/main/java`, `src/main/resources`, `src/test/java`.

Пакет: `ru.oldzoomer.<module_with_underscores>`.

## Сборка и запуск

| Команда                       | Описание                    |
|-------------------------------|-----------------------------|
| `./gradlew build`             | Сборка всех модулей + тесты |
| `./gradlew test`              | Запуск unit-тестов          |
| `./gradlew checkstyleMain`    | Проверка стиля кода         |
| `./gradlew :<module>:bootRun` | Запуск сервиса локально     |

### Зависимости среды

Локальная разработка требует запущенных бэкенд-сервисов:

```bash
docker compose -f compose-dev.yml up -d   # MinIO, PostgreSQL, Redis, Redpanda
./gradlew :nodehistj-history-diff:bootRun  # Запуск сервиса
```

## Стиль кода

- **4 пробела** для отступов, **120 символов** — максимальная длина строки.
- **Именование:** `PascalCase` для типов, `camelCase` для методов/параметров/полей.
- **Checkstyle** — обязательная проверка (конфиг: `config/checkstyle/checkstyle.xml`).
- Lombok (`@RequiredArgsConstructor`, `@Builder`, `@Getter` и т.д.) — для boilerplate.
- MapStruct — для маппинга DTO ↔ Entity.
- Аннотации Spring Data JDBC (`@Table`, `@Column`, `@Id`) — для маппинга сущностей.

## Тесты

- JUnit 5 + Spring Boot Test (`@SpringBootTest`).
- Запуск: `./gradlew test` или `./gradlew :<module>:test`.
- Именованный паттерн: `<MethodName>_<Scenario>_<ExpectedResult>`.

## Коммиты

Используйте префиксы:

| Префикс       | Назначение                              |
|---------------|-----------------------------------------|
| `Fix`         | Исправление багов                       |
| `Refactoring` | Изменение структуры без смены поведения |
| `Test`        | Изменения в тестах                      |
| `Bump`        | Обновление зависимостей                 |
| `Merge`       | Слияние PR                              |

Формат: `Prefix: краткое описание` (англ.).

## PR

- Связывайте PR с issue (если есть).
- Опишите контекст изменений и ожидаемое поведение.
- Dependabot-PRы обычно мерджатся без дополнительного ревью.

## Архитектура

Модули взаимодействуют через PostgreSQL, Redis (кэш) и Redpanda/Kafka (асинхронные события). Каждая служба имеет свой
контекст API (`/historic`, `/diff`, `/newest`).
