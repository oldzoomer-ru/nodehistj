# NodehistJ

NodehistJ is a project that consists of multiple services for downloading, processing, and serving historical
nodelists of Fidonet. The project leverages modern technologies such as Spring Boot, Kafka, MinIO, Redis, and PostgreSQL.

## Table of Contents

- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running the Services](#running-the-services)
- [Environment Variables](#environment-variables)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Project Structure

The project is composed of the following services:

- **nodehistj-download-nodelists**: A service that downloads nodelists from an FTP server and uploads them to MinIO.
- **nodehistj-historic-nodelists**: A service that processes and serves the nodelists.

## Prerequisites

- Docker
- Docker Compose
- Java 21
- Maven

## Setup

1. **Clone the repository:**

   ```sh
   git clone https://github.com/oldzoomer-gh/nodehistj.git
   cd nodehistj
   ```

2. **Build the Docker images:**

   ```sh
   docker-compose build
   ```

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
- nodehistj-historic-nodelists

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

This service processes and serves the nodelists. It provides endpoints to retrieve node list entries.

#### Endpoints

- **Get all nodelist entries:**

  ```http
  GET /nodelist
  ```

- **Get zone nodelist entry:**

  ```http
  GET /nodelist?zone={zone}
  ```

- **Get network nodelist entry:**

  ```http
  GET /nodelist?zone={zone}&network={network}
  ```

- **Get node nodelist entry:**

  ```http
  GET /nodelist?zone={zone}&network={network}&node={node}
  ```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
