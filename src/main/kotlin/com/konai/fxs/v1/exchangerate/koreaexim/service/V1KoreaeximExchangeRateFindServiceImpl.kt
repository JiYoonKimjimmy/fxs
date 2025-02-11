package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.exchangerate.koreaexim.repository.V1KoreaeximExchangeRateRepository
import com.konai.fxs.v1.exchangerate.koreaexim.repository.cache.V1KoreaeximExchangeRateCacheRepository
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import org.springframework.stereotype.Service

@Service
class V1KoreaeximExchangeRateFindServiceImpl(
    private val v1KoreaeximExchangeRateRepository: V1KoreaeximExchangeRateRepository,
    private val v1KoreaeximExchangeRateCacheRepository: V1KoreaeximExchangeRateCacheRepository,
) : V1KoreaeximExchangeRateFindService {

    override fun findLatestExchangeRate(currency: String, requestDate: String): V1KoreaeximExchangeRate {
        return v1KoreaeximExchangeRateCacheRepository.findKoreaeximExchangeRateCache(currency = currency)
            ?: v1KoreaeximExchangeRateRepository.findLatestKoreaeximExchangeRate(registerDate = requestDate, curUnit = currency)
            ?.let { v1KoreaeximExchangeRateCacheRepository.saveKoreaeximExchangeRateCache(it) }
            ?: throw ResourceNotFoundException(ErrorCode.EXCHANGE_RATE_NOT_FOUND)
    }

}