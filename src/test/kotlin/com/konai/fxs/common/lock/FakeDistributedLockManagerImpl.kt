package com.konai.fxs.common.lock

import com.konai.fxs.common.enumerate.DistributedLockType
import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.testsupport.redis.EmbeddedRedis
import com.konai.fxs.v1.account.service.domain.V1Account
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class FakeDistributedLockManagerImpl : DistributedLockManager {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun <R> lock(key: String, waitTime: Long, leaseTime: Long, timeUnit: TimeUnit, block: () -> R): R {
        val redissonClient = EmbeddedRedis.redissonClient
        val lock = redissonClient.getLock(key)

        return try {
            lock.tryLock(waitTime, leaseTime, timeUnit).also { logger.info("Redisson '$key' locked.") }
            // block 함수 처리
            block()
        } finally {
            try {
                if (lock.isHeldByCurrentThread) {
                    lock.unlock()
                        .also { logger.info("Redisson '$key' unlocked.") }
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