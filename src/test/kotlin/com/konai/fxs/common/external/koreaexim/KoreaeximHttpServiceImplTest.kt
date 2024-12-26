package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.common.NONE
import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomRestClientTest
import io.kotest.matchers.shouldBe
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@AutoConfigureWireMock(port = 8888, stubs = ["classpath:/mappings/koreaexim"])
@CustomRestClientTest
class KoreaeximHttpServiceImplTest(
    private val koreaeximHttpServiceProxy: KoreaeximHttpServiceProxy,
    private val applicationProperties: ApplicationProperties,
) : CustomStringSpec({

    "한국수출입은행 환율 정보 조회 API 요청하여 'result: 1' 성공 응답 정상 확인한다" {
        // given
        val date = "20241217"
        val service = KoreaeximHttpServiceImpl(koreaeximHttpServiceProxy, applicationProperties)

        // when
        val result = service.getExchangeRates(date)

        // then
        result.isSuccess shouldBe true

        val content = result.content.first()
        content.result shouldBe 1
        content.curUnit shouldBe "USD"
        content.ttb shouldBe "1,420.55"
        content.tts shouldBe "1,449.24"
        content.dealBasR shouldBe "1,434.9"
        content.bkpr shouldBe "1,434"
        content.yyEfeeR shouldBe "0"
        content.tenDdEfeeR shouldBe "0"
        content.kftcBkpr shouldBe "1,434"
        content.kftcDealBasR shouldBe "1,434.9"
        content.curNm shouldBe "미국 달러"
    }

    "한국수출입은행 환율 정보 조회 API 요청하여 'result: 2' 실패 응답 정상 확인한다" {
        // given
        val date = "20241217"
        val properties = ApplicationProperties(applicationProperties.koreaeximApiKey, "INVALID")
        val service = KoreaeximHttpServiceImpl(koreaeximHttpServiceProxy, properties)

        // when
        val result = service.getExchangeRates(date)

        // then
        result.isSuccess shouldBe false

        val content = result.content.first()
        content.result shouldBe 2
        content.curUnit shouldBe NONE
        content.ttb shouldBe NONE
        content.tts shouldBe NONE
        content.dealBasR shouldBe NONE
        content.bkpr shouldBe NONE
        content.yyEfeeR shouldBe NONE
        content.tenDdEfeeR shouldBe NONE
        content.kftcBkpr shouldBe NONE
        content.kftcDealBasR shouldBe NONE
        content.curNm shouldBe NONE
    }

    "한국수출입은행 환율 정보 조회 API 요청하여 'result: 3' 실패 응답 정상 확인한다" {
        // given
        val date = "20241217"
        val properties = ApplicationProperties("INVALID", applicationProperties.koreaeximApiType)
        val service = KoreaeximHttpServiceImpl(koreaeximHttpServiceProxy, properties)

        // when
        val result = service.getExchangeRates(date)

        // then
        result.isSuccess shouldBe false

        val content = result.content.first()
        content.result shouldBe 3
        content.curUnit shouldBe NONE
        content.ttb shouldBe NONE
        content.tts shouldBe NONE
        content.dealBasR shouldBe NONE
        content.bkpr shouldBe NONE
        content.yyEfeeR shouldBe NONE
        content.tenDdEfeeR shouldBe NONE
        content.kftcBkpr shouldBe NONE
        content.kftcDealBasR shouldBe NONE
        content.curNm shouldBe NONE
    }

    "한국수출입은행 환율 정보 조회 API 요청하여 'result: 4' 실패 응답 정상 확인한다" {
        // given
        val date = "20241217"
        val properties = ApplicationProperties("INVALID", "REQUEST_EXCEEDED")
        val service = KoreaeximHttpServiceImpl(koreaeximHttpServiceProxy, properties)

        // when
        val result = service.getExchangeRates(date)

        // then
        result.isSuccess shouldBe false

        val content = result.content.first()
        content.result shouldBe 4
        content.curUnit shouldBe NONE
        content.ttb shouldBe NONE
        content.tts shouldBe NONE
        content.dealBasR shouldBe NONE
        content.bkpr shouldBe NONE
        content.yyEfeeR shouldBe NONE
        content.tenDdEfeeR shouldBe NONE
        content.kftcBkpr shouldBe NONE
        content.kftcDealBasR shouldBe NONE
        content.curNm shouldBe NONE
    }

})