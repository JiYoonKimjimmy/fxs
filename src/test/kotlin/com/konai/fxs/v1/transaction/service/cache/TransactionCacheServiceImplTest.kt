package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionCacheType.PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.matchers.bigdecimal.shouldBeZero
import io.kotest.matchers.equals.shouldBeEqual
import java.math.BigDecimal

class TransactionCacheServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val transactionCacheService = dependencies.transactionCacheService
    val numberRedisTemplate = dependencies.numberRedisTemplate

    given("외화계좌 출금 준비 금액 합계 Cache 조회 요청되어") {
        val acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT)

        `when`("Cache 정보 없는 경우") {
            val result = transactionCacheService.findPreparedWithdrawalTotalAmountCache(acquirer)

            then("'0' 금액 반환 정상 확인한다") {
                result.shouldBeZero()
            }
        }

        // cache 정보 저장
        val cacheKey = PREPARED_WITHDRAWAL_TOTAL_AMOUNT_CACHE.getKey(acquirer.id, acquirer.type.name)
        val cacheValue = 100000
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue)

        `when`("Cache 정보 있는 경우") {
            val result = transactionCacheService.findPreparedWithdrawalTotalAmountCache(acquirer)

            then("'100000' 금액 반환 정상 확인한다") {
                result.shouldBeEqual(BigDecimal(100000))
            }
        }
    }

})