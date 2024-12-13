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
import com.konai.fxs.testsupport.TestCommonFunctions.saveAccount
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

    val v1AccountFindService = dependencies.v1AccountFindService
    val v1TransactionFindService = dependencies.v1TransactionFindService

    given("'외화 매입처 계좌 > 외화 예치금 계좌' 수기 입금 거래 요청되어") {
        var accountInvalid = v1AccountFixture.make()
        val fromAccountInvalid = v1AccountFixture.make()
        val transactionInvalid = v1TransactionFixture.manualDepositTransaction(
            baseAcquirer = accountInvalid.acquirer,
            targetAcquirer = fromAccountInvalid.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1300.0)
        )

        `when`("'baseAcquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionDepositService.deposit(transactionInvalid) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.baseAcquirer.id}' could not found"
            }
        }

        // 외화 계좌 상태 'INACTIVE' 변경
        accountInvalid = saveAccount(accountInvalid, status = INACTIVE)

        `when`("'baseAcquirer' 요청 정보 기준 외화 계좌 정보 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionDepositService.deposit(transactionInvalid) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.baseAcquirer.id}' status is invalid"
            }
        }

        // 외화 계좌 상태 'ACTIVE' 변경
        saveAccount(accountInvalid, status = ACTIVE)

        `when`("'targetAcquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionDepositService.deposit(transactionInvalid) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.targetAcquirer?.id}' could not found"
            }
        }

        // 외화 계좌 상태 'INACTIVE' 변경
        saveAccount(fromAccountInvalid, status = INACTIVE)
        
        `when`("'targetAcquirer' 요청 정보 기준 외화 계좌 정보 상태 'ACTIVE' 아닌 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionDepositService.deposit(transactionInvalid) }

            then("'ACCOUNT_STATUS_IS_INVALID' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_STATUS_IS_INVALID
                exception.detailMessage shouldBe "Account for acquirerId '${transactionInvalid.targetAcquirer?.id}' status is invalid"
            }
        }

        // 정상 외화 계좌 정보 생성
        val account = saveAccount(v1AccountFixture.make())
        val fromAccount = saveAccount(v1AccountFixture.make())
        val transaction = v1TransactionFixture.manualDepositTransaction(
            baseAcquirer = account.acquirer,
            targetAcquirer = fromAccount.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1300.0)
        )
        
        `when`("수기 입금 거래 요청 성공인 경우") {
            val result = v1TransactionDepositService.deposit(transaction)

            then("수기 입금 거래 결과 상태 'COMPLETED' 정상 확인한다") {
                result.id!! shouldBeGreaterThanOrEqual 1L
                result.status shouldBe COMPLETED
            }

            then("'외화 예치금 계좌' 잔액 증액 & 평균 환율 & 매입 수량 변경 정상 확인한다") {
                val saved = v1AccountFindService.findByPredicate(V1AccountPredicate(id = account.id))!!
                saved.balance shouldBe BigDecimal(100)
                saved.averageExchangeRate.toDouble() shouldBe 1300.00
                saved.depositAmount shouldBe BigDecimal(100)
            }

            then("수기 입금 거래 내역 정보 생성 정상 확인한다") {
                val acquirerPredicate = V1AcquirerPredicate(transaction.baseAcquirer.id, transaction.baseAcquirer.type, transaction.baseAcquirer.name)
                val fromAcquirerPredicate = V1AcquirerPredicate(transaction.targetAcquirer?.id, transaction.targetAcquirer?.type, transaction.targetAcquirer?.name)
                val predicate = V1TransactionPredicate(baseAcquirer = acquirerPredicate, targetAcquirer = fromAcquirerPredicate)
                val saved = v1TransactionFindService.findByPredicate(predicate)

                saved!! shouldNotBe null
                saved.id shouldBe result.id
                saved.channel shouldBe TransactionChannel.PORTAL
                saved.baseAcquirer.id shouldBe account.acquirer.id
                saved.targetAcquirer?.id shouldBe fromAccount.acquirer.id
                saved.type shouldBe TransactionType.DEPOSIT
                saved.purpose shouldBe TransactionPurpose.DEPOSIT
                saved.currency shouldBe Currency.USD
                saved.amount shouldBe BigDecimal(100)
                saved.beforeBalance shouldBe BigDecimal.ZERO
                saved.afterBalance shouldBe BigDecimal(100)
                saved.exchangeRate shouldBe BigDecimal(1300)
                saved.transferDate shouldBe transaction.transferDate
                saved.status shouldBe COMPLETED
            }
        }
    }

})