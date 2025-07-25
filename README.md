# NodehistJ Project

## Project Description

NodehistJ is a system for working with network node historical data, consisting of several microservices:

1. **nodehistj-download-nodelists** - data download service
2. **nodehistj-historic-nodelists** - historical data service
3. **nodehistj-newest-nodelists** - current data service
4. **nodehistj-history-diff** - historical data comparison service

## Requirements

- Java 21
- Docker and Docker Compose
- Gradle (for building from sources)

## Quick Start

1. Clone the repository:

```bash
git clone https://github.com/oldzoomer-ru/nodehistj.git
cd nodehistj
```

2. Start services using Docker Compose:

```bash
docker compose -f compose.yml up -d
```

Services will be available at:

- nodehistj-historic-nodelists: <http://localhost:8080>
- nodehistj-newest-nodelists: <http://localhost:8081>
- nodehistj-history-diff: <http://localhost:8082>

## Configuration

Before starting, create a `.env` file in the project root with the following variables:

```ini
MINIO_ROOT_USER=admin
MINIO_ROOT_PASSWORD=password
REDIS_PASSWORD=redispass
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
GITHUB_USERNAME=your_username
GITHUB_TOKEN=your_token
FTP_DOWNLOAD_FROM_YEAR=2020
```

## Building from Sources

To build all services:

```bash
./gradlew build
```

To build a specific service:

```bash
./gradlew :nodehistj-historic-nodelists:build
```

## Architecture

The project uses:

- Spring Boot 3.5.3
- PostgreSQL for data storage
- MinIO for file storage
- Kafka for inter-service communication
- Redis for caching

## Project Modules

- `nodehistj-download-nodelists` - downloads data from external sources
- `nodehistj-historic-nodelists` - API for historical data
- `nodehistj-newest-nodelists` - API for current data
- `nodehistj-history-diff` - data version comparison service
- `lib/common-utils` - common utilities for all services
