package com.konai.fxs.common.lock

import com.konai.fxs.common.enumerate.DistributedLockType
import com.konai.fxs.v1.account.service.domain.V1Account
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class DistributedLockManager(
    private val redissonClient: RedissonClient,
) {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun <R> lock(
        key: String,
        waitTime: Long = 20,
        leaseTime: Long = 60,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        block:() -> R
    ): R {
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

    fun <R> accountLock(
        lockType: DistributedLockType,
        account: V1Account,
        block:() -> R
    ): R {
        val key = lockType.getKey(account.id.toString())
        return lock(key = key, leaseTime = 0, block = block)
    }

}