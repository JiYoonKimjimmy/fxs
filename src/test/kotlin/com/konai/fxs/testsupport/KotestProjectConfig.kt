package com.konai.fxs.testsupport

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class KotestProjectConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))
}