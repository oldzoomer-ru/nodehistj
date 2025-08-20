# NodehistJ - Исторические списки узлов

Модуль для работы с историческими данными списков узлов через REST API (порт 8081)

## Основные возможности

- REST API для доступа к архивным данным (порт 8081)
- Поддержка фильтрации по годам, дням и зонам
- Кэширование в Redis (TTL 1 час)
- Интеграция с MinIO (хранилище) и Kafka (события)

## API Endpoints

Базовый путь: `/historic`

### Получение данных

`GET /historic/historicNodelist`

Параметры:

- `year` (required) - год данных (4 цифры)
- `dayOfYear` (required) - день года (1-366)
- `zone` (optional) - номер зоны
- `network` (optional) - номер сети (требует zone)
- `node` (optional) - номер узла (требует zone и network)

Пример запроса:

```bash
curl "http://localhost:8081/historic/historicNodelist?year=2023&dayOfYear=150"
```

Формат ответа:

```json
[
  {
    "nodelistYear": 2023,
    "nodelistName": "nodelist.123",
    "nodes": [...]
  }
]
```

## Настройка кэширования

Данные кэшируются в Redis с TTL 1 час. Ключи кэша:

- Все данные: `historicAllDataOfNodelist`
- По зоне: `historicGetZoneNodelistEntry:год-день-зона`
- По сети: `historicGetNetworkNodelistEntry:год-день-зона-сеть`
- По узлу: `historicGetNodeNodelistEntry:год-день-зона-сеть-узел`

## Переменные окружения

| Переменная | Описание | Обязательно | По умолчанию |
|------------|----------|-------------|--------------|
| `CASSANDRA_HOST` | Хост Cassandra/ScyllaDB | Да | localhost |
| `CASSANDRA_DC` | Имя датацентра | Да | datacenter1 |
| `REDIS_HOST` | Хост Redis | Нет | localhost |
| `MINIO_URL` | URL MinIO | Да | - |
| `MINIO_USER` | Пользователь MinIO | Да | - |
| `MINIO_PASSWORD` | Пароль MinIO | Да | - |

## Запуск

```bash
docker compose -f compose.yml up nodehistj-historic-nodelists
