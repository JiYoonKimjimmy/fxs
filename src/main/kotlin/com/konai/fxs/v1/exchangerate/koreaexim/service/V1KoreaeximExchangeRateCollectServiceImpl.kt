package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.common.external.koreaexim.KoreaeximHttpService
import com.konai.fxs.v1.exchangerate.koreaexim.repository.V1KoreaeximExchangeRateRepository
import com.konai.fxs.v1.exchangerate.koreaexim.repository.cache.V1KoreaeximExchangeRateCacheRepository
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class V1KoreaeximExchangeRateCollectServiceImpl(
    private val koreaeximHttpService: KoreaeximHttpService,
    private val v1KoreaeximExchangeRateRepository: V1KoreaeximExchangeRateRepository,
    private val v1KoreaeximExchangeRateCacheRepository: V1KoreaeximExchangeRateCacheRepository
) : V1KoreaeximExchangeRateCollectService {

    override fun collect(index: Int, searchDate: String): List<V1KoreaeximExchangeRate> {
        /**
         * 한국수출입은행 환율 정보 조회 처리
         * 1. Koreaexim 환율 정보 조회
         * 2. 최근 Koreaexim 환율 Cache 정보 변경
         * 3. Koreaexim 환율 DB 정보 저장
         */
        return koreaeximHttpService.getExchangeRates(searchDate)
            .takeIf { it.isSuccess }
            ?.let { saveAllKoreaeximExchangeRates(it.content) }
            ?: emptyList()
    }

    private fun saveAllKoreaeximExchangeRates(content: List<V1KoreaeximExchangeRate>): List<V1KoreaeximExchangeRate> {
        runBlocking {
            // 최근 Koreaexim 환율 Cache 정보 변경
            launch(Dispatchers.IO) { v1KoreaeximExchangeRateRepository.saveAll(content) }
            // Koreaexim 환율 DB 정보 저장
            launch(Dispatchers.IO) { v1KoreaeximExchangeRateCacheRepository.saveAllKoreaeximExchangeRateCache(content) }
        }
        return content
    }

}