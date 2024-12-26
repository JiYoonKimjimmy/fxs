package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.common.Currency
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class V1KoreaeximExchangeRateFindServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1KoreaeximExchangeRateFindService = dependencies.v1KoreaeximExchangeRateFindService

    val v1KoreaeximExchangeRateRepository = dependencies.fakeV1KoreaeximExchangeRateRepository
    val v1KoreaeximExchangeRateCacheRepository = dependencies.v1KoreaeximExchangeRateCacheRepository

    val v1KoreaeximExchangeRateFixture = dependencies.v1KoreaeximExchangeRateFixture

    val currency = Currency.EUR

    beforeSpec {
        v1KoreaeximExchangeRateCacheRepository.clearKoreaeximExchangeRateCache(currency)
    }

    given("한국수출입은행 환율 정보 조회 요청되어") {
        val requestDate = "20241216"

        `when`("요청 정보 기준 조회 결과 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1KoreaeximExchangeRateFindService.findOne(currency, requestDate) }

            then("'EXCHANGE_RATE_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.EXCHANGE_RATE_NOT_FOUND
            }
        }

        // koreaexim 환율 정보 DB 저장
        val saved = v1KoreaeximExchangeRateFixture.make(registerDate = requestDate, curUnit = currency, ttb = "1000")
        v1KoreaeximExchangeRateRepository.saveAll(listOf(saved))

        `when`("요청 'currency' 기준 환율 Cache 정보 없는 경우") {
            val result = v1KoreaeximExchangeRateFindService.findOne(currency, requestDate)

            then("DB 조회 결과 정상 확인한다") {
                result shouldNotBe null
                result.curUnit shouldBe currency
                result.registerDate shouldBe requestDate
                result.ttb shouldBe "1000"
            }
        }

        // koreaexim 환율 정보 Cache 저장
        val cache = v1KoreaeximExchangeRateFixture.make(registerDate = requestDate, curUnit = currency, ttb = "900")
        v1KoreaeximExchangeRateCacheRepository.saveAllKoreaeximExchangeRateCache(listOf(cache))

        `when`("요청 'currency' 기준 환율 Cache 정보 있는 경우") {
            val result = v1KoreaeximExchangeRateFindService.findOne(currency, requestDate)

            then("Cache 조회 결과 정상 확인한다") {
                result shouldNotBe null
                result.curUnit shouldBe currency
                result.registerDate shouldBe requestDate
                result.ttb shouldBe "900"
            }
        }
    }

})