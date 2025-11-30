package com.rentalservice.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.timeout}")
    private int timeout;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean sslEnabled;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String protocol = sslEnabled ? "rediss://" : "redis://";

        config.useSingleServer()
                .setAddress(protocol + redisHost + ":" + redisPort)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setTimeout(timeout)
                .setConnectTimeout(10000);

        return Redisson.create(config);
    }
}