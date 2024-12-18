package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomRestClientTest
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.Import

@Import(ApplicationProperties::class)
@AutoConfigureWireMock(port = 8888)
@CustomRestClientTest
class KoreaeximHttpServiceImplTest(
    private val koreaeximHttpServiceProxy: KoreaeximHttpServiceProxy,
    private val applicationProperties: ApplicationProperties,
) : CustomStringSpec({

    val koreaeximHttpServiceImpl = KoreaeximHttpServiceImpl(koreaeximHttpServiceProxy, applicationProperties)

    "한국수출입은행 환율 정보 조회 API 요청하여 응답 정상 확인한다" {
        // given
        val date = "20241217"

        // when
        val result = koreaeximHttpServiceImpl.getExchangeRate(date)

        // then
        result.shouldNotBeEmpty()
    }

})