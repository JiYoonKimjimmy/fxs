package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate

interface V1KoreaeximExchangeRateCollectService {

    fun collect(index: Int, searchDate: String): List<V1KoreaeximExchangeRate>

}