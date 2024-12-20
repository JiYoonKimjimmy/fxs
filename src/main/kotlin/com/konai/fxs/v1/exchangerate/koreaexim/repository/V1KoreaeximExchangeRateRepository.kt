package com.konai.fxs.v1.exchangerate.koreaexim.repository

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate

interface V1KoreaeximExchangeRateRepository {

    fun saveAll(exchangeRates: List<V1KoreaeximExchangeRate>): List<V1KoreaeximExchangeRate>

    fun findByPredicate(predicate: V1KoreaeximExchangeRatePredicate): V1KoreaeximExchangeRate?

}