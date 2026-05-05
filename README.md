# NodehistJ - system for working with historical FidoNet nodelist data

## Overview

NodehistJ is a comprehensive system for managing and accessing FidoNet nodelist data, including historical records, current nodelists, and node history differences. The system provides RESTful APIs to query and retrieve node information across different time periods and contexts.

## Architecture

The system consists of multiple microservices that work together to provide complete nodelist management:

- **Historic Nodelists Service** (`/historic` context) - Stores and retrieves historical FidoNet nodelist data
- **Newest Nodelists Service** (`/newest` context) - Provides access to current nodelist data
- **History Diff Service** (`/diff` context) - Manages node history and difference tracking

## Dependencies

- **Any S3 storage** – File storage for nodelist archives
- **Redis** – Caching for improved performance
- **Redpanda (Kafka)** – Message exchange for data synchronization
- **PostgreSQL** – Primary data store for all nodelist information

## Quick Start

### Basic variant

```bash
docker compose -f compose.yml up -d
```

### For development (with MinIO)

```bash
docker compose -f compose-dev.yml up -d
```

### With Traefik (for production)

```bash
docker compose -f compose-traefik.yml up -d
```

## Main Environment Variables

| Variable                 | Description                                     | Required               | Default         |
|--------------------------|-------------------------------------------------|------------------------|-----------------|
| `S3_USER`                | MinIO user                                      | Yes                    | –               |
| `S3_PASSWORD`            | MinIO password                                  | Yes                    | –               |
| `POSTGRES_PASSWORD`      | PostgreSQL password                             | Yes                    | –               |
| `KAFKA_BOOTSTRAP_SERVER` | Kafka address                                   | No                     | `redpanda:9092` |
| `REDIS_HOST`             | Redis address                                   | No                     | `redis`         |
| `FTP_DOWNLOAD_FROM_YEAR` | Start year for downloads                        | No                     | `1984`          |
| `DOMAIN`                 | Traefik domain (only for `compose-traefik.yml`) | Yes (only for Traefik) | –               |

## Useful Commands

- Stop services: `docker compose -f compose.yml down`
- View logs: `docker compose -f compose.yml logs -f`
- Rebuild images: `docker compose -f compose.yml build`

## Development Setup

### Prerequisites

- Docker and Docker Compose
- Java 25
- Gradle

### Building the Project

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Running Locally

```bash
# Start dependencies
docker compose -f compose-dev.yml up -d

# Build and run services
./gradlew :nodehistj-historic-nodelists:bootRun
./gradlew :nodehistj-newest-nodelists:bootRun
./gradlew :nodehistj-history-diff:bootRun
```
