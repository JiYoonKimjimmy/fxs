package com.konai.fxs.v1.exchangerate.koreaexim.repository.entity

import com.konai.fxs.testsupport.TestExtensionFunctions.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder

class V1KoreaeximExchangeRateEntityFixture {

    fun make(
        index: Int = 1,
        registerDate: String = "20241217",
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
    ): V1KoreaeximExchangeRateEntity {
        return fixtureMonkey.giveMeKotlinBuilder<V1KoreaeximExchangeRateEntity>()
            .setExp(V1KoreaeximExchangeRateEntity::index, index)
            .setExp(V1KoreaeximExchangeRateEntity::registerDate, registerDate)
            .setExp(V1KoreaeximExchangeRateEntity::result, result)
            .setExp(V1KoreaeximExchangeRateEntity::curUnit, curUnit)
            .setExp(V1KoreaeximExchangeRateEntity::curNm, curNm)
            .setExp(V1KoreaeximExchangeRateEntity::ttb, ttb)
            .setExp(V1KoreaeximExchangeRateEntity::tts, tts)
            .setExp(V1KoreaeximExchangeRateEntity::dealBasR, dealBasR)
            .setExp(V1KoreaeximExchangeRateEntity::bkpr, bkpr)
            .setExp(V1KoreaeximExchangeRateEntity::yyEfeeR, yyEfeeR)
            .setExp(V1KoreaeximExchangeRateEntity::tenDdEfeeR, tenDdEfeeR)
            .setExp(V1KoreaeximExchangeRateEntity::kftcDealBasR, kftcDealBasR)
            .setExp(V1KoreaeximExchangeRateEntity::kftcBkpr, kftcBkpr)
            .sample()
    }

}