# NodehistJ Download Nodelists Module

## Description

Module for downloading nodelist files from FTP server and saving them to MinIO. After download sends notification via Kafka.

## Main Components

1. **FtpClient** - FTP client
2. **UpdateNodelists** - main file download service
3. **KafkaTopic** - Kafka topic configuration

## Configuration

Required parameters in application.properties:

```properties
ftp.host= # FTP server
ftp.port=21 # FTP port
ftp.user= # FTP user
ftp.password= # FTP password
ftp.path= # Base path on FTP server
ftp.downloadFromYear=2020 # Starting year for downloads

minio.url=http://minio:9000 # MinIO URL
minio.user=admin # MinIO user
minio.password=password # MinIO password
minio.bucket=nodelists # Storage bucket

kafka.bootstrap.server=kafka:9092 # Kafka server address
```

## Running

The module automatically downloads files on startup and then on schedule (every 24 hours by default).

For manual execution:

```java
@Autowired
private UpdateNodelists updateNodelists;

updateNodelists.updateNodelists();
```

## Workflow

1. Connects to FTP server
2. Checks for new nodelist files (format nodelist.###)
3. Downloads new files to MinIO
4. Sends list of new files via Kafka
5. Closes FTP connection

## Dependencies

- Spring Boot 3.5.3
- Apache Commons Net (FTP client)
- Spring Kafka
- MinIO Java SDK
