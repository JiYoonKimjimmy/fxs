package com.konai.fxs.testsupport.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisSentinel
import redis.embedded.RedisServer

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

}

object EmbeddedRedis {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    private const val REDIS_HOST = "localhost"
    private const val REDIS_PORT = 6379
    private const val REDIS_SENTINEL_MASTER = "mymaster"
    private const val REDIS_SENTINEL_PORT = 26379

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

}