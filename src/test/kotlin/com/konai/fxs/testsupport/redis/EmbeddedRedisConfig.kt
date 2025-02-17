package com.konai.fxs.testsupport.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@Profile("test")
@TestConfiguration
class EmbeddedRedisConfig {

    @PostConstruct
    fun embeddedRedisStart() {
        EmbeddedRedis.embeddedRedisStart()
    }

    @PreDestroy
    fun embeddedRedisStop() {
        EmbeddedRedis.embeddedRedisStop()
    }

    @Bean
    fun redissonClient(): RedissonClient {
        return EmbeddedRedis.redissonClient
    }

}