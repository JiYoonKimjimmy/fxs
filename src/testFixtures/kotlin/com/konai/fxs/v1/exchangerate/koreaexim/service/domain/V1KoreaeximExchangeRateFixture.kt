package com.konai.fxs.v1.exchangerate.koreaexim.service.domain

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import java.time.LocalDate

class V1KoreaeximExchangeRateFixture {

    fun make(
        registerDate: String = LocalDate.now().convertPatternOf(),
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
    ): V1KoreaeximExchangeRate {
        return fixtureMonkey.giveMeKotlinBuilder<V1KoreaeximExchangeRate>()
            .setExp(V1KoreaeximExchangeRate::registerDate, registerDate)
            .setExp(V1KoreaeximExchangeRate::result, result)
            .setExp(V1KoreaeximExchangeRate::curUnit, curUnit)
            .setExp(V1KoreaeximExchangeRate::curNm, curNm)
            .setExp(V1KoreaeximExchangeRate::ttb, ttb)
            .setExp(V1KoreaeximExchangeRate::tts, tts)
            .setExp(V1KoreaeximExchangeRate::dealBasR, dealBasR)
            .setExp(V1KoreaeximExchangeRate::bkpr, bkpr)
            .setExp(V1KoreaeximExchangeRate::yyEfeeR, yyEfeeR)
            .setExp(V1KoreaeximExchangeRate::tenDdEfeeR, tenDdEfeeR)
            .setExp(V1KoreaeximExchangeRate::kftcDealBasR, kftcDealBasR)
            .setExp(V1KoreaeximExchangeRate::kftcBkpr, kftcBkpr)
            .sample()
    }

}