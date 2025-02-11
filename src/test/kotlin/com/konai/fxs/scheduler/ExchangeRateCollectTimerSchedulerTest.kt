package com.konai.fxs.scheduler

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.v1.exchangerate.koreaexim.service.V1KoreaeximExchangeRateCollectService
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class ExchangeRateCollectTimerSchedulerTest : CustomBehaviorSpec({
    
    val v1KoreaeximExchangeRateCollectService = mockk<V1KoreaeximExchangeRateCollectService>(relaxed = true)
    val applicationProperties = dependencies.applicationProperties
    val exchangeRateCollectTimerScheduler = ExchangeRateCollectTimerScheduler(v1KoreaeximExchangeRateCollectService, applicationProperties)

    given("한국수출입은행 환율 정보 수집 Timer 실행되어") {
        val date = LocalDate.now().convertPatternOf()
        val size = applicationProperties.koreaeximCollectorSize
        val ttl = applicationProperties.koreaeximCollectorTTL

        `when`("Timer 메시지 발행 성공인 경우") {
            exchangeRateCollectTimerScheduler.readyKoreaeximExchangeRateCollectorTimer()

            then("메지시 발행 건수 정상 확인한다") {
                verify { v1KoreaeximExchangeRateCollectService.ready(date, size, ttl) }
            }
        }
    }

})