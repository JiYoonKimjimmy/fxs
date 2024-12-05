package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.matchers.bigdecimal.shouldBeZero
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1TransactionCacheServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1TransactionCacheService = dependencies.v1TransactionCacheService
    val numberRedisTemplate = dependencies.numberRedisTemplate

    val v1TransactionFixture = dependencies.v1TransactionFixture

    given("출금 거래 대기 금액 Cache 조회 요청되어") {
        val acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT)

        `when`("Cache 정보 없는 경우") {
            val result = v1TransactionCacheService.findWithdrawalTransactionPendingAmountCache(acquirer)

            then("'0' 금액 반환 정상 확인한다") {
                result.shouldBeZero()
            }
        }

        // 출금 거래 대기 금액 Cache 저장
        val cacheKey = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirer.id, acquirer.type.name)
        val cacheValue = 100000
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue)

        `when`("Cache 정보 있는 경우") {
            val result = v1TransactionCacheService.findWithdrawalTransactionPendingAmountCache(acquirer)

            then("'100000' 금액 반환 정상 확인한다") {
                result.shouldBeEqual(BigDecimal(100000))
            }
        }
    }

    given("출금 거래 Cache 저장 요청되어") {
        val transaction = v1TransactionFixture.make()

        `when`("Cache 저장 성공인 경우") {
            val result = v1TransactionCacheService.saveWithdrawalTransactionCache(transaction)

            then("처리 결과 정상 확인한다") {
                result shouldNotBe null
                result.id shouldBe transaction.id
            }
        }
    }

    given("출금 거래 대기 금액 Cache 존재 여부 확인 요청되어") {
        val transaction = v1TransactionCacheService.saveWithdrawalTransactionCache(v1TransactionFixture.make())
        val trReferenceId = transaction.trReferenceId
        val channel = transaction.channel

        `when`("Cache 정보 존재하는 경우") {
            val result = v1TransactionCacheService.hasWithdrawalTransactionCache(trReferenceId, channel)

            then("'true' 결과 정상 확인한다") {
                result shouldBe true
            }
        }
    }

    given("출금 거래 대기 금액 Cache 삭제 요청되어") {
        val transaction = v1TransactionCacheService.saveWithdrawalTransactionCache(v1TransactionFixture.make())
        val trReferenceId = transaction.trReferenceId
        val channel = transaction.channel

        `when`("Cache 삭제 성공인 경우") {
            v1TransactionCacheService.deleteWithdrawalTransactionCache(trReferenceId, channel)

            then("처리 결과 정상 확인한다") {
                v1TransactionCacheService.hasWithdrawalTransactionCache(trReferenceId, channel) shouldBe false
            }
        }
    }

})