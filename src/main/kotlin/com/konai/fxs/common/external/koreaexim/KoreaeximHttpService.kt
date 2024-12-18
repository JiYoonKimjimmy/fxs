package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.KoreaeximExchangeRate

interface KoreaeximHttpService {

    fun getExchangeRate(searchDate: String): List<KoreaeximExchangeRate>

}