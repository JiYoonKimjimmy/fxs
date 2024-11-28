package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class TransactionCacheRepositoryImpl(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : TransactionCacheRepository {

    override fun findWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().get(key)
    }

    override fun incrementWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().increment(key, amount)
    }

    override fun decrementWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number? {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().decrement(key, amount)
    }

    override fun clearWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType) {
        val key = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        numberRedisTemplate.delete(key)
    }
    
}