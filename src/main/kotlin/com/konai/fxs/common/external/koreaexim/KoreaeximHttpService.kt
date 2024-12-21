package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate

interface KoreaeximHttpService {

    fun getExchangeRates(searchDate: String): List<V1KoreaeximExchangeRate>

}