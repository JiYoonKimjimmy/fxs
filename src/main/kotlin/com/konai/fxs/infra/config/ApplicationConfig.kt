package com.konai.fxs.infra.config

import com.konasl.commonlib.logging.filter.EnableLoggingFilter
import com.konasl.commonlib.logging.helper.masking.MaskingProperties
import com.konasl.commonlib.springweb.correlation.EnableCorrelationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableLoggingFilter
@EnableCorrelationFilter
@EnableConfigurationProperties(MaskingProperties::class)
@Configuration
class ApplicationConfig