package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomRestClientTest
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateMapper
import io.kotest.matchers.shouldBe
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@AutoConfigureWireMock(port = 8888, stubs = ["classpath:/mappings/koreaexim"])
@CustomRestClientTest
class KoreaeximHttpServiceImplTest(
    private val koreaeximHttpServiceProxy: KoreaeximHttpServiceProxy,
    private val applicationProperties: ApplicationProperties,
) : CustomStringSpec({

    val v1KoreaeximExchangeRateMapper = V1KoreaeximExchangeRateMapper()
    val koreaeximHttpServiceImpl = KoreaeximHttpServiceImpl(v1KoreaeximExchangeRateMapper, koreaeximHttpServiceProxy, applicationProperties)

    "한국수출입은행 환율 정보 조회 API 요청하여 응답 정상 확인한다" {
        // given
        val date = "20241217"

        // when
        val result = koreaeximHttpServiceImpl.getExchangeRate(date).first()

        // then
        result.curUnit shouldBe "USD"
        result.ttb shouldBe "1,420.55"
        result.tts shouldBe "1,449.24"
        result.dealBasR shouldBe "1,434.9"
        result.bkpr shouldBe "1,434"
        result.yyEfeeR shouldBe "0"
        result.tenDdEfeeR shouldBe "0"
        result.kftcBkpr shouldBe "1,434"
        result.kftcDealBasR shouldBe "1,434.9"
        result.curNm shouldBe "미국 달러"
    }

})