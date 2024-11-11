package com.konai.fxs.testsupport.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class EmbeddedRedisConfig {

    @PostConstruct
    fun embeddedRedisStart() {
        EmbeddedRedis.embeddedRedisStart()
    }

    @PreDestroy
    fun embeddedRedisStop() {
        EmbeddedRedis.embeddedRedisStop()
    }

}