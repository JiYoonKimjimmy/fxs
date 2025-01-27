package com.konai.fxs.infra.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.ExternalComponent
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "external-url")
@Configuration
class ExternalUrlProperties {

    lateinit var properties: Map<String, ExternalUrlProperty>

    fun getProperty(component: ExternalComponent): ExternalUrlProperty {
        return this.properties[component.getPropertyName()] ?: throw InternalServiceException(ErrorCode.EXTERNAL_URL_PROPERTY_NOT_DEFINED)
    }

    data class ExternalUrlProperty(
        private val host: String,
        private val port: String = EMPTY,
        @field:JsonProperty("context-path")
        private val contextPath: String = EMPTY
    ) {
        val url: String by lazy {
            val portStr = port.takeIf { it.isNotEmpty() }?.let { ":$it" } ?: EMPTY
            val contextPathStr = contextPath.takeIf { !it.startsWith("/") && it.isNotEmpty() }?.let { "/$it" } ?: contextPath
            "$host$portStr$contextPathStr"
        }
    }

}

