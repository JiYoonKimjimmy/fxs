package com.konai.fxs.testsupport.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

object RedisTestConfig {

    private const val REDIS_HOST = "localhost"
    private const val REDIS_PORT = "6379"
    private const val REDIS_SENTINEL_MASTER = "mymaster"
    private const val REDIS_SENTINEL_NODES = "localhost:26379"

    val lettuceConnectionFactory: LettuceConnectionFactory by lazy { redisConnectionFactory() }
    val numberRedisTemplate: RedisTemplate<String, Number> by lazy { numberRedisTemplate() }
    val redissonClient: RedissonClient by lazy { redissonClient() }

    private fun numberRedisTemplate(): RedisTemplate<String, Number> {
        return configureRedisTemplate(RedisTemplate<String, Number>()).also { it.afterPropertiesSet() }
    }

    private fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisSentinelConfiguration = RedisSentinelConfiguration(
            REDIS_SENTINEL_MASTER,
            setOf(REDIS_SENTINEL_NODES)
        )
        return LettuceConnectionFactory(redisSentinelConfiguration).also { it.afterPropertiesSet() }
    }

    private fun <T> configureRedisTemplate(redisTemplate: RedisTemplate<String, T>): RedisTemplate<String, T> {
        return redisTemplate.apply {
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = GenericJackson2JsonRedisSerializer()
            this.connectionFactory = lettuceConnectionFactory
        }
    }

    private fun redissonClient(): RedissonClient {
        return Redisson.create(Config().apply(this::useSingleServerTestConfig))
    }

    private fun useSingleServerTestConfig(config: Config) {
        config
            .useSingleServer()
            .setAddress("redis://$REDIS_HOST:$REDIS_PORT")
    }

}