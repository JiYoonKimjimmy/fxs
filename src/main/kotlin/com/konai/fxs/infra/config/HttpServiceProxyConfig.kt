package com.konai.fxs.infra.config

import com.konai.fxs.common.enumerate.ExternalComponent
import com.konai.fxs.common.external.knotify.KnotifyHttpServiceProxy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpServiceProxyConfig(
    private val restClientConfig: RestClientConfig
) {

    @Bean
    fun knotifyHttpServiceProxy(): KnotifyHttpServiceProxy {
        return restClientConfig.restClientRepositoryProxy(ExternalComponent.KNOTIFY, KnotifyHttpServiceProxy::class.java)
    }

}