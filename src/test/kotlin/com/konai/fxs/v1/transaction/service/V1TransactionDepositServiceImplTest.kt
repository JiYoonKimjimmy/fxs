package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus.ACTIVE
import com.konai.fxs.common.enumerate.AccountStatus.INACTIVE
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1TransactionDepositServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1TransactionDepositService = dependencies.v1TransactionDepositService

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture

    val v1AccountRepository = dependencies.fakeV1AccountRepository
    val v1TransactionRepository = dependencies.fakeV1TransactionRepository

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
                result.id shouldBeGreaterThanOrEqual 1L
                result.status shouldBe COMPLETED
            }

            then("외화 계좌 잔액 증가 & 평균 환율 & 매입 수량 변경 정상 확인한다") {
                val accountEntity = v1AccountRepository.findByPredicate(V1AccountPredicate(id = account.id))!!
                accountEntity.balance shouldBe BigDecimal(100)
                accountEntity.averageExchangeRate.toDouble() shouldBe 1300.00
                accountEntity.depositAmount shouldBe BigDecimal(100)
            }

            then("외화 계좌 수기 입금 거래 내역 생성 정상 확인한다") {
                val acquirerPredicate = V1AcquirerPredicate(transaction.acquirer.id, transaction.acquirer.type, transaction.acquirer.name)
                val fromAcquirerPredicate = V1AcquirerPredicate(transaction.fromAcquirer?.id, transaction.fromAcquirer?.type, transaction.fromAcquirer?.name)
                val predicate = V1TransactionPredicate(acquirer = acquirerPredicate, fromAcquirer = fromAcquirerPredicate)
                val transactionEntity = v1TransactionRepository.findByPredicate(predicate)

                transactionEntity!! shouldNotBe null
                transactionEntity.id shouldBe result.id
                transactionEntity.acquirer.id shouldBe account.acquirer.id
                transactionEntity.fromAcquirer?.id shouldBe fromAccount.acquirer.id
                transactionEntity.type shouldBe TransactionType.DEPOSIT
                transactionEntity.purpose shouldBe TransactionPurpose.DEPOSIT
                transactionEntity.channel shouldBe TransactionChannel.PORTAL
                transactionEntity.currency shouldBe Currency.USD
                transactionEntity.amount shouldBe BigDecimal(100)
                transactionEntity.exchangeRate shouldBe BigDecimal(1300)
                transactionEntity.transferDate shouldBe transaction.transferDate
                transactionEntity.status shouldBe COMPLETED
            }
        }
    }

})