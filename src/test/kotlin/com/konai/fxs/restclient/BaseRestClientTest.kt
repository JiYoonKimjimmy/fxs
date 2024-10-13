package com.konai.fxs.common.restclient

import com.konai.fxs.infra.config.ExternalUrlProperties
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BaseRestClientTest : StringSpec({

    val externalUrlProperties = ExternalUrlProperties()

    fun generateBaseUrl(componentName: ComponentName): String {
        return externalUrlProperties.getProperty(componentName).url
    }

    "KNOTIFY client base url 정보 생성 성공한다" {
        // given
        val componentName = ComponentName.KNOTIFY

        // when
        val result = generateBaseUrl(componentName)

        // then
        result shouldBe "http://118.33.122.28:15519"
    }

})