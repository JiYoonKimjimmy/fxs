package com.konai.fxs.v1.account.service

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.saveAccount
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class V1AccountValidationServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1AccountValidationService = dependencies.v1AccountValidationService

    val v1AccountFixture = dependencies.v1AccountFixture
    val numberRedisTemplate = dependencies.numberRedisTemplate

    given("외화 계좌 상태 확인 요청하여") {
        val currency = Currency.USD

        `when`("'acquirer' 조건 일치한 외화 계좌 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountValidationService.checkStatus(V1Acquirer(generateUUID(), FX_DEPOSIT), currency) }

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
        val acquirer = V1Acquirer(generateUUID(), FX_DEPOSIT)
        val currency = Currency.USD
        val amount100001 = BigDecimal(100001)

        `when`("'acquirer' 기준 외화 계좌 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1AccountValidationService.checkLimit(acquirer, currency, amount100001) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        // 외화 계좌 DB 저장
        saveAccount(v1AccountFixture.make(acquirerId = acquirer.id, acquirerType = acquirer.type, currency = currency, balance = 100000))

        `when`("'amount' 요청 금액보다 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkLimit(acquirer, currency, amount100001) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
            }
        }

        val amount99990 = BigDecimal(99990)

        `when`("계좌 잔액 충분하여 한도 확인 결과 정상인 경우") {
            val result = v1AccountValidationService.checkLimit(acquirer, currency, amount99990)

            then("예외 발생 없이 정상 확인한다") {
                result.acquirer.id shouldBe acquirer.id
            }
        }

        // 출금 거래 대기 금액 Cache 저장
        val cacheKey = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirer.id, acquirer.type.name)
        val cacheValue11 = 11
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue11)

        `when`("계좌 잔액은 충분하지만, (요청 금액 + 출금 거래 대기 금액) 한도 초과인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1AccountValidationService.checkLimit(acquirer, currency, amount99990) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
            }
        }

        val cacheValue10 = 10
        numberRedisTemplate.opsForValue().set(cacheKey, cacheValue10)

        `when`("계좌 잔액 충분 & (요청 금액 + 출금 거래 대기 금액) 한도 확인 결과 정상인 경우") {
            val result = v1AccountValidationService.checkLimit(acquirer, currency, amount99990)

            then("예외 발생 없이 정상 확인한다") {
                result.acquirer.id shouldBe acquirer.id
            }
        }
    }

})