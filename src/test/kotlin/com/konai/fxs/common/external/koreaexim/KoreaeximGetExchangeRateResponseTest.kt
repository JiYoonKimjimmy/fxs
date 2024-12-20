package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestCommonFunctions
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class KoreaeximGetExchangeRateResponseTest : CustomStringSpec({

    val objectMapper = TestCommonFunctions.objectMapper

    "한국수출입은행 환율 조회 정보 JSON 직렬화 정상 확인한다" {
        // given
        val data = V1KoreaeximExchangeRate(
            registerDate = "20241217",
            result = 1,
            curUnit = "USD",
            ttb = "1,357.38",
            tts = "1,384.81",
            dealBasR = "1,371.1",
            bkpr = "1,371",
            yyEfeeR = "0",
            tenDdEfeeR = "0",
            kftcBkpr = "1,371",
            kftcDealBasR = "1,371.1",
            curNm = "미국 달러"
        )

        // when
        val result = objectMapper.writeValueAsString(data)

        // then
        result shouldNotBe null
        shouldNotThrowAny { objectMapper.readValue(result, V1KoreaeximExchangeRate::class.java) }
    }

    "한국수출입은행 환율 조회 정보 JSON 역직렬화 정상 확인한다" {
        // given
        val json = """
            {
                "result": 1,
                "cur_unit": "USD",
                "ttb": "1,357.38",
                "tts": "1,384.81",
                "deal_bas_r": "1,371.1",
                "bkpr": "1,371",
                "yy_efee_r": "0",
                "ten_dd_efee_r": "0",
                "kftc_bkpr": "1,371",
                "kftc_deal_bas_r": "1,371.1",
                "cur_nm": "미국 달러"
            }
        """.trimIndent()

        // when
        val result = objectMapper.readValue(json, KoreaeximGetExchangeRateResponse::class.java)

        // then
        result shouldNotBe null
        result.result shouldBe 1
        result.curUnit shouldBe "USD"
        result.ttb shouldBe "1,357.38"
        result.tts shouldBe "1,384.81"
        result.dealBasR shouldBe "1,371.1"
        result.bkpr shouldBe "1,371"
        result.yyEfeeR shouldBe "0"
        result.tenDdEfeeR shouldBe "0"
        result.kftcBkpr shouldBe "1,371"
        result.kftcDealBasR shouldBe "1,371.1"
        result.curNm shouldBe "미국 달러"
    }

})