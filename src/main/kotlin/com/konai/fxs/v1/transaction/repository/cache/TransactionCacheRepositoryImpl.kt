package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class TransactionCacheRepositoryImpl(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : TransactionCacheRepository {

    override fun findWithdrawalReadyTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number? {
        val key = WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE.getKey(acquirerId, acquirerType.name)
        return numberRedisTemplate.opsForValue().get(key)
    }

}