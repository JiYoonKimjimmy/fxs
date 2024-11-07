package com.konai.fxs.testsupport

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

class RedisTestListener : TestListener {

    companion object {
        private const val REDIS_HOST = "localhost"
        private const val REDIS_PORT = 6381
    }

    override suspend fun beforeSpec(spec: Spec) {
        TestRedisConfig(REDIS_HOST, REDIS_PORT.toString()).setup()
    }

    override suspend fun afterSpec(spec: Spec) {
        TestRedisConfig(REDIS_HOST, REDIS_PORT.toString()).shutdown()
    }

}