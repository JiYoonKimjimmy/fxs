package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.annotation.CustomSpringBootTest
import io.kotest.matchers.shouldBe
import org.springframework.data.redis.core.RedisTemplate

@CustomSpringBootTest
class TransactionCacheRepositoryImplTest(
    private val numberRedisTemplate: RedisTemplate<String, Number>
) : CustomStringSpec({

    "RedisTemplate 사용하여 Cache 저장 정상 확인한다" {
        // given
        val key = "test-key"
        val value = 10000

        // when
        numberRedisTemplate.opsForValue().set(key, value)

        // then
        val result = numberRedisTemplate.opsForValue().get(key)
        result shouldBe value
    }

})