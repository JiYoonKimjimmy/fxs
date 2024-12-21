package com.konai.fxs.v1.exchangerate.koreaexim.repository.cache

import com.konai.fxs.common.enumerate.ExchangeRateCacheType.KOREAEXIM_EXCHANGE_RATE_CACHE
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.data.redis.core.RedisTemplate

@CustomSpringBootTest
class V1KoreaeximExchangeRateCacheRepositoryImplTest(
    private val koreaeximExchangeRateRedisTemplate: RedisTemplate<String, V1KoreaeximExchangeRate>
) : CustomStringSpec({

    val v1KoreaeximExchangeRateFixture = dependencies.v1KoreaeximExchangeRateFixture

    "한국수출입은행 환율 Cache 정보 저장 정상 확인한다" {
        // given
        val exchangeRate = v1KoreaeximExchangeRateFixture.make()
        val key = KOREAEXIM_EXCHANGE_RATE_CACHE.getKey(exchangeRate.curUnit)

        // when
        koreaeximExchangeRateRedisTemplate.opsForValue().set(key, exchangeRate)

        // then
        val cache = koreaeximExchangeRateRedisTemplate.opsForValue().get(key)
        cache!! shouldNotBe null
        cache.index shouldBe exchangeRate.index
        cache.registerDate shouldBe exchangeRate.registerDate
        cache.result shouldBe exchangeRate.result
        cache.curUnit shouldBe exchangeRate.curUnit
        cache.curNm shouldBe exchangeRate.curNm
        cache.ttb shouldBe exchangeRate.ttb
        cache.tts shouldBe exchangeRate.tts
        cache.dealBasR shouldBe exchangeRate.dealBasR
        cache.bkpr shouldBe exchangeRate.bkpr
        cache.yyEfeeR shouldBe exchangeRate.yyEfeeR
        cache.tenDdEfeeR shouldBe exchangeRate.tenDdEfeeR
        cache.kftcDealBasR shouldBe exchangeRate.kftcDealBasR
        cache.kftcBkpr shouldBe exchangeRate.kftcBkpr
    }

})