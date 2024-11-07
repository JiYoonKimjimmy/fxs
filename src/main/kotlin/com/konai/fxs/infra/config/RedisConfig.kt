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
        return LettuceConnectionFactory(redisSentinelConfiguration).also { it.afterPropertiesSet() }
    }

    @Bean
    fun numberRedisTemplate(): RedisTemplate<String, Number> {
        return configureRedisTemplate(RedisTemplate<String, Number>()).also { it.afterPropertiesSet() }
    }

    private fun <T> configureRedisTemplate(redisTemplate: RedisTemplate<String, T>): RedisTemplate<String, T> {
        return redisTemplate.apply {
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = GenericJackson2JsonRedisSerializer()
            this.connectionFactory = redisConnectionFactory()
        }
    }

}