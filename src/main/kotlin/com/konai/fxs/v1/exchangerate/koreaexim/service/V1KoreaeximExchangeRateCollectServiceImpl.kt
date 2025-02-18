package com.konai.fxs.v1.exchangerate.koreaexim.service

import com.konai.fxs.common.VIRTUAL_THREAD
import com.konai.fxs.common.error
import com.konai.fxs.common.external.koreaexim.KoreaeximHttpService
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.message.MessageQueueExchange.V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE
import com.konai.fxs.message.MessageQueuePublisher
import com.konai.fxs.message.V1ExchangeRateCollectorTimerMessage
import com.konai.fxs.v1.exchangerate.koreaexim.repository.V1KoreaeximExchangeRateRepository
import com.konai.fxs.v1.exchangerate.koreaexim.repository.cache.V1KoreaeximExchangeRateCacheRepository
import com.konai.fxs.v1.exchangerate.koreaexim.service.domain.V1KoreaeximExchangeRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.stereotype.Service

@Service
class V1KoreaeximExchangeRateCollectServiceImpl(
    private val v1KoreaeximExchangeRateRepository: V1KoreaeximExchangeRateRepository,
    private val v1KoreaeximExchangeRateCacheRepository: V1KoreaeximExchangeRateCacheRepository,
    private val koreaeximHttpService: KoreaeximHttpService,
    private val distributedLockManager: DistributedLockManager,
    private val messageQueuePublisher: MessageQueuePublisher
) : V1KoreaeximExchangeRateCollectService {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun ready(date: String, size: Int, ttl: Int) {
        try {
            distributedLockManager.exchangeRateCollectorTimerLock(date) {
                for (i in 1..size) {
                    val message = V1ExchangeRateCollectorTimerMessage(index = i, date = date)
                    val messagePostProcessor = MessagePostProcessor { it.messageProperties.expiration = "${ttl * i}"; it }
                    messageQueuePublisher.sendDirectMessage(V1_EXCHANGE_RATE_COLLECTOR_TIMER_EXCHANGE, message, messagePostProcessor)
                }
            }
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    override fun collect(index: Int, searchDate: String): List<V1KoreaeximExchangeRate> {
        /**
         * 한국수출입은행 환율 정보 조회 처리
         * 1. Koreaexim 환율 정보 조회
         * 2. 최근 Koreaexim 환율 Cache 정보 변경
         * 3. Koreaexim 환율 DB 정보 저장
         */
        return koreaeximHttpService.getExchangeRates(searchDate)
            .takeIf { it.isSuccess }
            ?.content
            ?.applyIndex(index)
            ?.let { saveAllKoreaeximExchangeRates(it) }
            ?: emptyList()
    }

    private fun List<V1KoreaeximExchangeRate>.applyIndex(index: Int): List<V1KoreaeximExchangeRate> {
        return this.map { it.applyIndex(index) }
    }

    private fun saveAllKoreaeximExchangeRates(content: List<V1KoreaeximExchangeRate>): List<V1KoreaeximExchangeRate> {
        runBlocking(Dispatchers.VIRTUAL_THREAD) {
            // 최근 Koreaexim 환율 Cache 정보 변경
            launch { v1KoreaeximExchangeRateRepository.saveAll(content) }
            // Koreaexim 환율 DB 정보 저장
            launch { v1KoreaeximExchangeRateCacheRepository.saveAllKoreaeximExchangeRateCache(content) }
        }
        return content
    }

}