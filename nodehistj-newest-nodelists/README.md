# NodehistJ Newest Nodelists Module

## Description

Module for working with current versions of nodelists.

## Main Features

- Getting current nodelist versions
- API for accessing up-to-date data
- Results caching

## API Endpoints (Version 1)

### GET /api/v1/nodelists/latest

Returns the latest version of nodelist.

Parameters:

- limit: maximum number of nodes (default 1000)
- offset: pagination offset (default 0)

Example response:

```json
{
  "nodes": [
    {
      "nodelistEntry": {
        "nodelistYear": 2025,
        "nodelistName": "nodelist.003"
      },
      "nodeName": "Node 1",
      "location": "Washington, DC",
      "sysOpName": "John Doe",
      "phone": "123-456-7890",
      "baudRate": 9600,
      "flags": ["CM", "MO"]
    }
  ]
}
```

### GET /api/v1/nodelists/{nodeId}

Returns information about specific node.

Example response:

```json
{
  "nodelistEntry": {
    "nodelistYear": 2025,
    "nodelistName": "nodelist.003"
  },
  "nodeName": "Node 1",
  "location": "Washington, DC",
  "sysOpName": "John Doe",
  "phone": "123-456-7890",
  "baudRate": 9600,
  "flags": ["CM", "MO"]
}
```

## Module Structure

- `controller/` - REST controllers
- `service/` - business logic
- `repo/` - database repositories
- `entity/` - database entities
- `dto/` - data transfer objects
- `mapper/` - mappers between entities and DTOs

## Dependencies

- Spring Boot 3.5.3
- Spring Data JPA
- Redis
- MapStruct

## Configuration

Main settings in `src/main/resources/application.yml`
