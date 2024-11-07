package com.konai.fxs.testsupport

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.embedded.RedisSentinel
import redis.embedded.RedisServer

object EmbeddedRedis {
        // logger
        private val logger = LoggerFactory.getLogger(this::class.java)

        private const val REDIS_HOST = "localhost"
        private const val REDIS_PORT = 6381
        private const val REDIS_SENTINEL_MASTER = "mymaster"
        private const val REDIS_SENTINEL_PORT = 26381

        private val redisServer: RedisServer by lazy {
            RedisServer.newRedisServer().port(REDIS_PORT).build()
        }

        private val redisSentinel: RedisSentinel by lazy {
            RedisSentinel
                .newRedisSentinel()
                .bind(REDIS_HOST)
                .masterName(REDIS_SENTINEL_MASTER)
                .masterPort(REDIS_SENTINEL_PORT)
                .build()
        }

        private val lettuceConnectionFactory: LettuceConnectionFactory by lazy {
            val redisConnectionFactory = LettuceConnectionFactory()
            redisConnectionFactory.standaloneConfiguration.apply {
                hostName = REDIS_HOST
                port = REDIS_PORT
            }
            redisConnectionFactory.afterPropertiesSet()
            redisConnectionFactory
        }

        val numberRedisTemplate: RedisTemplate<String, Number> by lazy {
            val numberRedisTemplate = RedisTemplate<String, Number>().apply {
                this.keySerializer = StringRedisSerializer()
                this.valueSerializer = GenericJackson2JsonRedisSerializer()
                this.connectionFactory = lettuceConnectionFactory
            }
            numberRedisTemplate.afterPropertiesSet()
            numberRedisTemplate
        }

        fun start() {
            if (!redisServer.isActive) {
                redisServer.start()
                redisSentinel.start()
                logger.info("Embedded Redis Server Started!!")
            }
        }

        fun stop() {
            lettuceConnectionFactory.resetConnection()
            redisServer.stop()
            redisSentinel.stop()
            logger.info("Embedded Redis Server Stopped!!")
        }

    }