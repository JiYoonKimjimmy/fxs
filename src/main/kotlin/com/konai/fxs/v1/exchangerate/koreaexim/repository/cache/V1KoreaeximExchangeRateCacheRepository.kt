package com.konai.fxs.v1.exchangerate.koreaexim.repository.cache

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate

interface V1KoreaeximExchangeRateCacheRepository {

    fun saveKoreaeximExchangeRateCache(exchangeRate: V1KoreaeximExchangeRate): V1KoreaeximExchangeRate

    fun saveAllKoreaeximExchangeRateCache(exchangeRates: List<V1KoreaeximExchangeRate>)

    fun findKoreaeximExchangeRateCache(currency: String): V1KoreaeximExchangeRate?

    fun clearKoreaeximExchangeRateCache(currency: String)

}