package com.konai.fxs.infra.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

//@EnableLoggingFilter
//@EnableCorrelationFilter
//@EnableConfigurationProperties(MaskingProperties::class)
@EnableTransactionManagement
@Configuration
class ApplicationConfig