package ru.oldzoomer.minio.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinioConnectionProperties {
    private String url;
    private String user;
    private String password;
}
