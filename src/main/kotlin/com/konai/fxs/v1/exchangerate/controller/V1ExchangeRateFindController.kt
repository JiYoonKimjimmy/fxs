package com.konai.fxs.v1.exchangerate.controller

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.scheduler.ExchangeRateCollectTimerScheduler
import com.konai.fxs.v1.exchangerate.controller.model.V1FindLatestKoreaeximExchangeRateResponse
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateFindService
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRateMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RequestMapping("/api/v1/exchange-rate")
@RestController
class V1ExchangeRateFindController(
    private val v1KoreaeximExchangeRateMapper: V1KoreaeximExchangeRateMapper,
    private val exchangeRateCollectTimerScheduler: ExchangeRateCollectTimerScheduler,
    private val v1KoreaeximExchangeRateFindService: V1KoreaeximExchangeRateFindService,
) {

    @GetMapping("/koreaexim")
    fun findKoreaeximExchangeRate(
        @RequestParam(required = false) searchDate: String = LocalDate.now().convertPatternOf()
    ): List<V1KoreaeximExchangeRate> {
        return v1KoreaeximExchangeRateFindService.findAllExchangeRate(searchDate)
    }

    @GetMapping("/koreaexim/{currency}")
    fun findKoreaeximExchangeRate(
        @PathVariable currency: String,
        @RequestParam(required = false) requestDate: String = LocalDate.now().convertPatternOf(),
    ): ResponseEntity<V1FindLatestKoreaeximExchangeRateResponse> {
        return v1KoreaeximExchangeRateFindService.findLatestExchangeRate(currency, requestDate)
            .let { v1KoreaeximExchangeRateMapper.domainToModel(it) }
            .let { V1FindLatestKoreaeximExchangeRateResponse(it) }
            .success(HttpStatus.OK)
    }

    @PostMapping("/set-timer")
    fun setTimer() {
        exchangeRateCollectTimerScheduler.readyKoreaeximExchangeRateCollectorTimer()
    }

}