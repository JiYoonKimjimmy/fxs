package com.konai.fxs.testsupport.annotation

import com.konai.fxs.infra.config.ExternalUrlProperties
import com.konai.fxs.infra.config.HttpServiceProxyConfig
import com.konai.fxs.infra.config.RestClientConfig
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import

@Import(value = [RestClientConfig::class, HttpServiceProxyConfig::class, ExternalUrlProperties::class])
@RestClientTest
annotation class CustomRestClientTest