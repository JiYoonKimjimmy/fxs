package com.konai.fxs.scheduler

import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQTestListener
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.delay

class ExchangeRateCollectTimerSchedulerTest : CustomBehaviorSpec({

    listeners(
        EmbeddedRedisTestListener(),
        MockRabbitMQTestListener(V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE, V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_EXCHANGE)
    )

    val exchangeRateCollectTimerScheduler = dependencies.exchangeRateCollectTimerScheduler

    val rabbitTemplate = dependencies.rabbitTemplate
    val koreaeximCollectorSize = dependencies.applicationProperties.koreaeximCollectorSize
    val koreaeximCollectorTTL = dependencies.applicationProperties.koreaeximCollectorTTL

    given("한국수출입은행 환율 정보 수집 Timer 실행되어") {

        `when`("Timer 메시지 발행 성공인 경우") {
            exchangeRateCollectTimerScheduler.readyKoreaeximExchangeRateCollectorTimer()

            then("메지시 발행 건수 정상 확인한다") {
                repeat(koreaeximCollectorSize) {
                    delay(koreaeximCollectorTTL.toLong())
                    rabbitTemplate.receiveAndConvert(V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_EXCHANGE.queueName) shouldNotBe null
                }
            }
        }
    }

})