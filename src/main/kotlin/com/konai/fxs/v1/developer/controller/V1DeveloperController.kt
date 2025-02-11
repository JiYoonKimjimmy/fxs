package com.konai.fxs.v1.developer.controller

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.v1.developer.service.V1DeveloperService
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController("/api/developer")
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
    fun readyCollectorTimer() {
        v1DeveloperService.readyCollectorTimer()
    }

}