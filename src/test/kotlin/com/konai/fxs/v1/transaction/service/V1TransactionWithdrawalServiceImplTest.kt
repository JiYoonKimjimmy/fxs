package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.saveAccount
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1TransactionWithdrawalServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1TransactionWithdrawalService = dependencies.v1TransactionWithdrawalService

    val v1AccountRepository = dependencies.fakeV1AccountRepository
    val v1TransactionRepository = dependencies.fakeV1TransactionRepository

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture

    given("외화 계좌 수기 출금 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence())
        val transaction = v1TransactionFixture.manualWithdrawalTransaction(
            acquirer = account.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1000.00)
        )

        `when`("'acquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionWithdrawalService.manualWithdrawal(transaction) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        // 외화 계좌 정보 저장
        saveAccount(account)

        `when`("'amount' 요청 금액보다 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.manualWithdrawal(transaction) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 0 < (readyAmount: 0 + amount: 100)"
            }
        }

        // 외화 계좌 정보 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        `when`("정상 'account' 수기 출금 요청인 경우") {
            val result = v1TransactionWithdrawalService.manualWithdrawal(transaction)

            then("수기 입금 거래 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe TransactionStatus.COMPLETED
            }

            then("외화 계좌 잔액 감액 변경 정상 확인한다") {
                val accountEntity = v1AccountRepository.findByPredicate(V1AccountPredicate(id = account.id))!!
                accountEntity.balance shouldBe BigDecimal(0)
            }

            then("외화 계좌 수기 입금 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val transactionEntity = v1TransactionRepository.findByPredicate(predicate)

                transactionEntity!! shouldNotBe null
                transactionEntity.id shouldBe result.id
                transactionEntity.acquirer.id shouldBe account.acquirer.id
                transactionEntity.type shouldBe TransactionType.WITHDRAWAL
                transactionEntity.purpose shouldBe TransactionPurpose.WITHDRAWAL
                transactionEntity.channel shouldBe TransactionChannel.PORTAL
                transactionEntity.currency shouldBe Currency.USD
                transactionEntity.amount shouldBe BigDecimal(100)
                transactionEntity.exchangeRate shouldBe BigDecimal(1000)
                transactionEntity.transferDate shouldBe transaction.transferDate
                transactionEntity.status shouldBe TransactionStatus.COMPLETED
            }
        }
    }

})