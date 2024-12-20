package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class KoreaeximGetExchangeRateResponseFixture {

    fun make(
        result: Int = 1,
        curUnit: String = "USD",
        curNm: String = "미국 달러",
        ttb: String = "1,420.55",
        tts: String = "1,449.24",
        dealBasR: String = "1,434.9",
        bkpr: String = "1,434",
        yyEfeeR: String = "0",
        tenDdEfeeR: String = "0",
        kftcDealBasR: String = "1,434.9",
        kftcBkpr: String = "1,434",
    ): KoreaeximGetExchangeRateResponse {
        return fixtureMonkey.giveMeKotlinBuilder<KoreaeximGetExchangeRateResponse>()
            .setExp(KoreaeximGetExchangeRateResponse::result, result)
            .setExp(KoreaeximGetExchangeRateResponse::curUnit, curUnit)
            .setExp(KoreaeximGetExchangeRateResponse::curNm, curNm)
            .setExp(KoreaeximGetExchangeRateResponse::ttb, ttb)
            .setExp(KoreaeximGetExchangeRateResponse::tts, tts)
            .setExp(KoreaeximGetExchangeRateResponse::dealBasR, dealBasR)
            .setExp(KoreaeximGetExchangeRateResponse::bkpr, bkpr)
            .setExp(KoreaeximGetExchangeRateResponse::yyEfeeR, yyEfeeR)
            .setExp(KoreaeximGetExchangeRateResponse::tenDdEfeeR, tenDdEfeeR)
            .setExp(KoreaeximGetExchangeRateResponse::kftcDealBasR, kftcDealBasR)
            .setExp(KoreaeximGetExchangeRateResponse::kftcBkpr, kftcBkpr)
            .sample()
    }

}