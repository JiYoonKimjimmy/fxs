package com.konai.fxs.infra.config

import com.konai.fxs.infra.config.ExternalUrlProperties.ExternalUrlProperty
import com.konai.fxs.testsupport.CustomStringSpec
import io.kotest.matchers.shouldBe

class ExternalUrlPropertiesTest : CustomStringSpec({

    "Knotify properties 설정 기준 URL 생성 정상 확인한다" {
        // given
        val property = ExternalUrlProperty(host = "http://knotify.konadc.com", port = "15519")

        // when
        val result = property.url

        // then
        result shouldBe "http://knotify.konadc.com:15519"
    }

    "한국수출입은행 properties 설정 기준 URL 생성 정상 확인한다" {
        // given
        val property = ExternalUrlProperty(host = "https://www.koreaexim.go.kr")

        // when
        val result = property.url

        // then
        result shouldBe "https://www.koreaexim.go.kr"
    }

})