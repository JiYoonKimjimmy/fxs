package com.konai.fxs.common.external.koreaexim

import com.konai.fxs.infra.config.ApplicationProperties
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.KoreaeximExchangeRate
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class KoreaeximHttpServiceImpl(
    private val koreaeximHttpServiceProxy: KoreaeximHttpServiceProxy,
    private val applicationProperties: ApplicationProperties,
) : KoreaeximHttpService {

    @Retryable(
        retryFor = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 500, multiplier = 2.0)
    )
    override fun getExchangeRate(searchDate: String): List<KoreaeximExchangeRate> {
        return koreaeximHttpServiceProxy.getExchangeRate(
            apiKey = applicationProperties.koreaeximApiKey,
            apiType = applicationProperties.koreaeximApiType,
            searchDate = searchDate
        ).map { it.toDomain() }
    }

}