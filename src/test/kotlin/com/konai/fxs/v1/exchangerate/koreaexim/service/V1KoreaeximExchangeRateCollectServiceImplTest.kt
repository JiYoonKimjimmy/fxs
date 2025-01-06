package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.common.util.convertPatternOf
import com.konai.fxs.message.V1ExchangeRateCollectorTimerMessage
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQTestListener
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRatePredicate
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate

class V1KoreaeximExchangeRateCollectServiceImplTest : CustomBehaviorSpec({

    listeners(
        EmbeddedRedisTestListener(),
        MockRabbitMQTestListener(
            V1_EXCHANGE_RATE_COLLECTOR_TIMER_DL_EXCHANGE,
            V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE
        )
    )

    val v1KoreaeximExchangeRateCollectService = dependencies.v1KoreaeximExchangeRateCollectService
    val rabbitTemplate = dependencies.rabbitTemplate

    val fakeV1KoreaeximExchangeRateRepository = dependencies.fakeV1KoreaeximExchangeRateRepository
    val v1KoreaeximExchangeRateCacheRepository = dependencies.v1KoreaeximExchangeRateCacheRepository

    given("한국수출입은행 환율 정보 수집기 Timer 요청되어") {
        val date = LocalDate.now().convertPatternOf()
        val size = 1

        `when`("요청 'size' 만큼 메시지 발생 성공인 경우") {
            v1KoreaeximExchangeRateCollectService.ready(date, size)

            then("처리 결과 정상 확인한다") {
                val result = rabbitTemplate.receiveAndConvert(V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE.queueName) as V1ExchangeRateCollectorTimerMessage
                result shouldNotBe null
                result.index shouldBe 1
                result.date shouldBe date
            }
        }
    }
    
    given("한국수출입은행 환율 정보 조회 요청되어") {
        val index = 1
        val searchDate = "20241217"

        `when`("API 연동 정상 조회 성공인 경우") {
            val result = v1KoreaeximExchangeRateCollectService.collect(index, searchDate)

            then("조회 결과 정상 확인한다") {
                result.shouldNotBeEmpty()
            }

            then("Koreaexim 환율 정보 내역 저장 정상 확인한다") {
                val exchangeRate = result.first()
                val predicate = V1KoreaeximExchangeRatePredicate(registerDate = searchDate, curUnit = exchangeRate.curUnit)
                val saved = fakeV1KoreaeximExchangeRateRepository.findByPredicate(predicate)
                saved!! shouldNotBe null
                saved.curNm shouldBe exchangeRate.curNm
            }

            then("최근 환율 정보 Cache 변경 정상 확인한다") {
                val exchangeRate = result.first()
                val saved = v1KoreaeximExchangeRateCacheRepository.findKoreaeximExchangeRateCache(exchangeRate.curUnit)
                saved!! shouldNotBe null
                saved.curNm shouldBe exchangeRate.curNm
            }
        }
    }
    
})