package com.konai.fxs.v1.developer.service

import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate

interface V1DeveloperService {

    fun readyKoreaeximCollectorTimer(date: String, size: Int, ttl: Int)

    fun collectKoreaeximExchangeRate(index: Int, searchDate: String): List<V1KoreaeximExchangeRate>

    fun findAllKoreaeximExchangeRate(searchDate: String): List<V1KoreaeximExchangeRate>
    
}