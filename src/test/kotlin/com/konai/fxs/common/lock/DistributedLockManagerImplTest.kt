package com.konai.fxs.common.lock

import com.konai.fxs.common.enumerate.SequenceType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.redis.EmbeddedRedis
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class DistributedLockManagerImplTest : CustomStringSpec({

    listeners(EmbeddedRedisTestListener())

    val v1AccountFixture = dependencies.v1AccountFixture

    val redissonClient by lazy { EmbeddedRedis.redissonClient }
    val distributedLockManager by lazy { DistributedLockManagerImpl(redissonClient) }

    "RedissonClient 활용한 Distributed Lock 처리 정상 확인한다" {
        // given
        val timeUnit = TimeUnit.SECONDS
        val waitTime = 5L
        val leaseTime = 10L
        val lockKey = "redissonClient-test-lock"
        val lock = redissonClient.getLock(lockKey)

        // when then
        try {
            val result = lock.tryLock(waitTime, leaseTime, timeUnit)

            result shouldBe true

            println("!! $lockKey Locked !!")
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
                println("!! $lockKey Released !!")
            }
        }
    }

    "DistributedLockManager 활용한 Distributed Lock 처리 정상 확인한다" {
        // given
        val timeUnit = TimeUnit.SECONDS
        val waitTime = 5L
        val leaseTime = 10L
        val lockKey = "distributedLockManager-test-lock"

        // when
        val result = distributedLockManager.lock(lockKey, waitTime, leaseTime, timeUnit) {
            "Hello World!!"
        }

        // then
        result shouldBe "Hello World!!"
    }

    "Distributed Lock 유지 시간 설정 없이 정상 확인한다" {
        // given
        val timeUnit = TimeUnit.SECONDS
        val waitTime = 5L
        val lockKey = "distributedLockManager-test-lock"

        // when
        val result = runBlocking {
            distributedLockManager.lock(lockKey, waitTime, timeUnit = timeUnit) {
                Thread.sleep(5000)
                "Hello World!!"
            }
        }

        // then
        result shouldBe "Hello World!!"
    }

    "Lock 유지 시간 설정 없이 'accountLock' 정상 확인한다" {
        // given
        val account = v1AccountFixture.make(id = generateSequence())

        // when
        val result = distributedLockManager.accountLock(account) {
            "Hello World, ${account.acquirer.name}"
        }

        // then
        result shouldBe "Hello World, 외화 예치금 계좌"
    }

    "Lock 유지 시간 설정 없이 'sequenceLock' 정상 확인한다" {
        // given
        val sequenceType = SequenceType.TRANSACTION_SEQUENCE

        // when
        val result = distributedLockManager.sequenceLock(sequenceType) {
            "Hello World, ${sequenceType.name}"
        }

        // then
        result shouldBe "Hello World, TRANSACTION_SEQUENCE"
    }

    "'exchangeRateCollectorTimerLock' 중복 Lock 점유 요청 예외 발생 정상 확인한다" {
        // given
        val date = "20241227"
        val block: () -> String = {
            Thread.sleep(2000)
            "Hello World, $date."
        }

        // when
        val task1 = async(Dispatchers.Default) { distributedLockManager.exchangeRateCollectorTimerLock(date) { block() } }
        val task2 = async(Dispatchers.Default) {
            delay(1000)
            shouldThrow<InternalServiceException> { distributedLockManager.exchangeRateCollectorTimerLock(date) { block() } }
        }

        // then
        task1.await() shouldBe "Hello World, 20241227."
        task2.await().errorCode shouldBe ErrorCode.REDISSON_LOCK_ATTEMPT_ERROR
    }

})