**NodehistJ - System for Working with Historical FidoNet Node Data**

## Dependencies

- **MinIO** – file storage
- **Redis** – caching
- **Redpanda (Kafka)** – message exchange
- **PostgreSQL** – primary data store

## Quick Start

1. **Clone the repository**

   ```bash
   git clone https://github.com/oldzoomer-ru/nodehistj.git
   cd nodehistj
   ```

2. **Create a `.env` file with minimal settings**

   ```ini
   MINIO_USER=admin
   MINIO_PASSWORD=password
   ```

3. **Run the services (pick one variant)**

   **Basic variant**

   ```bash
   docker compose -f compose.yml up -d
   ```

   **For development (with MinIO)**

   ```bash
   docker compose -f compose-dev.yml up -d
   ```

   **With Traefik (for production)**

   ```bash
   docker compose -f compose-traefik.yml up -d
   ```

## Main Environment Variables

| Variable                 | Description                                     | Required | Default         |
|--------------------------|-------------------------------------------------|----------|-----------------|
| `MINIO_USER`             | MinIO user                                      | Yes      | –               |
| `MINIO_PASSWORD`         | MinIO password                                  | Yes      | –               |
| `POSTGRES_PASSWORD`      | PostgreSQL password                             | Yes      | –               |
| `KAFKA_BOOTSTRAP_SERVER` | Kafka address                                   | No       | `redpanda:9092` |
| `REDIS_HOST`             | Redis address                                   | No       | `redis`         |
| `FTP_DOWNLOAD_FROM_YEAR` | Start year for downloads                        | No       | `1984`          |
| `DOMAIN`                 | Traefik domain (only for `compose-traefik.yml`) | –        | –               |

## Useful Commands

- Stop services: `docker compose -f compose.yml down`
- View logs: `docker compose -f compose.yml logs -f`
- Rebuild images: `docker compose -f compose.yml build