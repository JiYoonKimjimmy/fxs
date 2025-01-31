package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "external-url.properties.koreaexim.host=https://www.koreaexim.go.kr",
    "external-url.properties.koreaexim.port=443"
])
@CustomSpringBootTest
class KoreaeximHttpServiceImplBootTest(
    private val koreaeximHttpService: KoreaeximHttpService
) : CustomStringSpec({

    "한국수출입은행 API 요청하여 정상 응답 확인한다" {
        val searchDate = "20241217"

        val result = koreaeximHttpService.getExchangeRates(searchDate)

        result.content.shouldNotBeEmpty()
    }

})