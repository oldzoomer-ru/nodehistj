# NodehistJ Download Nodelists Module

## Description

Module for downloading nodelist files from FTP server and saving them to MinIO. After download sends notification via Kafka.

## Main Components

1. **FtpClient** - FTP client
2. **UpdateNodelists** - main file download service
3. **KafkaTopic** - Kafka topic configuration

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
