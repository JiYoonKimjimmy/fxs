package com.konai.fxs.common.external.koreaexim

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

interface KoreaeximHttpServiceProxy {

    @GetExchange("/site/program/financial/exchangeJSON?authkey={apiKey}&data={apiType}&searchdate={searchDate}")
    fun getExchangeRate(
        @PathVariable apiKey: String,
        @PathVariable apiType: String,
        @PathVariable searchDate: String
    ): List<KoreaeximGetExchangeRateResponse>

}