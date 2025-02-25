package com.konai.fxs.v1.developer.service

import com.konai.fxs.common.external.koreaexim.KoreaeximHttpService
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateCollectService
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.springframework.stereotype.Service

@Service
class V1DeveloperServiceImpl(
    private val koreaeximHttpService: KoreaeximHttpService,
    private val v1KoreaeximExchangeRateCollectService: V1KoreaeximExchangeRateCollectService
) : V1DeveloperService {

    override fun readyKoreaeximCollectorTimer(date: String, size: Int, ttl: Int) {
        v1KoreaeximExchangeRateCollectService.ready(date, size, ttl)
    }

    override fun collectKoreaeximExchangeRate(index: Int, searchDate: String): List<V1KoreaeximExchangeRate> {
        return v1KoreaeximExchangeRateCollectService.collect(index, searchDate)
    }

    override fun findAllKoreaeximExchangeRate(searchDate: String): List<V1KoreaeximExchangeRate> {
        return koreaeximHttpService.getExchangeRates(searchDate).content
    }

}