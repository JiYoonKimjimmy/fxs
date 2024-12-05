package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE
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

    override fun saveWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel, transactionId: Long) {
        val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("saveWithdrawalTransactionCache : [$key]")
        numberRedisTemplate.opsForValue().set(key, transactionId)
    }

    override fun findWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Long? {
        val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("findWithdrawalTransactionCache : [$key]")
        return numberRedisTemplate.opsForValue().get(key)?.toLong()
    }

    override fun hasWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean {
        val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("hasWithdrawalTransactionCache : [$key]")
        return numberRedisTemplate.hasKey(key)
    }

    override fun deleteWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel) {
        val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel.name)
        logger.info("deleteWithdrawalTransactionCache : [$key]")
        numberRedisTemplate.delete(key)
    }

    override fun findWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("findWithdrawalTransactionPendingAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().get(key)
    }

    override fun incrementWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("incrementWithdrawalTransactionPendingAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().increment(key, amount)
    }

    override fun decrementWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("decrementWithdrawalTransactionPendingAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().decrement(key, amount)
    }

    override fun clearWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType) {
        val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("clearWithdrawalTransactionPendingAmountCache : [$key]")
        numberRedisTemplate.delete(key)
    }

}