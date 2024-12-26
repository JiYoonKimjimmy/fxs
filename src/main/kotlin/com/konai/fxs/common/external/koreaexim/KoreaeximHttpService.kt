package com.konai.fxs.common.external.koreaexim

interface KoreaeximHttpService {

    fun getExchangeRates(searchDate: String): KoreaeximExchangeRateResult

}