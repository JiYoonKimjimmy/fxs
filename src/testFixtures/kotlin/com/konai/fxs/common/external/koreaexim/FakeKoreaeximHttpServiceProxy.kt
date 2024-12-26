package com.konai.fxs.common.external.koreaexim

class FakeKoreaeximHttpServiceProxy : KoreaeximHttpServiceProxy {

    private val koreaeximExchangeRateResponseFixture = KoreaeximExchangeRateResponseFixture()

    override fun getExchangeRate(apiKey: String, apiType: String, searchDate: String): List<KoreaeximExchangeRateResponse> {
        return listOf(koreaeximExchangeRateResponseFixture.make())
    }

}