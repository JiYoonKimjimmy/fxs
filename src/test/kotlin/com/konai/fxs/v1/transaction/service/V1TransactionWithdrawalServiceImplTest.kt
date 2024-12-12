package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_CACHE
import com.konai.fxs.common.enumerate.TransactionCacheType.WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionChannel.ORS
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionStatus.CANCELED
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.testsupport.CustomBehaviorSpec
import com.konai.fxs.testsupport.TestCommonFunctions.saveAccount
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
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
    val v1TransactionCacheService = dependencies.v1TransactionCacheService
    val v1SequenceGeneratorService = dependencies.v1SequenceGeneratorService

    val v1AccountFindService = dependencies.v1AccountFindService
    val v1TransactionFindService = dependencies.v1TransactionFindService

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture

    val numberRedisTemplate = dependencies.numberRedisTemplate

    given("외화 계좌 수기 출금 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence())
        val transaction = v1TransactionFixture.manualWithdrawalTransaction(
            baseAcquirer = account.acquirer,
            amount = BigDecimal(100),
            exchangeRate = BigDecimal(1000.00)
        )

        `when`("'baseAcquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionWithdrawalService.withdrawal(transaction) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        // 외화 계좌 정보 저장
        saveAccount(account)

        `when`("'amount' 요청 금액보다 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.withdrawal(transaction) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 0 < (pendingAmount: 0 + amount: 100)"
            }
        }

        // 외화 계좌 정보 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        `when`("정상 'account' 수기 출금 요청인 경우") {
            val result = v1TransactionWithdrawalService.withdrawal(transaction)

            then("수기 출금 거래 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe COMPLETED
            }

            then("외화 계좌 잔액 감액 변경 정상 확인한다") {
                val accountEntity = v1AccountFindService.findByPredicate(V1AccountPredicate(id = account.id))!!
                accountEntity.balance shouldBe BigDecimal(0)
            }

            then("외화 계좌 수기 출금 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val transactionEntity = v1TransactionFindService.findByPredicate(predicate)

                transactionEntity!! shouldNotBe null
                transactionEntity.id shouldBe result.id
                transactionEntity.channel shouldBe TransactionChannel.PORTAL
                transactionEntity.baseAcquirer.id shouldBe account.acquirer.id
                transactionEntity.type shouldBe TransactionType.WITHDRAWAL
                transactionEntity.purpose shouldBe TransactionPurpose.WITHDRAWAL
                transactionEntity.currency shouldBe Currency.USD
                transactionEntity.amount shouldBe BigDecimal(100)
                transactionEntity.exchangeRate shouldBe BigDecimal(1000)
                transactionEntity.transferDate shouldBe transaction.transferDate
                transactionEntity.status shouldBe COMPLETED
            }
        }
    }
    
    given("외화 계좌 출금 대기 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence())
        val transaction = v1TransactionFixture.withdrawalTransaction(
            baseAcquirer = account.acquirer,
            amount = BigDecimal(100)
        )

        `when`("'baseAcquirer' 요청 정보 기준 외화 계좌 정보 존재하지 않는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionWithdrawalService.pending(transaction) }

            then("'ACCOUNT_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_NOT_FOUND
            }
        }

        // 외화 계좌 정보 저장
        saveAccount(account)

        `when`("'amount' 요청 금액보다 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.pending(transaction) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 0 < (pendingAmount: 0 + amount: 100)"
            }
        }

        // 외화 계좌 정보 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        `when`("정상 'account' 출금 요청인 경우") {
            val result = v1TransactionWithdrawalService.pending(transaction)

            then("출금 거래 상태 'PENDING' 정상 확인한다") {
                result.status shouldBe TransactionStatus.PENDING
            }

            then("외화 계좌 출금 대기 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val transactionEntity = v1TransactionFindService.findByPredicate(predicate)

                transactionEntity!! shouldNotBe null
                transactionEntity.id shouldBe result.id
                transactionEntity.channel shouldBe ORS
                transactionEntity.baseAcquirer.id shouldBe account.acquirer.id
                transactionEntity.type shouldBe TransactionType.WITHDRAWAL
                transactionEntity.purpose shouldBe TransactionPurpose.REMITTANCE
                transactionEntity.currency shouldBe Currency.USD
                transactionEntity.amount shouldBe BigDecimal(100)
                transactionEntity.exchangeRate shouldBe BigDecimal(1000)
                transactionEntity.transferDate shouldBe transaction.transferDate
                transactionEntity.status shouldBe TransactionStatus.PENDING
            }

            then("'출금 대기 거래 Cache 정보' 저장 정상 확인한다") {
                val trReferenceId = transaction.trReferenceId
                val channel = transaction.channel.name
                val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel)

                numberRedisTemplate.opsForValue().get(key) shouldBe result.id
            }

            then("'출금 대기 거래 금액 Cache 정보' 업데이트 정상 확인한다") {
                val acquirer = transaction.baseAcquirer
                val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirer.id, acquirer.type.name)

                numberRedisTemplate.opsForValue().get(key) shouldBe transaction.amount.toLong()
            }

        }
    }

    given("외화 계좌 출금 완료 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence())
        val baseAcquirer = account.acquirer
        val trReferenceId = generateUUID()
        val channel = ORS

        `when`("요청 'trReferenceId' 기준 출금 거래 정보 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> { v1TransactionWithdrawalService.complete(trReferenceId, channel) }

            then("'WITHDRAWAL_TRANSACTION_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.WITHDRAWAL_TRANSACTION_NOT_FOUND
            }
        }

        // 외화 계좌 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        // 출금 거래 정보 저장
        val transaction = v1TransactionFixture.withdrawalTransaction(baseAcquirer, trReferenceId, BigDecimal(100))
        v1TransactionWithdrawalService.pending(transaction)

        // 출금 거래 대기 금액 추가분 증액
        v1TransactionCacheService.incrementWithdrawalTransactionPendingAmountCache(baseAcquirer, BigDecimal(100))

        `when`("요청 'amount' 금액보다 외화 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.complete(trReferenceId, channel) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 100 < (pendingAmount: 200 + amount: 0)"
            }
        }

        // 외화 계좌 잔액 업데이트
        saveAccount(account, balance = BigDecimal(1000), averageExchangeRate = BigDecimal(1300.00))

        `when`("정상 'account' 출금 완료 요청인 경우") {
            val result = v1TransactionWithdrawalService.complete(trReferenceId, channel)

            then("출금 거래 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe COMPLETED
            }

            then("외화 계좌 출금 거래 내역 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val transactionEntity = v1TransactionFindService.findByPredicate(predicate)

                transactionEntity!! shouldNotBe null
                transactionEntity.id shouldBe result.id
                transactionEntity.channel shouldBe ORS
                transactionEntity.baseAcquirer.id shouldBe account.acquirer.id
                transactionEntity.type shouldBe TransactionType.WITHDRAWAL
                transactionEntity.purpose shouldBe TransactionPurpose.REMITTANCE
                transactionEntity.currency shouldBe Currency.USD
                transactionEntity.amount shouldBe BigDecimal(100)
                transactionEntity.exchangeRate shouldBe BigDecimal(1000)
                transactionEntity.transferDate shouldBe transaction.transferDate
                transactionEntity.status shouldBe COMPLETED
            }

            then("'출금 거래 Cache' 삭제 정상 확인한다") {
                v1TransactionCacheService.findWithdrawalTransactionCache(trReferenceId, channel) shouldBe null
            }

            then("'출금 거래 대기 금액 Cache' 감액 처리 정상 확인한다") {
                v1TransactionCacheService.findWithdrawalTransactionPendingAmountCache(baseAcquirer) shouldBe BigDecimal(100)
            }
        }
    }
    
    given("외화 계좌 출금 취소 거래 요청되어") {
        val canceledTransactionId = v1SequenceGeneratorService::nextTransactionSequence

        `when`("'orgTrReferenceId' 요청 정보 기준 출금 완료 거래 없는 경우") {
            val exception = shouldThrow<ResourceNotFoundException> {
                v1TransactionWithdrawalService.cancel(generateUUID(), generateUUID(), ORS, canceledTransactionId)
            }
            
            then("'WITHDRAWAL_COMPLETED_TRANSACTION_NOT_FOUND' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.WITHDRAWAL_COMPLETED_TRANSACTION_NOT_FOUND
            }
        }

        // 외화 계좌 정보 저장
        val account = v1AccountFixture.make(id = generateSequence(), balance = 1000)
        saveAccount(account)

        // 출금 거래 정보 저장
        val transaction = v1TransactionFixture.withdrawalTransaction(account.acquirer, generateUUID(), BigDecimal(100))
        v1TransactionWithdrawalService.pending(transaction)

        // 출금 거래 완료 처리
        v1TransactionWithdrawalService.complete(transaction.trReferenceId, transaction.channel)

        val trReferenceId = generateUUID()
        val orgTrReferenceId = transaction.trReferenceId
        val channel = transaction.channel

        `when`("정상 출금 취소 거래 요청인 경우") {
            val result = v1TransactionWithdrawalService.cancel(trReferenceId, orgTrReferenceId, channel, canceledTransactionId)

            then("출금 취소 거래 완료 처리 정상 확인한다") {
                result.trReferenceId shouldBe trReferenceId
                result.orgTrReferenceId shouldBe orgTrReferenceId
                result.status shouldBe COMPLETED
            }

            then("외화 계좌 잔액 변경 정상 확인한다") {
                val entity = v1AccountFindService.findByPredicate(V1AccountPredicate(account.id))!!
                entity.balance shouldBe BigDecimal(1000)
            }

            then("출금 완료 거래 'CANCELED' 상태 변경 정상 확인한다") {
                val entity = v1TransactionFindService.findByPredicate(V1TransactionPredicate(trReferenceId = orgTrReferenceId))!!
                entity.status shouldBe CANCELED
                entity.cancelDate shouldNotBe null
            }

            then("출금 취소 거래 내역 생성 정상 확인한다") {
                val entity = v1TransactionFindService.findByPredicate(V1TransactionPredicate(trReferenceId = trReferenceId))!!
                entity.status shouldBe COMPLETED
                entity.orgTrReferenceId shouldBe orgTrReferenceId
            }
        }
    }

})