package com.konai.fxs.v1.account.service

import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.redis.RedisTestListener
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class V1AccountValidationServiceImplTest : CustomBehaviorSpec({

    listeners(RedisTestListener())

    val v1AccountValidationService = dependencies.v1AccountValidationService
    val v1AccountSaveService = dependencies.v1AccountSaveService
    val v1AccountFixture = dependencies.v1AccountFixture
    val numberRedisTemplate = dependencies.numberRedisTemplate

    fun saveAccount(account: V1Account): V1Account {
        return v1AccountSaveService.save(account)
    }

    given("외화 계좌 상태 확인 요청하여") {
        val currency = "USD"

        `when`("'acquirer' 조건 일치한 외화 계좌 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountValidationService.checkStatus(V1Acquirer(generateUUID(), AcquirerType.FX_DEPOSIT), currency) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        val inActiveAccount = saveAccount(v1AccountFixture.make(status = AccountStatus.INACTIVE))

        `when`("'acquirer' 조건 일치한 외화 계좌 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkStatus(inActiveAccount.acquirer, inActiveAccount.currency) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
            }
        }

        val activeAccount = saveAccount(v1AccountFixture.make(status = AccountStatus.ACTIVE))
        
        `when`("'acquirer' 조건 일치한 외화 계좌 상태 'ACTIVE' 인 경우") {
            val result = v1AccountValidationService.checkStatus(activeAccount.acquirer, activeAccount.currency)
            
            then("예외 발생 없이 정상 확인한다") {
                result.id shouldBe activeAccount.id
                result.acquirer.id shouldBe activeAccount.acquirer.id
                result.status shouldBe AccountStatus.ACTIVE
            }
        }
    }

    given("외화 계좌 한도 확인 요청하여") {
        val account = v1AccountFixture.make(balance = 100000)
        val amount100001 = BigDecimal.valueOf(100001)

        `when`("'amount' 요청 금액보다 계좌 한도 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkLimit(account, amount100001) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
            }
        }

        val amount99990 = BigDecimal.valueOf(99990)

        `when`("계좌 잔액 충분하여 한도 확인 결과 정상인 경우") {
            val result = v1AccountValidationService.checkLimit(account, amount99990)

            then("예외 발생 없이 정상 확인한다") {
                result.acquirer.id shouldBe account.acquirer.id
            }
        }

        // 출금 준비 합계 Cache 저장
        val cacheKey = WITHDRAWAL_READY_TOTAL_AMOUNT_CACHE.getKey(account.acquirer.id, account.acquirer.type.name)
        val cacheValue11 = 11
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue11)

        `when`("계좌 잔액은 충분하지만, (요청 금액 + 출금 준비 합계) 한도 초과인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkLimit(account, amount99990) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
            }
        }

        val cacheValue10 = 10
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue10)

        `when`("계좌 잔액 충분 & (요청 금액 + 출금 준비 합계) 한도 확인 결과 정상인 경우") {
            val result = v1AccountValidationService.checkLimit(account, amount99990)

            then("예외 발생 없이 정상 확인한다") {
                result.acquirer.id shouldBe account.acquirer.id
            }
        }
    }

})