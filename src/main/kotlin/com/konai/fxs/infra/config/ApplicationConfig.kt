package com.konai.fxs.infra.config

import com.konasl.commonlib.logging.filter.EnableLoggingFilter
import com.konasl.commonlib.logging.helper.masking.MaskingProperties
import com.konasl.commonlib.springweb.correlation.EnableCorrelationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableLoggingFilter
@EnableCorrelationFilter
@EnableConfigurationProperties(MaskingProperties::class)
@EnableTransactionManagement
@Configuration
class ApplicationConfig