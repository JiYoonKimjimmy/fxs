package com.konai.fxs.infra.config

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RedissonConfig(
    @Value("\${spring.profiles.active}")
    private val environment: String,
    @Value("\${spring.data.redis.host}")
    private val host: String,
    @Value("\${spring.data.redis.port}")
    private val port: String,
    @Value("\${spring.data.redis.password}")
    private val password: String,
    @Value("\${spring.data.redis.sentinel.master}")
    private val master: String,
    @Value("\${spring.data.redis.sentinel.nodes}")
    private val nodes: List<String>,
    @Value("\${spring.data.redis.timeout}")
    private val timeout: Duration,
    @Value("\${spring.data.redis.connect-timeout}")
    private val connectTimeout: Duration
) {

    @Bean
    fun redissonClient(): RedissonClient {
        return Redisson.create(redissonClientConfig())
    }

    private fun redissonClientConfig(): Config {
        val config = when (environment) {
            "dev", "qa" -> this::useSingleServerConfig
            "prod" -> this::useSentinelServersConfig
            "test" -> this::useSingleServerTestConfig
            else -> throw InternalServiceException(ErrorCode.UNKNOWN_ENVIRONMENT)
        }
        return Config().apply(config)
    }

    private fun useSingleServerConfig(config: Config) {
        config
            .useSingleServer()
            .setAddress("redis://$host:$port")
            .setPassword(password)
            .setTimeout(timeout.toMillis().toInt())
            .setConnectTimeout(connectTimeout.toMillis().toInt())
            .setConnectionMinimumIdleSize(0)
            .setConnectionPoolSize(30)
    }

    private fun useSentinelServersConfig(config: Config) {
        config
            .useSentinelServers()
            .addSentinelAddress(*nodes.toRedisSentinelAddresses())
            .setMasterName(master)
            .setPassword(password)
            .setTimeout(timeout.toMillis().toInt())
            .setConnectTimeout(connectTimeout.toMillis().toInt())
            .setMasterConnectionMinimumIdleSize(0)
            .setMasterConnectionPoolSize(30)
    }

    private fun useSingleServerTestConfig(config: Config) {
        config
            .useSingleServer()
            .setAddress("redis://$host:$port")
            .setTimeout(timeout.toMillis().toInt())
            .setConnectTimeout(connectTimeout.toMillis().toInt())
            .setConnectionMinimumIdleSize(0)
            .setConnectionPoolSize(30)
    }

    private fun List<String>.toRedisSentinelAddresses(): Array<String> {
        return this.map { "redis://$it" }.toTypedArray()
    }

}