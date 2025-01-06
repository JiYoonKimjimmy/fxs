package com.konai.fxs.scheduler

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateCollectService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ExchangeRateCollectTimerScheduler(
    private val v1KoreaeximExchangeRateCollectService: V1KoreaeximExchangeRateCollectService,
    private val applicationProperties: ApplicationProperties
) {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 59 11 * * ?")
    fun readyKoreaeximExchangeRateCollectorTimer() {
        logger.info("!! Run 'readyKoreaeximExchangeRateCollectorTimer' Scheduler !!")
        val date = LocalDate.now().convertPatternOf()
        val size = applicationProperties.koreaeximCollectorSize
        v1KoreaeximExchangeRateCollectService.ready(date, size)
    }

}