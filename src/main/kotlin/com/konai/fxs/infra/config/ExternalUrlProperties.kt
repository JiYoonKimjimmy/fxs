package com.konai.fxs.infra.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.ExternalComponent
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class ExternalUrlProperties {

    private val properties: Map<String, ExternalUrlProperty> = getExternalUrlProperties()

    private fun getExternalUrlProperties(): Map<String, ExternalUrlProperty> {
        val objectMapper = ObjectMapper(YAMLFactory()).registerModule(kotlinModule())
        val resource = ClassPathResource("application-external-url.yml")
        val externalUrlMap: Map<String, Map<String, ExternalUrlProperty>> = objectMapper.readValue(resource.inputStream)
        return externalUrlMap["external-url"] ?: emptyMap()
    }

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

