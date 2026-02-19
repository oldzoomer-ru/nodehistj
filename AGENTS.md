# NodehistJ Agent Guide

## Project Structure and Module Organization

NodehistJ is a multi-module Gradle project managing FidoNet nodelist data with the following modules:

- `nodehistj-download-nodelists` - Downloads and processes nodelist data
- `nodehistj-historic-nodelists` - Stores and retrieves historical nodelist data (`/historic` context)
- `nodehistj-newest-nodelists` - Provides access to current nodelist data (`/newest` context)
- `nodehistj-history-diff` - Manages node history and difference tracking (`/diff` context)
- `lib:minio` - MinIO client library

## Build, Test and Development Commands

### Build

```bash
./gradlew build
```

### Test

```bash
./gradlew test
```

### Run (Development)

```bash
./gradlew :nodehistj-historic-nodelists:bootRun
./gradlew :nodehistj-newest-nodelists:bootRun
./gradlew :nodehistj-history-diff:bootRun
```

## Code Style and Naming Conventions

- Java/Kotlin code follows standard conventions
- Module names follow the `nodehistj-*` pattern
- REST endpoints use consistent context paths (`/historic`, `/newest`, `/diff`)
- Tests are organized by module with clear naming conventions

## VCS Recommendations

### Commits

- Use descriptive commit messages following conventional commit format
- Group related changes in single commits
- Reference relevant issues in commit messages

### Pull Requests

- Include clear descriptions of changes
- Link to related issues
- Ensure all tests pass before merging
- Provide context about the impact of changes

## Architecture Overview

The system consists of multiple microservices that work together to provide complete nodelist management:

- **Historic Nodelists Service** (`/historic` context) - Stores and retrieves historical FidoNet nodelist data
- **Newest Nodelists Service** (`/newest` context) - Provides access to current nodelist data
- **History Diff Service** (`/diff` context) - Manages node history and difference tracking

## Dependencies

- **MinIO** – File storage for nodelist archives
- **Redis** – Caching for improved performance
- **Redpanda (Kafka)** – Message exchange for data synchronization
- **PostgreSQL** – Primary data store for all nodelist information

## API Endpoints

### Historic Nodelists Service (`/historic` context)

#### Get Historical Nodelist Entries

**Endpoint:** `GET /historic/historicNodelist`

Retrieves historical FidoNet nodelist entries with optional filtering by date and node identifiers.

**Parameters:**

| Parameter   | Type    | Required | Description                  |
|-------------|---------|----------|------------------------------|
| `year`      | Integer | Yes      | Year (e.g., 2024)            |
| `dayOfYear` | Integer | Yes      | Day of year (1-366)          |
| `zone`      | Integer | No       | Zone identifier (1-32767)    |
| `network`   | Integer | No       | Network identifier (1-32767) |
| `node`      | Integer | No       | Node identifier (0-32767)    |

### Newest Nodelists Service (`/newest` context)

#### Get Current Nodelist Entries

**Endpoint:** `GET /newest/nodelist`

Retrieves current FidoNet nodelist entries with optional filtering by zone, network, and node.

**Parameters:**

| Parameter | Type    | Required | Description                  |
|-----------|---------|----------|------------------------------|
| `zone`    | Integer | No       | Zone identifier (1-32767)    |
| `network` | Integer | No       | Network identifier (1-32767) |
| `node`    | Integer | No       | Node identifier (0-32767)    |

### History Diff Service (`/diff` context)

#### Get Node History

**Endpoint:** `GET /diff/history`

Retrieves node history with optional filtering by zone, network, and node.

**Parameters:**

| Parameter | Type    | Required | Description                  |
|-----------|---------|----------|------------------------------|
| `zone`    | Integer | No       | Zone identifier (1-32767)    |
| `network` | Integer | No       | Network identifier (1-32767) |
| `node`    | Integer | No       | Node identifier (0-32767)    |
| `page`    | Integer | No       | Page number (default: 0)     |
| `size`    | Integer | No       | Page size (default: 20)      |

## Additional Information

The project uses Docker Compose for deployment with multiple configuration files:

- `compose.yml` - Basic deployment
- `compose-dev.yml` - Development with MinIO
- `compose-traefik.yml` - Production with Traefik

Environment variables required for development:

- `S3_USER` - MinIO user
- `S3_PASSWORD` - MinIO password
- `POSTGRES_PASSWORD` - PostgreSQL password
- `KAFKA_BOOTSTRAP_SERVER` - Kafka address (default: redpanda:9092)
- `REDIS_HOST` - Redis address (default: redis)
