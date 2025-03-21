package com.konai.fxs.testsupport

import com.konai.fxs.testsupport.redis.EmbeddedRedis
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestProjectConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    override suspend fun afterProject() {
        EmbeddedRedis.lettuceConnectionFactory.resetConnection()
    }

}