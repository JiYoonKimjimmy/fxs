package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.PENDING_TRANSACTION_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.PENDING_TRANSACTION_CACHE
import com.konai.fxs.common.enumerate.TransactionChannel
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class V1TransactionCacheRepositoryImpl(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : V1TransactionCacheRepository {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun savePendingTransactionCache(trReferenceId: String, channel: TransactionChannel, transactionId: Long) {
        val key = PENDING_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("savePendingTransactionCache : [$key]")
        numberRedisTemplate.opsForValue().set(key, transactionId)
    }

    override fun findPendingTransactionCache(trReferenceId: String, channel: TransactionChannel): Long? {
        val key = PENDING_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("findPendingTransactionCache : [$key]")
        return numberRedisTemplate.opsForValue().get(key)?.toLong()
    }

    override fun hasPendingTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean {
        val key = PENDING_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("hasPendingTransactionCache : [$key]")
        return numberRedisTemplate.hasKey(key)
    }

    override fun deletePendingTransactionCache(trReferenceId: String, channel: TransactionChannel) {
        val key = PENDING_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("deletePendingTransactionCache : [$key]")
        numberRedisTemplate.delete(key)
    }

    override fun findPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = PENDING_TRANSACTION_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("findPendingTransactionAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().get(key)
    }

    override fun incrementPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PENDING_TRANSACTION_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("incrementPendingTransactionAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().increment(key, amount)
    }

    override fun decrementPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PENDING_TRANSACTION_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("decrementPendingTransactionAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().decrement(key, amount)
    }

    override fun clearPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType) {
        val key = PENDING_TRANSACTION_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("clearPendingTransactionAmountCache : [$key]")
        numberRedisTemplate.delete(key)
    }

}