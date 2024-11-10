package com.konai.fxs.testsupport.redis

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

class EmbeddedRedisTestListener : TestListener {

    override suspend fun beforeSpec(spec: Spec) {
        EmbeddedRedis.embeddedRedisStart()
    }

}