# AGENTS.md

This document serves as a guide for contributors to the NodehistJ project, providing essential information about the
project structure, development workflow, and coding standards.

## Project Structure and Module Organization

NodehistJ is a multi-module Spring Boot application designed for managing FidoNet nodelist data. The project consists of
several microservices:

- **Historic Nodelists Service** (`nodehistj-historic-nodelists`) - Stores and retrieves historical FidoNet nodelist
  data
- **Newest Nodelists Service** (`nodehistj-newest-nodelists`) - Provides access to current nodelist data
- **History Diff Service** (`nodehistj-history-diff`) - Manages node history and difference tracking
- **Download Nodelists Service** (`nodehistj-download-nodelists`) - Handles downloading of nodelist archives
- **MinIO Library** (`lib/minio`) - Provides MinIO client integration

Each module follows standard Spring Boot structure with `src/main` and `src/test` directories.

## Build, Test, and Development Commands

### Building the Project

```bash
./gradlew build
```

### Running Individual Services

```bash
cd nodehistj-historic-nodelists && ./gradlew bootRun
cd nodehistj-newest-nodelists && ./gradlew bootRun
cd nodehistj-history-diff && ./gradlew bootRun
```

### Running Tests

```bash
./gradlew test
```

### Docker Compose for Development

```bash
docker compose -f compose-dev.yml up -d
```

## Code Style and Naming Conventions

- **Java Style**: Follow Spring Boot conventions with proper package structure
- **Naming**: Use camelCase for variables and methods, PascalCase for classes
- **Documentation**: Include JavaDoc comments for public APIs
- **Testing**: Follow the pattern `ClassNameTest` for test classes

## VCS Guidelines: Commits and Pull Requests

### Commit Messages

- Use present tense ("Add feature" not "Added feature")
- Keep messages concise but descriptive
- Reference related issues with `#issue-number`

### Pull Request Requirements

- Include a clear description of changes
- Link to relevant issues
- Ensure all tests pass
- Add necessary documentation updates

## Development Environment Setup

The project requires:

- Java 25+
- Gradle 9+
- Docker for development environment setup

To start the complete development environment:

```bash
docker compose -f compose-dev.yml up -d
```

## Special Considerations

### Dependencies

The system uses several key technologies:

- **MinIO** for file storage of nodelist archives
- **Redis** for caching
- **Redpanda (Kafka)** for message exchange
- **PostgreSQL** for primary data storage

### Configuration

Environment variables are used for configuration, with defaults provided in Docker compose files. Key variables include:

- `MINIO_USER` and `MINIO_PASSWORD`
- `POSTGRES_PASSWORD`
- `KAFKA_BOOTSTRAP_SERVER`
- `REDIS_HOST`

This project is designed for managing historical FidoNet nodelist data and provides RESTful APIs to query and retrieve
node information across different time periods.