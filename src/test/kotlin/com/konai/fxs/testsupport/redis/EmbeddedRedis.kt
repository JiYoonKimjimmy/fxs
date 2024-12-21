package com.konai.fxs.testsupport.redis

import com.konai.fxs.testsupport.TestCommonFunctions.objectMapper
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.embedded.RedisSentinel
import redis.embedded.RedisServer

object EmbeddedRedis {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    private const val REDIS_HOST = "localhost"
    private const val REDIS_PORT = 6379
    private const val REDIS_SENTINEL_MASTER = "mymaster"
    private const val REDIS_SENTINEL_PORT = 26379
    private const val REDIS_SENTINEL_NODES = "$REDIS_HOST:$REDIS_SENTINEL_PORT"

    private val redisServer: RedisServer by lazy {
        RedisServer
            .newRedisServer()
            .port(REDIS_PORT)
            .build()
    }

    private val redisSentinel: RedisSentinel by lazy {
        RedisSentinel
            .newRedisSentinel()
            .bind(REDIS_HOST)
            .masterName(REDIS_SENTINEL_MASTER)
            .port(REDIS_SENTINEL_PORT)
            .build()
    }

    val lettuceConnectionFactory: LettuceConnectionFactory by lazy { redisConnectionFactory() }
    val numberRedisTemplate: RedisTemplate<String, Number> by lazy { numberRedisTemplate() }
    val koreaeximExchangeRateRedisTemplate: RedisTemplate<String, V1KoreaeximExchangeRate> by lazy { koreaeximExchangeRateRedisTemplate() }
    val redissonClient: RedissonClient by lazy { redissonClient() }

    fun embeddedRedisStart() {
        if (!redisSentinel.isActive) {
            redisSentinel.start()
            logger.info("Embedded Redis Sentinel Started!!")
        }
        if (!redisServer.isActive) {
            redisServer.start()
            logger.info("Embedded Redis Server Started!!")
        }
    }

    fun embeddedRedisStop() {
        redisSentinel.stop()
        redisServer.stop()
        logger.info("Embedded Redis Server Stopped!!")
    }

    private fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisSentinelConfiguration = RedisSentinelConfiguration(
            REDIS_SENTINEL_MASTER,
            setOf(REDIS_SENTINEL_NODES)
        )
        return LettuceConnectionFactory(redisSentinelConfiguration).also { it.afterPropertiesSet() }
    }

    private fun numberRedisTemplate(): RedisTemplate<String, Number> {
        return RedisTemplate<String, Number>().apply {
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = GenericJackson2JsonRedisSerializer()
            this.connectionFactory = lettuceConnectionFactory
        }.also { it.afterPropertiesSet() }
    }

    private fun koreaeximExchangeRateRedisTemplate(): RedisTemplate<String, V1KoreaeximExchangeRate> {
        return RedisTemplate<String, V1KoreaeximExchangeRate>().apply {
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = Jackson2JsonRedisSerializer(objectMapper, V1KoreaeximExchangeRate::class.java)
            this.connectionFactory = lettuceConnectionFactory
        }.also { it.afterPropertiesSet() }
    }

    private fun redissonClient(): RedissonClient {
        return Redisson.create(Config().apply(this::useSingleServerTestConfig))
    }

    private fun useSingleServerTestConfig(config: Config) {
        config
            .useSingleServer()
            .setAddress("redis://${REDIS_HOST}:${REDIS_PORT}")
    }

}