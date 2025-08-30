# NodehistJ - Система работы с историческими данными узлов

## Основные модули и API

1. **Загрузка данных** ([документация](nodehistj-download-nodelists/README.md))
   - Автоматическая загрузка файлов nodelist с FTP
   - Отправка уведомлений в Kafka топик `download_nodelists_is_finished_topic`

2. **Исторические данные** (порт 8081)
   - Базовый путь: `/historic`
   - Основные эндпоинты:
     - `GET /historic/historicNodelist` - получение данных
       - Параметры:
         - `year` (обязательный) - год данных
         - `dayOfYear` (обязательный) - день года (1-366)
         - `zone` - номер зоны (опционально)
         - `network` - номер сети (требует zone)
         - `node` - номер узла (требует zone и network)
       - Формат ответа: JSON массив объектов nodelist

3. **Текущие данные** (порт 8082)
   - Базовый путь: `/newest`
   - Основные эндпоинты:
     - `GET /newest/nodelist` - получение текущих списков
     - `GET /newest/nodelist/{id}` - получение конкретного списка
     - `POST /newest/nodelist` - обновление списков
   - Формат ответа: JSON

4. **Сравнение версий** (порт 8083)
   - Базовый путь: `/diff`
   - Основные эндпоинты:
     - `GET /diff/diff` - сравнение версий
       - Параметры:
         - `version1` - первая версия (формат: год-день)
         - `version2` - вторая версия (формат: год-день)
     - `GET /diff/history` - история изменений узла
   - Формат ответа: JSON с различиями

## Зависимости

- MinIO - хранилище файлов
- Redis - кэширование
- Redpanda (Kafka) - обмен сообщениями
- Cassandra - основное хранилище данных

## Быстрый старт

1. Склонируйте репозиторий:

```bash
git clone https://github.com/oldzoomer-ru/nodehistj.git
cd nodehistj
```

2. Создайте `.env` файл с минимальными настройками:

```ini
MINIO_USER=admin
MINIO_PASSWORD=password
```

3. Запустите сервисы (выберите один вариант):

**Базовый вариант:**

```bash
docker compose -f compose.yml up -d
```

**Для разработки (с MinIO):**

```bash
docker compose -f compose-dev.yml up -d
```

**С Traefik (для production):**

```bash
docker compose -f compose-traefik.yml up -d
```

## Основные переменные окружения

| Переменная | Описание | Обязательно | По умолчанию |
|------------|----------|-------------|--------------|
| `MINIO_USER` | Пользователь MinIO | Да | - |
| `MINIO_PASSWORD` | Пароль MinIO | Да | - |
| `KAFKA_BOOTSTRAP_SERVER` | Адрес Kafka | Нет | redpanda:9092 |
| `REDIS_HOST` | Адрес Redis | Нет | redis |
| `FTP_DOWNLOAD_FROM_YEAR` | Год начала загрузки | Нет | 1984 |
| `DOMAIN` | Домен для Traefik | Только для compose-traefik.yml | - |

## Полезные команды

- Остановить сервисы: `docker compose -f compose.yml down`
- Просмотр логов: `docker compose -f compose.yml logs -f`
- Пересборка образов: `docker compose -f compose.yml build`
