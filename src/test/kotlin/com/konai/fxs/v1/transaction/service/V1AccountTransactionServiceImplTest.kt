package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.common.enumerate.AcquirerType.MTO_FUNDING
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.saveAccount
import com.konai.fxs.testsupport.TestExtensionFunctions.toPredicate
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1AccountTransactionServiceImplTest : CustomBehaviorSpec({

    listeners(EmbeddedRedisTestListener())

    val v1AccountTransactionService = dependencies.v1AccountTransactionService

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture

    val v1TransactionFindService = dependencies.v1TransactionFindService

    given("외화 예치금 계좌 'MTO 펀딩' 수기 출금 거래 요청되어") {
        val baseAccount = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT, acquirerName = "외화 예치금 계좌"))
        val targetAccount = saveAccount(v1AccountFixture.make(acquirerType = MTO_FUNDING, acquirerId = "MTO 펀딩 계좌"))
        val transaction = v1TransactionFixture.manualWithdrawalTransaction(
            baseAcquirer = baseAccount.acquirer,
            targetAcquirer = targetAccount.acquirer,
            purpose = TransactionPurpose.FUNDING,
            amount = BigDecimal(1000),
            exchangeRate = BigDecimal(1000),
        )

        `when`("처리 결과 성공인 경우") {
            val result = v1AccountTransactionService.manualDeposit(transaction)

            then("수기 출금 거래 요청 처리 결과 'COMPLETED' 정상 확인한다") {
                result.status shouldBe TransactionStatus.COMPLETED
            }

            then("외화 예치금 계좌 기준 'FUNDING' 출금 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(
                    trReferenceId = result.trReferenceId,
                    baseAcquirer = baseAccount.acquirer.toPredicate(),
                    targetAcquirer = targetAccount.acquirer.toPredicate(),
                    type = TransactionType.WITHDRAWAL,
                    purpose = TransactionPurpose.FUNDING,
                )
                val entity = v1TransactionFindService.findByPredicate(predicate)

                entity!! shouldNotBe null
                entity.status shouldBe TransactionStatus.COMPLETED
            }

            then("MTO 펀딩 계좌 기준 'FUNDING' 입금 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(
                    trReferenceId = result.trReferenceId,
                    baseAcquirer = targetAccount.acquirer.toPredicate(),
                    targetAcquirer = baseAccount.acquirer.toPredicate(),
                    type = TransactionType.DEPOSIT,
                    purpose = TransactionPurpose.FUNDING,
                )
                val entity = v1TransactionFindService.findByPredicate(predicate)

                entity!! shouldNotBe null
                entity.status shouldBe TransactionStatus.COMPLETED
            }
        }
    }

    given("외화 계좌 'MTO 펀딩 출금' 거래 내역의 역거래 생성 요청되어") {
        val baseAccount = saveAccount(v1AccountFixture.make(acquirerType = FX_DEPOSIT, acquirerName = "외화 예치금 계좌"))
        val targetAccount = saveAccount(v1AccountFixture.make(acquirerType = MTO_FUNDING, acquirerId = "MTO 펀딩 계좌"))
        val transaction = v1TransactionFixture.manualWithdrawalTransaction(
            baseAcquirer = baseAccount.acquirer,
            targetAcquirer = targetAccount.acquirer,
            purpose = TransactionPurpose.FUNDING,
            amount = BigDecimal(1000),
            exchangeRate = BigDecimal(1000),
        )

        `when`("역거래 생성 처리 성공인 경우") {
            val result = v1AccountTransactionService.reverse(transaction)

            then("역거래 생성 요청 처리 결과 'COMPLETED' 정상 확인한다") {
                result.status shouldBe TransactionStatus.COMPLETED
            }

            then("MTO 펀딩 계좌 입금 내역 정보 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(
                    trReferenceId = result.trReferenceId,
                    baseAcquirer = targetAccount.acquirer.toPredicate(),
                    targetAcquirer = baseAccount.acquirer.toPredicate(),
                    type = TransactionType.DEPOSIT,
                    purpose = TransactionPurpose.FUNDING,
                )
                val entity = v1TransactionFindService.findByPredicate(predicate)

                entity!! shouldNotBe null
                entity.status shouldBe TransactionStatus.COMPLETED
            }
        }
    }

})