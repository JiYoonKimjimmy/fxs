package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.AccountStatus.ACTIVE
import com.konai.fxs.common.enumerate.AccountStatus.INACTIVE
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class V1TransactionDepositServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1TransactionDepositService = dependencies.v1TransactionDepositService
    val v1TransactionFixture = dependencies.v1TransactionFixture
    val v1AccountFixture = dependencies.v1AccountFixture
    val v1AccountRepository = dependencies.fakeV1AccountRepository

    given("외화 계좌 수기 입금 거래 요청되어") {
        var accountInvalid = v1AccountFixture.make()
        val fromAccountInvalid = v1AccountFixture.make()
        val transactionInvalid = v1TransactionFixture.manualDepositTransaction(
            acquirer = accountInvalid.acquirer,
            fromAcquirer = fromAccountInvalid.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1300.0)
        )

        `when`("'acquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionDepositService.manualDeposit(transactionInvalid) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.acquirer.id}' could not found"
            }
        }

        // 외화 계좌 상태 'INACTIVE' 변경
        accountInvalid = v1AccountRepository.save(accountInvalid.update(V1AccountPredicate(status = INACTIVE)))

        `when`("'acquirer' 요청 정보 기준 외화 계좌 정보 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionDepositService.manualDeposit(transactionInvalid) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.acquirer.id}' status is invalid"
            }
        }

        // 외화 계좌 상태 'ACTIVE' 변경
        v1AccountRepository.save(accountInvalid.update(V1AccountPredicate(status = ACTIVE)))

        `when`("'fromAcquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionDepositService.manualDeposit(transactionInvalid) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.fromAcquirer?.id}' could not found"
            }
        }

        // 외화 계좌 상태 'INACTIVE' 변경
        v1AccountRepository.save(fromAccountInvalid.update(V1AccountPredicate(status = INACTIVE)))
        
        `when`("'fromAcquirer' 요청 정보 기준 외화 계좌 정보 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionDepositService.manualDeposit(transactionInvalid) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.fromAcquirer?.id}' status is invalid"
            }
        }

        // 정상 외화 계좌 정보 생성
        val account = v1AccountRepository.save(v1AccountFixture.make())
        val fromAccount = v1AccountRepository.save(v1AccountFixture.make())
        val transaction = v1TransactionFixture.manualDepositTransaction(
            acquirer = account.acquirer,
            fromAcquirer = fromAccount.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1300.0)
        )
        
        `when`("정상 'account' 수기 입금 거래인 경우") {
            val result = v1TransactionDepositService.manualDeposit(transaction)

            then("수기 입금 거래 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe COMPLETED
            }
            
            val entity = v1AccountRepository.findByPredicate(V1AccountPredicate(id = account.id))!!

            then("외화 계좌 잔액 증가 & 평균 환율 & 매입 수량 변경 정상 확인한다") {
                entity.balance shouldBe BigDecimal(100)
                entity.averageExchangeRate.toDouble() shouldBe 1300.00
                entity.depositAmount shouldBe BigDecimal(100)
            }

            then("외화 계좌 수기 입금 거래 내역 생성 정상 확인한다") {

            }
        }
    }

})