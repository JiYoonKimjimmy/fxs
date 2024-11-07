package com.konai.fxs.testsupport

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Duration

@TestConfiguration
class TestRedisConfig(
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: String
) {

    @PostConstruct
    fun setup() {
        EmbeddedRedis.start()
    }

    @PreDestroy
    fun shutdown() {
        EmbeddedRedis.stop()
    }

    @Bean
    fun redissonClient(): RedissonClient {
        return Redisson.create(redissonClientConfig())
    }

    private fun redissonClientConfig(): Config {
        return Config().apply(this::useTestSingleServerConfig)
    }

    private fun useTestSingleServerConfig(config: Config) {
        config
            .useSingleServer()
            .setAddress("redis://$host:$port")
            .setTimeout(Duration.ofSeconds(10).toMillis().toInt())
            .setConnectTimeout(Duration.ofSeconds(10).toMillis().toInt())
    }

}