package com.konai.fxs.common.lock

import com.konai.fxs.common.enumerate.DistributedLockType
import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1Account
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class DistributedLockManagerImpl(
    private val redissonClient: RedissonClient
) : DistributedLockManager {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun <R> lock(
        key: String,
        waitTime: Long,
        leaseTime: Long,
        timeUnit: TimeUnit,
        block: () -> R
    ): R {
        val lock = redissonClient.getLock(key)

        return try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                logger.info("Redisson '$key' locked.")
                block()
            } else {
                logger.info("Redisson '$key' lock attempt failed.")
                throw InternalServiceException(ErrorCode.REDISSON_LOCK_ATTEMPT_ERROR)
            }
        } finally {
            try {
                if (lock.isHeldByCurrentThread) {
                    lock.unlock().also { logger.info("Redisson '$key' unlocked.") }
                }
            } catch (e: IllegalMonitorStateException) {
                logger.error("Redisson '$key' Lock already unLock.")
            }
        }
    }

    override fun <R> sequenceLock(sequenceType: SequenceType, block: () -> R): R {
        val key = DistributedLockType.SEQUENCE_LOCK.getKey(sequenceType.name)
        return lock(key = key, leaseTime = 0, block = block)
    }

    override fun <R> accountLock(account: V1Account, block: () -> R): R {
        val key = DistributedLockType.ACCOUNT_LOCK.getKey(account.id.toString())
        return lock(key = key, leaseTime = 0, block = block)
    }

    override fun <R> withdrawalTransactionAmountLock(account: V1Account, block: () -> R): R {
        val key = DistributedLockType.WITHDRAWAL_TRANSACTION_AMOUNT_LOCK.getKey(account.id.toString())
        return lock(key = key, leaseTime = 0, block = block)
    }

    override fun <R> exchangeRateCollectorTimerLock(date: String, block: () -> R): R {
        val key = DistributedLockType.EXCHANGE_RATE_COLLECTOR_TIMER_LOCK.getKey(date)
        return lock(key = key, waitTime = 0, leaseTime = 0, block = block)
    }
}