package com.konai.fxs.infra.config

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@EnableRedisRepositories
@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisSentinelConfiguration = RedisSentinelConfiguration(
            redisProperties.sentinel.master,
            redisProperties.sentinel.nodes.toSet()
        )
        redisSentinelConfiguration.password = RedisPassword.of(redisProperties.password)
        return LettuceConnectionFactory(redisSentinelConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }

}