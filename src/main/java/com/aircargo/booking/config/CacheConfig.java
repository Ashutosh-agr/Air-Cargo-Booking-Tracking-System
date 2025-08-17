package com.aircargo.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            @Value("${app.cache.ttl.route:PT15M}")Duration routeTtl,
            @Value("${app.cache.ttl.flight:PT30M}") Duration flightTtl){
        return (builder) -> builder
                .withCacheConfiguration("routeCache",
                        org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(routeTtl))
                .withCacheConfiguration("flightCache",
                        org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(flightTtl));
    }
}
