package com.konai.fxs.v1.exchangerate.koreaexim.repository.cache

import com.konai.fxs.common.enumerate.ExchangeRateCacheType.KOREAEXIM_EXCHANGE_RATE_CACHE
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class V1KoreaeximExchangeRateCacheRepositoryImpl(
    private val koreaeximExchangeRateRedisTemplate: RedisTemplate<String, V1KoreaeximExchangeRate>
) : V1KoreaeximExchangeRateCacheRepository {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun saveKoreaeximExchangeRateCache(exchangeRate: V1KoreaeximExchangeRate) {
        val key = KOREAEXIM_EXCHANGE_RATE_CACHE.getKey(exchangeRate.curUnit)
        logger.info("saveKoreaeximExchangeRateCache : [$key]")
        koreaeximExchangeRateRedisTemplate.opsForValue().set(key, exchangeRate)
    }

    override fun saveAllKoreaeximExchangeRateCache(exchangeRates: List<V1KoreaeximExchangeRate>) {
        exchangeRates.forEach { saveKoreaeximExchangeRateCache(it) }
    }

    override fun findKoreaeximExchangeRateCache(currency: String): V1KoreaeximExchangeRate? {
        val key = KOREAEXIM_EXCHANGE_RATE_CACHE.getKey(currency)
        logger.info("findKoreaeximExchangeRateCache : [$key]")
        return koreaeximExchangeRateRedisTemplate.opsForValue().get(key)
    }

    override fun clearKoreaeximExchangeRateCache(currency: String) {
        val key = KOREAEXIM_EXCHANGE_RATE_CACHE.getKey(currency)
        logger.info("clearKoreaeximExchangeRateCache : [$key]")
        koreaeximExchangeRateRedisTemplate.delete(key)
    }

}