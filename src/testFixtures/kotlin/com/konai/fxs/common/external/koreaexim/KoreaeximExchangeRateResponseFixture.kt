package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class KoreaeximExchangeRateResponseFixture {

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
    ): KoreaeximExchangeRateResponse {
        return fixtureMonkey.giveMeKotlinBuilder<KoreaeximExchangeRateResponse>()
            .setExp(KoreaeximExchangeRateResponse::result, result)
            .setExp(KoreaeximExchangeRateResponse::curUnit, curUnit)
            .setExp(KoreaeximExchangeRateResponse::curNm, curNm)
            .setExp(KoreaeximExchangeRateResponse::ttb, ttb)
            .setExp(KoreaeximExchangeRateResponse::tts, tts)
            .setExp(KoreaeximExchangeRateResponse::dealBasR, dealBasR)
            .setExp(KoreaeximExchangeRateResponse::bkpr, bkpr)
            .setExp(KoreaeximExchangeRateResponse::yyEfeeR, yyEfeeR)
            .setExp(KoreaeximExchangeRateResponse::tenDdEfeeR, tenDdEfeeR)
            .setExp(KoreaeximExchangeRateResponse::kftcDealBasR, kftcDealBasR)
            .setExp(KoreaeximExchangeRateResponse::kftcBkpr, kftcBkpr)
            .sample()
    }

}