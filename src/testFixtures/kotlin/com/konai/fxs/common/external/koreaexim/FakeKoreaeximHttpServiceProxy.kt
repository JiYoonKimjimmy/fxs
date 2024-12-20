package com.konai.fxs.common.external.koreaexim

class FakeKoreaeximHttpServiceProxy : KoreaeximHttpServiceProxy {

    private val koreaeximGetExchangeRateResponseFixture = KoreaeximGetExchangeRateResponseFixture()

    override fun getExchangeRate(apiKey: String, apiType: String, searchDate: String): List<KoreaeximGetExchangeRateResponse> {
        return listOf(koreaeximGetExchangeRateResponseFixture.make())
    }

}