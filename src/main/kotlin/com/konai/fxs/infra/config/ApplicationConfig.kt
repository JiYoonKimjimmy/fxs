package com.konai.fxs.infra.config

import com.konasl.commonlib.logging.filter.EnableLoggingFilter
import com.konasl.commonlib.logging.helper.masking.MaskingProperties
import com.konasl.commonlib.springweb.correlation.EnableCorrelationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableAsync

@EnableRetry
@EnableAsync
@EnableLoggingFilter
@EnableCorrelationFilter
@EnableRedisRepositories
@EnableConfigurationProperties(MaskingProperties::class)
@Configuration
class ApplicationConfig