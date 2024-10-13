package com.konai.fxs.testsupport

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import org.springframework.context.annotation.Configuration

@Configuration
class KotestConfig : AbstractProjectConfig() {

    override fun extensions(): List<Extension> = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

}