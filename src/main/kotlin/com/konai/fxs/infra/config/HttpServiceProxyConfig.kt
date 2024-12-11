package com.konai.fxs.infra.config

import com.konai.fxs.common.external.knotify.KnotifyHttpServiceProxy
import com.konai.fxs.common.enumerate.ExternalComponent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpServiceProxyConfig(
    private val restClientConfig: RestClientConfig
) {

    @Bean
    fun knotifyHttpServiceProxy(): KnotifyHttpServiceProxy {
        return restClientConfig.restClientHttpServiceProxy(ExternalComponent.KNOTIFY, KnotifyHttpServiceProxy::class.java)
    }

}