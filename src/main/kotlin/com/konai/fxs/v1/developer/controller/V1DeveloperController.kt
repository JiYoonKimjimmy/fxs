package com.konai.fxs.v1.developer.controller

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.v1.developer.service.V1DeveloperService
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RequestMapping("/api/v1/developer")
@RestController
class V1DeveloperController(
    private val v1DeveloperService: V1DeveloperService
) {

    @GetMapping("/exchange-rate/koreaexim")
    fun findAllKoreaeximExchangeRate(
        @RequestParam(required = false) searchDate: String = LocalDate.now().convertPatternOf()
    ): List<V1KoreaeximExchangeRate> {
        return v1DeveloperService.findAllKoreaeximExchangeRate(searchDate)
    }

    @PostMapping("/exchange-rate/ready/collector/timer")
    fun readyCollectorTimer(
        @RequestParam(required = false) date: String = LocalDate.now().convertPatternOf(),
        @RequestParam(required = false) size: Int = 1,
        @RequestParam(required = false) ttl: Int = 10000
    ) {
        v1DeveloperService.readyCollectorTimer(date, size, ttl)
    }

}