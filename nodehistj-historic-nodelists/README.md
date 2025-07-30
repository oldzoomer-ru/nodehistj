# NodehistJ Historic Nodelists Module

## Description

Provides REST API for accessing historical nodelist data stored in PostgreSQL with Redis caching.

## API Endpoints

### GET /historicNodelist

Returns nodelist entries by specified parameters.

Query parameters:

- year: 4-digit year (required)
- dayOfYear: day of year 1-366 (required)
- zone: zone number (optional)
- network: network number (optional, requires zone)
- node: node number (optional, requires zone and network)

Response example:

```json
[
  {
    "nodelistYear": 2025,
    "nodelistName": "nodelist.123"
  }
]
```

## Caching Strategy

- First request: loads from PostgreSQL
- Subsequent requests: served from Redis with following cache keys:
  - All data: "historicAllDataOfNodelist"
  - Zone data: "historicGetZoneNodelistEntry" (key: year-dayOfYear-zone)
  - Network data: "historicGetNetworkNodelistEntry" (key: year-dayOfYear-zone-network)
  - Node data: "historicGetNodeNodelistEntry" (key: year-dayOfYear-zone-network-node)

Cache TTL: 1 hour

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- Spring Data Redis
- PostgreSQL JDBC
- Lombok
