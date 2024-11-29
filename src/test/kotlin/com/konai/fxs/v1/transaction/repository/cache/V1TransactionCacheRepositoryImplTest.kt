package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import io.kotest.matchers.shouldBe
import org.springframework.data.redis.core.RedisTemplate

@CustomSpringBootTest
class V1TransactionCacheRepositoryImplTest(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : CustomStringSpec({

    "'numberRedisTemplate' 사용하여 Cache 저장 정상 확인한다" {
        // given
        val key = "test-key"
        val value = 10000
        numberRedisTemplate.opsForValue().set(key, value)

        // when
        val result = numberRedisTemplate.opsForValue().get(key)

        // then
        result shouldBe value
    }

    "'numberRedisTemplate' 사용하여 Cache 저장하고, 'hasKey' 존재 유무 정상 확인한다" {
        // given
        val key = "test-key"
        val value = 1
        numberRedisTemplate.opsForValue().set(key, value)

        // when
        val result = numberRedisTemplate.hasKey(key)

        // then
        result shouldBe true
    }

    "'numberRedisTemplate' 사용하여 Cache 저장하고, 'delete' 삭제 정상 확인한다" {
        // given
        val key = "test-key"
        val value = 1
        numberRedisTemplate.opsForValue().set(key, value)

        // when
        numberRedisTemplate.delete(key)

        // then
        numberRedisTemplate.hasKey(key) shouldBe false
    }

})