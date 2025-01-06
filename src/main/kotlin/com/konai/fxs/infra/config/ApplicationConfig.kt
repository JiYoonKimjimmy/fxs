package com.konai.fxs.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.konasl.commonlib.logging.filter.EnableLoggingFilter
import com.konasl.commonlib.logging.helper.masking.MaskingProperties
import com.konasl.commonlib.springweb.correlation.EnableCorrelationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableAsync
@EnableScheduling
@EnableLoggingFilter
@EnableCorrelationFilter
@EnableRedisRepositories
@EnableConfigurationProperties(MaskingProperties::class)
@Configuration
class ApplicationConfig {

    @Primary
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerModule(kotlinModule())
    }

}