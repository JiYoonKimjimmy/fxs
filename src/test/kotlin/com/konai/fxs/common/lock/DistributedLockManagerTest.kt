package com.konai.fxs.common.lock

import com.konai.fxs.testsupport.redis.EmbeddedRedis
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import java.util.concurrent.TimeUnit

class DistributedLockManagerTest : StringSpec({

    listeners(EmbeddedRedisTestListener())

    lateinit var redissonClient: RedissonClient
    lateinit var distributedLockManager: DistributedLockManager

    beforeSpec {
        redissonClient = Config()
            .apply {
                this.useSingleServer()
                    .setAddress("redis://${EmbeddedRedis.REDIS_HOST}:${EmbeddedRedis.REDIS_PORT}")
            }
            .let { Redisson.create(it) }

        distributedLockManager = DistributedLockManager(redissonClient)
    }

    afterSpec {
        redissonClient.shutdown()
    }

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

})