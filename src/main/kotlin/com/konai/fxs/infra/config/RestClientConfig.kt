package com.konai.fxs.infra.config

import com.konasl.commonlib.springweb.correlation.core.RequestContext
import com.konasl.commonlib.springweb.correlation.headerpropagator.CorrelationHeaderField.*
import com.konasl.commonlib.springweb.correlation.loggercontext.CorrelationLoggingField.*
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig : RestClientAutoConfiguration() {

    @Bean
    fun restClient(): RestClient {
        return RestClient
            .builder()
            .requestInitializer {
                it.headers[ASP_ID_HEADER_FIELD.getName()]         = MDC.get(ASP_ID_LOG_FIELD.getName()) ?: ASP_ID_LOG_FIELD.getName()
                it.headers[MPA_ID_HEADER_FIELD.getName()]         = MDC.get(MPA_ID_LOG_FIELD.getName()) ?: MPA_ID_LOG_FIELD.getName()
                it.headers[USER_ID_HEADER_FIELD.getName()]        = MDC.get(USER_ID_LOG_FIELD.getName()) ?: USER_ID_LOG_FIELD.getName()
                it.headers[CORRELATION_ID_HEADER_FIELD.getName()] = MDC.get(CORRELATION_ID_LOG_FIELD.getName()) ?: RequestContext.generateId()
            }
            .build()
    }


}