package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TRANSACTION_CACHE
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class V1TransactionCacheRepositoryImpl(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : V1TransactionCacheRepository {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun findPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("findPreparedWithdrawalTotalAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().get(key)
    }

    override fun incrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("incrementPreparedWithdrawalTotalAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().increment(key, amount)
    }

    override fun decrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("decrementPreparedWithdrawalTotalAmountCache : [$key]")
        return numberRedisTemplate.opsForValue().decrement(key, amount)
    }

    override fun clearPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType) {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        logger.info("clearPreparedWithdrawalTotalAmountCache : [$key]")
        numberRedisTemplate.delete(key)
    }

    override fun savePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String, transactionId: Long) {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        logger.info("savePreparedWithdrawalTransactionCache : [$key]")
        numberRedisTemplate.opsForValue().set(key, transactionId)
    }

    override fun hasPreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String): Boolean {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        logger.info("hasPreparedWithdrawalTransactionCache : [$key]")
        return numberRedisTemplate.hasKey(key)
    }

    override fun deletePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String) {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        logger.info("deletePreparedWithdrawalTransactionCache : [$key]")
        numberRedisTemplate.delete(key)
    }

}