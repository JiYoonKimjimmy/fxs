package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TRANSACTION_CACHE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class TransactionCacheRepositoryImpl(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : TransactionCacheRepository {

    override fun findPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().get(key)
    }

    override fun incrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().increment(key, amount)
    }

    override fun decrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().decrement(key, amount)
    }

    override fun clearPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType) {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        numberRedisTemplate.delete(key)
    }

    override fun savePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String, transactionId: Long) {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        numberRedisTemplate.opsForValue().set(key, transactionId)
    }

    override fun hasPreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String): Boolean {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        return numberRedisTemplate.hasKey(key)
    }

    override fun deletePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String) {
        val key = PREPARED_WITHDRAWAL_TRANSACTION_CACHE.getKey(acquirerId, acquirerType.name, trReferenceId)
        numberRedisTemplate.delete(key)
    }

}