package com.ice.framework.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Author: qiang.su
 * Date: 2020/4/1
 * Msg:
 */

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@ConditionalOnProperty("spring.redis.password")
@Setter
@Getter
@Primary
public class RedissonProperties {

    private int timeout;

    private String host;

    private String port;

    private String password;

    private int database;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize = 10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;

    private String[] sentinelAddresses;

    private String masterName;

}
