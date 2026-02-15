# NodehistJ - system for working with historical FidoNet nodelist data

## Overview

NodehistJ is a comprehensive system for managing and accessing FidoNet nodelist data, including historical records, current nodelists, and node history differences. The system provides RESTful APIs to query and retrieve node information across different time periods and contexts.

## Architecture

The system consists of multiple microservices that work together to provide complete nodelist management:

- **Historic Nodelists Service** (`/historic` context) - Stores and retrieves historical FidoNet nodelist data
- **Newest Nodelists Service** (`/newest` context) - Provides access to current nodelist data
- **History Diff Service** (`/diff` context) - Manages node history and difference tracking

## Dependencies

- **MinIO** – File storage for nodelist archives
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

## API Endpoints

### Historic Nodelists Service (`/historic` context)

#### Get Historical Nodelist Entries

**Endpoint:** `GET /historic/historicNodelist`

**Description:** Retrieves historical FidoNet nodelist entries with optional filtering by year, day of year, zone, network, and node.

**Parameters:**

| Parameter   | Type   | Required | Description                              |
|-------------|--------|----------|------------------------------------------|
| `year`      | Year   | Yes      | Year of nodelist (e.g., 2024)            |
| `dayOfYear` | Integer| Yes      | Day of year (1-366)                      |
| `zone`      | Integer| No       | Zone identifier (1-32767)                |
| `network`   | Integer| No       | Network identifier (1-32767)             |
| `node`      | Integer| No       | Node identifier (0-32767)                |

**Method Signature:**

```java
@GetMapping("/historicNodelist")
public Set<NodeEntryDto> getNodelistEntry(
    @RequestParam Year year,
    @RequestParam @Min(1) @Max(366) Integer dayOfYear,
    @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
    @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
    @RequestParam(required = false) @Min(0) @Max(32767) Integer node
)
```

**Request Examples:**

```bash
# Get all nodes for a specific date
GET /historic/historicNodelist?year=2024&dayOfYear=180

# Get nodes in zone 2
GET /historic/historicNodelist?year=2024&dayOfYear=180&zone=2

# Get nodes in zone 2, network 10
GET /historic/historicNodelist?year=2024&dayOfYear=180&zone=2&network=10

# Get specific node
GET /historic/historicNodelist?year=2024&dayOfYear=180&zone=2&network=10&node=5
```

**Response Format:**

```json
[
  {
    "zone": 2,
    "network": 10,
    "node": 5,
    "name": "Example Node",
    "location": "Example Location",
    "phone": "+1-234-567-8900",
    "password": "secret",
    "description": "Example node description"
  }
]
```

**Response Codes:**

- `200` - Successful operation
- `400` - Invalid parameters (validation failed)
- `404` - Requested data not found
- `500` - Internal server error

### Newest Nodelists Service (`/newest` context)

#### Get Current Nodelist Entries

**Endpoint:** `GET /newest/nodelist`

**Description:** Retrieves current FidoNet nodelist entries with optional filtering by zone, network, and node.

**Parameters:**

| Parameter   | Type   | Required | Description                              |
|-------------|--------|----------|------------------------------------------|
| `zone`      | Integer| No       | Zone identifier (1-32767)                |
| `network`   | Integer| No       | Network identifier (1-32767)             |
| `node`      | Integer| No       | Node identifier (0-32767)                |

**Method Signature:**

```java
@GetMapping("/nodelist")
public Set<NodeEntryDto> getNodelistEntry(
    @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
    @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
    @RequestParam(required = false) @Min(0) @Max(32767) Integer node
)
```

**Request Examples:**

```bash
# Get all nodes
GET /newest/nodelist

# Get nodes in zone 2
GET /newest/nodelist?zone=2

# Get nodes in zone 2, network 10
GET /newest/nodelist?zone=2&network=10

# Get specific node
GET /newest/nodelist?zone=2&network=10&node=5
```

**Response Format:**

```json
[
  {
    "zone": 2,
    "network": 10,
    "node": 5,
    "name": "Example Node",
    "location": "Example Location",
    "phone": "+1-234-567-8900",
    "password": "secret",
    "description": "Example node description"
  }
]
```

**Response Codes:**

- `200` - Successful operation
- `400` - Invalid parameters (validation failed)
- `404` - Requested data not found
- `500` - Internal server error

### History Diff Service (`/diff` context)

#### Get Node History

**Endpoint:** `GET /diff/history`

**Description:** Retrieves node history with optional filtering by zone, network, and node.

**Parameters:**

| Parameter   | Type   | Required | Description                              |
|-------------|--------|----------|------------------------------------------|
| `zone`      | Integer| No       | Zone identifier (1-32767)                |
| `network`   | Integer| No       | Network identifier (1-32767)             |
| `node`      | Integer| No       | Node identifier (0-32767)                |
| `page`      | Integer| No       | Page number (default: 0)                 |
| `size`      | Integer| No       | Page size (default: 20)                  |

**Method Signature:**

```java
@GetMapping
public Page<NodeHistoryEntryDto> getHistory(
    @RequestParam(required = false) @Min(1) @Max(32767) Integer zone,
    @RequestParam(required = false) @Min(1) @Max(32767) Integer network,
    @RequestParam(required = false) @Min(0) @Max(32767) Integer node,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
)
```

**Request Examples:**

```bash
# Get all history
GET /diff/history

# Get zone history
GET /diff/history?zone=1

# Get network history
GET /diff/history?zone=1&network=2

# Get node history
GET /diff/history?zone=1&network=2&node=3

# Get paginated results
GET /diff/history?page=1&size=10
```

**Response Format:**

```json
{
  "content": [
    {
      "zone": 1,
      "network": 2,
      "node": 3,
      "changeDate": "2024-01-15T10:30:00Z",
      "oldName": "Old Name",
      "newName": "New Name",
      "oldLocation": "Old Location",
      "newLocation": "New Location"
    }
  ],
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 1,
  "empty": false
}
```

**Response Codes:**

- `200` - Successful operation
- `400` - Invalid parameters (validation failed)
- `404` - Requested data not found
- `500` - Internal server error

## Error Handling

The system implements standardized error handling with appropriate HTTP status codes and error messages:

### Common Error Responses

#### 400 Bad Request

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid parameter combination: node specified without network",
  "path": "/historic/historicNodelist"
}
```

#### 404 Not Found

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "No data found for requested parameters",
  "path": "/historic/historicNodelist"
}
```

#### 500 Internal Server Error

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred while processing the request",
  "path": "/historic/historicNodelist"
}
```

## Useful Commands

- Stop services: `docker compose -f compose.yml down`
- View logs: `docker compose -f compose.yml logs -f`
- Rebuild images: `docker compose -f compose.yml build`

## Caching Strategy

All API endpoints are configured with caching to improve performance:

- **Historic Nodelists Service**: Cache key prefix `historic:`
- **Newest Nodelists Service**: Cache key prefix `nodelist:`
- **History Diff Service**: Cache key prefix `diff:`

Cache TTL is set to 12 hours for all services.

## Data Model

### Node Entry

| Field         | Type     | Description                          |
|---------------|----------|--------------------------------------|
| `zone`        | Integer  | FidoNet zone identifier              |
| `network`     | Integer  | FidoNet network identifier           |
| `node`        | Integer  | FidoNet node identifier              |
| `name`        | String   | Node name                            |
| `location`    | String   | Node location                        |
| `phone`       | String   | Node phone number                    |
| `password`    | String   | Node password                        |
| `description` | String   | Node description                     |

### Node History Entry

| Field         | Type     | Description                          |
|---------------|----------|--------------------------------------|
| `zone`        | Integer  | FidoNet zone identifier              |
| `network`     | Integer  | FidoNet network identifier           |
| `node`        | Integer  | FidoNet node identifier              |
| `changeDate`  | DateTime | Date of change                       |
| `oldName`     | String   | Previous name                        |
| `newName`     | String   | New name                             |
| `oldLocation` | String   | Previous location                    |
| `newLocation` | String   | New location                         |

## Development Setup

### Prerequisites

- Docker and Docker Compose
- Java 17+
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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
