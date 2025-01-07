package com.konai.fxs.v1.exchangerate.controller

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.ResultStatus
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.getProc
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.exchangerate.koreaexim.repository.V1KoreaeximExchangeRateRepository
import org.hamcrest.Matchers.equalTo
import org.springframework.test.web.servlet.MockMvc

@CustomSpringBootTest
class V1ExchangeRateFindControllerTest(
    private val mockMvc: MockMvc,
    private val v1KoreaeximExchangeRateRepository: V1KoreaeximExchangeRateRepository
) : CustomBehaviorSpec({

    val v1KoreaeximExchangeRateFixture = dependencies.v1KoreaeximExchangeRateFixture

    given("한국수출입은행 환율 정보 조회 API 요청하여") {
        val currency = Currency.USD
        val url = "/api/v1/exchange-rate/koreaexim/$currency"

        `when`("저장 정보 없는 경우") {
            val result = mockMvc.getProc(url)

            then("'404 Not Found - 210_3001_013' 에러 응답 정상 확인한다") {
                result.andExpect {
                    status { isNotFound() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.FAILED.name))
                        jsonPath("result.code", equalTo("210_3001_013"))
                        jsonPath("result.message", equalTo("Exchange rate find service failed. Exchange rate not found."))
                    }
                }
            }
        }

        // 한국수출입은행 환율 정보 DB 저장
        val saved = v1KoreaeximExchangeRateFixture.make(curUnit = currency)
        v1KoreaeximExchangeRateRepository.saveAll(listOf(saved))

        `when`("DB 저장 정보 있는 경우") {
            val result = mockMvc.getProc(url)

            then("'200 Ok' 성공 응답 정상 확인한다") {
                result.andExpect {
                    status { isOk() }
                    content {
                        jsonPath("result.status", equalTo(ResultStatus.SUCCESS.name))
                        jsonPath("data.currency", equalTo(currency))
                        jsonPath("data.currencyName", equalTo("미국 달러"))
                        jsonPath("data.ttBuyRate", equalTo(1420.55))
                        jsonPath("data.ttSellRate", equalTo(1449.24))
                        jsonPath("data.dealCriteriaRate", equalTo(1434.9))
                    }
                }
            }
        }

    }

})