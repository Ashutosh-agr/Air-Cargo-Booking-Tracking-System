package com.aircargo.booking.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress("rediss://content-shepherd-7132.upstash.io:6379")
              .setPassword(redisPassword) 
              .setSslEnableEndpointIdentification(true)
              .setConnectionMinimumIdleSize(5)
              .setConnectionPoolSize(20)
              .setIdleConnectionTimeout(10000)
              .setConnectTimeout(10000)
              .setTimeout(3000);

        return Redisson.create(config);
    }
}
