package com.konai.fxs.infra.config

import com.konai.fxs.testsupport.CustomStringSpec
import com.konai.fxs.testsupport.EmbeddedRedis.numberRedisTemplate
import com.konai.fxs.testsupport.RedisTestListener
import io.kotest.matchers.shouldBe

class RedisConfigTest: CustomStringSpec({

    listeners(RedisTestListener())

    "cache key 기준 '10000' value 저장 정상 확인한다" {
        // given
        val key = "cache-key"
        val value = 10000

        // when
        numberRedisTemplate.opsForValue().set(key, value)

        // then
        val result = numberRedisTemplate.opsForValue().get(key)
        result shouldBe value
    }

})