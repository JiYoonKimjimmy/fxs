package com.konai.fxs.v1.developer.service

import com.konai.fxs.scheduler.ExchangeRateCollectTimerScheduler
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateFindService
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.springframework.stereotype.Service

@Service
class V1DeveloperServiceImpl(
    private val v1KoreaeximExchangeRateFindService: V1KoreaeximExchangeRateFindService,
    private val exchangeRateCollectTimerScheduler: ExchangeRateCollectTimerScheduler
) : V1DeveloperService {

    override fun findAllKoreaeximExchangeRate(searchDate: String): List<V1KoreaeximExchangeRate> {
        return v1KoreaeximExchangeRateFindService.findAllExchangeRate(searchDate)
    }

    override fun readyCollectorTimer() {
        exchangeRateCollectTimerScheduler.readyKoreaeximExchangeRateCollectorTimer()
    }

}