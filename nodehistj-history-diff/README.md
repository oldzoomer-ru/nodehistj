# NodehistJ History Diff Module

## Description

Service for tracking and comparing different versions of nodelists with history and difference reports.

## Main Features

- Tracks node history changes
- Compares nodelist versions
- Generates detailed difference reports
- Supports multiple comparison algorithms
- Stores history and comparison results in PostgreSQL

## API Endpoints (Base path: /history)

### Node History

#### GET /node/{zone}/{network}/{node}

Get history for a specific node

Parameters:

- zone: Integer - Zone ID
- network: Integer - Network ID
- node: Integer - Node ID
- page: Integer (default: 0) - Page number
- size: Integer (default: 20) - Page size

Response:

```json
{
  "content": [
    {
      "id": 123,
      "zone": 1,
      "network": 2,
      "node": 3,
      "changeType": "MODIFIED",
      "changeDate": "2025-07-10",
      "details": "Status changed"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100
}
```

#### GET /network/{zone}/{network}

Get history for a network

#### GET /zone/{zone}

Get history for a zone

#### GET /all

Get all history entries

### Date-based Queries

#### GET /date/{date}

Get changes for specific date

#### GET /range

Get changes between dates (startDate, endDate)

#### GET /summary

Get summary of changes for date range

### Other Queries

#### GET /type/{changeType}

Get changes by type (ADDED, REMOVED, MODIFIED)

#### GET /active-nodes

Get most active nodes in period

## Comparison Endpoint (POST /diff)

Compares two nodelists:

Request:

```json
{
  "firstVersion": "2025-07-10",
  "secondVersion": "2025-07-09",
  "algorithm": "LEVENSHTEIN"
}
```

Response:

```json
{
  "totalChanges": 15,
  "addedNodes": 5,
  "removedNodes": 3,
  "modifiedNodes": 7,
  "changes": [
    {
      "nodeId": "node1",
      "changeType": "MODIFIED",
      "details": "Status changed"
    }
  ]
}
```

## Configuration

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/nodelists_diff
spring.datasource.username=postgres
spring.datasource.password=postgres

diff.default-algorithm=LEVENSHTEIN
diff.max-comparison-time=5000 # ms
```

## Running Tests

```bash
./gradlew :nodehistj-history-diff:test
```

## Dependencies

- Spring Boot 3.5.3
- Apache Commons Text
- Spring Data JPA
- PostgreSQL JDBC
