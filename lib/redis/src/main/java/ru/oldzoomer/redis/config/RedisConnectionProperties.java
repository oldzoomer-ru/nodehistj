package ru.oldzoomer.redis.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisConnectionProperties {
    private String host;
    private int port;
}
