package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate

interface V1KoreaeximExchangeRateFindService {

    fun findOne(currency: String, requestDate: String): V1KoreaeximExchangeRate

}