# NodehistJ

NodehistJ is a Gradle multi-module project that consists of multiple services for downloading, processing, and serving historical
nodelists of Fidonet. The project leverages modern technologies such as Spring Boot, Kafka, MinIO, Redis, and PostgreSQL.

## Table of Contents

- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Building](#building)
- [Docker](#docker)
- [Running the Services](#running-the-services)
- [Environment Variables](#environment-variables)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Project Structure

The project is organized as a Gradle multi-module project with the following modules:

- **nodehistj-download-nodelists**: A service that downloads nodelists from an FTP server and uploads them to MinIO.
- **nodehistj-historic-nodelists**: A service that processes and serves historical nodelists (port 8080).
- **nodehistj-newest-nodelists**: A service that processes and serves the newest nodelists.
- **nodehistj-history-diff**: A service that tracks and provides history of changes between nodelists for each node.

## Prerequisites

- Docker
- Docker Compose
- Java 21
- Gradle 8.14+

## Setup

1. **Clone the repository:**

   ```sh
   git clone https://github.com/oldzoomer-gh/nodehistj.git
   cd nodehistj
   ```

2. **Build the project:**

   ```sh
   ./gradlew build
   ```

3. **Build the Docker images:**

   ```sh
   docker-compose build
   ```

## Building

The project uses Gradle multi-module structure. You can build all modules at once or individual modules:

**Build all modules:**
```sh
./gradlew build
```

**Build specific module:**
```sh
./gradlew :nodehistj-download-nodelists:build
./gradlew :nodehistj-historic-nodelists:build
./gradlew :nodehistj-newest-nodelists:build
./gradlew :nodehistj-history-diff:build
```

**Run tests:**
```sh
./gradlew test
```

**Run tests for specific module:**
```sh
./gradlew :nodehistj-historic-nodelists:test
```

## Docker

The project uses a unified Dockerfile that can build all services. Each service is built using build arguments:

**Build specific service:**
```sh
# Build download service
docker build --build-arg SERVICE_NAME=nodehistj-download-nodelists -t nodehistj-download-nodelists .

# Build historic service (requires GitHub credentials)
docker build \
  --build-arg SERVICE_NAME=nodehistj-historic-nodelists \
  --secret id=github_username,env=GITHUB_USERNAME \
  --secret id=github_token,env=GITHUB_TOKEN \
  -t nodehistj-historic-nodelists .

# Build newest service (requires GitHub credentials)
docker build \
  --build-arg SERVICE_NAME=nodehistj-newest-nodelists \
  --secret id=github_username,env=GITHUB_USERNAME \
  --secret id=github_token,env=GITHUB_TOKEN \
  -t nodehistj-newest-nodelists .

# Build history-diff service (requires GitHub credentials)
docker build \
  --build-arg SERVICE_NAME=nodehistj-history-diff \
  --secret id=github_username,env=GITHUB_USERNAME \
  --secret id=github_token,env=GITHUB_TOKEN \
  -t nodehistj-history-diff .
```

**Build all services:**
```sh
docker-compose build
```

### Docker Build Arguments

- `SERVICE_NAME`: Specifies which service to build (required)
- `SKIP_TESTS`: Set to "true" to skip tests during build (default: "true")

### Docker Secrets

Some services require GitHub credentials to access private Maven repositories:
- `github_username`: Your GitHub username
- `github_token`: Your GitHub personal access token

## Running the Services

To start all services, run:

```sh
docker-compose up -d
```

This will start the following services:

- MinIO
- Redis
- Kafka
- PostgreSQL
- nodehistj-download-nodelists
- nodehistj-historic-nodelists (port 8080)
- nodehistj-newest-nodelists (port 8081)
- nodehistj-history-diff (port 8082)

## Environment Variables

The services require several environment variables to be set. You can set these variables in a
`.env` file in the root directory of the project. Here is an example of the required environment variables:

```env
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin
REDIS_PASSWORD=redispassword
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
FTP_DOWNLOAD_FROM_YEAR=2020
GITHUB_USERNAME=yourgithubusername
GITHUB_TOKEN=yourgithubtoken
```

## Usage

### nodehistj-download-nodelists

This service downloads nodelists from an FTP server and uploads them to MinIO. The service is scheduled to run every day.

### nodehistj-historic-nodelists

This service processes and serves historical nodelists. It provides endpoints to retrieve node list entries for specific dates.

#### Endpoints

- **Get all nodelist entries for a specific date:**

  ```http
  GET /historicNodelist?year={year}&dayOfYear={dayOfYear}
  ```

- **Get zone nodelist entry for a specific date:**

  ```http
  GET /historicNodelist?year={year}&dayOfYear={dayOfYear}&zone={zone}
  ```

- **Get network nodelist entry for a specific date:**

  ```http
  GET /historicNodelist?year={year}&dayOfYear={dayOfYear}&zone={zone}&network={network}
  ```

- **Get node nodelist entry for a specific date:**

  ```http
  GET /historicNodelist?year={year}&dayOfYear={dayOfYear}&zone={zone}&network={network}&node={node}
  ```

### nodehistj-newest-nodelists

This service processes and serves the newest nodelists. It provides endpoints to retrieve the latest node list entries.

#### Endpoints

- **Get all latest nodelist entries:**

  ```http
  GET /nodelist
  ```

- **Get latest zone nodelist entry:**

  ```http
  GET /nodelist?zone={zone}
  ```

- **Get latest network nodelist entry:**

  ```http
  GET /nodelist?zone={zone}&network={network}
  ```

- **Get latest node nodelist entry:**

  ```http
  GET /nodelist?zone={zone}&network={network}&node={node}
  ```

### nodehistj-history-diff

This service tracks and provides history of changes between nodelists for each node. It analyzes differences between consecutive nodelists and stores change history.

#### Endpoints

- **Get history for a specific node:**

  ```http
  GET /history/node/{zone}/{network}/{node}?page={page}&size={size}
  ```

- **Get history for a specific network:**

  ```http
  GET /history/network/{zone}/{network}?page={page}&size={size}
  ```

- **Get history for a specific zone:**

  ```http
  GET /history/zone/{zone}?page={page}&size={size}
  ```

- **Get all history entries:**

  ```http
  GET /history/all?page={page}&size={size}
  ```

- **Get changes for a specific date:**

  ```http
  GET /history/date/{date}
  ```

- **Get changes between dates:**

  ```http
  GET /history/range?startDate={startDate}&endDate={endDate}&page={page}&size={size}
  ```

- **Get changes by type (ADDED, REMOVED, MODIFIED):**

  ```http
  GET /history/type/{changeType}?page={page}&size={size}
  ```

- **Get summary of changes for a date range:**

  ```http
  GET /history/summary?startDate={startDate}&endDate={endDate}
  ```

- **Get most active nodes (nodes with most changes):**

  ```http
  GET /history/active-nodes?startDate={startDate}&endDate={endDate}&page={page}&size={size}
  ```

## Module Dependencies

The project uses a shared dependency management approach:

- **Common dependencies** are defined in the root `build.gradle`
- **Module-specific dependencies** are defined in each module's `build.gradle`
- **Version management** is centralized using extension properties

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.