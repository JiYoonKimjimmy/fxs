package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AcquirerType.MTO_FUNDING
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
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXPIRE_TRANSACTION_DL_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQ.Exchange.V1_EXPIRE_TRANSACTION_EXCHANGE
import com.konai.fxs.testsupport.rabbitmq.MockRabbitMQTestListener
import com.konai.fxs.testsupport.redis.EmbeddedRedisTestListener
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal

class V1TransactionWithdrawalServiceImplTest : CustomBehaviorSpec({

    listeners(
        EmbeddedRedisTestListener(),
        MockRabbitMQTestListener(V1_EXPIRE_TRANSACTION_DL_EXCHANGE, V1_EXPIRE_TRANSACTION_EXCHANGE)
    )

    val v1TransactionWithdrawalService = dependencies.v1TransactionWithdrawalService
    val v1TransactionCacheService = dependencies.v1TransactionCacheService
    val v1SequenceGeneratorService = dependencies.v1SequenceGeneratorService

    val v1AccountFindService = dependencies.v1AccountFindService
    val v1TransactionFindService = dependencies.v1TransactionFindService

    val v1AccountFixture = dependencies.v1AccountFixture
    val v1TransactionFixture = dependencies.v1TransactionFixture

    val numberRedisTemplate = dependencies.numberRedisTemplate

    given("'외화 예치금 계좌' 수기 출금 거래 요청되어") {
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

        `when`("요청 'amount' 금액보다 'baseAcquirer' 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.withdrawal(transaction) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 0 < (pendingAmount: 0 + amount: 100)"
            }
        }

        // 외화 계좌 정보 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        `when`("수기 출금 거래 요청 처리 결과 성공인 경우") {
            val result = v1TransactionWithdrawalService.withdrawal(transaction)

            then("수기 출금 거래 결과 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe COMPLETED
            }

            then("'외화 예치금 계좌' 잔액 감액되어 '100 > 0' 변경 정상 확인한다") {
                val saved = v1AccountFindService.findByPredicate(V1AccountPredicate(id = account.id))!!
                saved.balance shouldBe BigDecimal(0)
            }

            then("수기 출금 거래 내역 정보 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val saved = v1TransactionFindService.findByPredicate(predicate)

                saved!! shouldNotBe null
                saved.id shouldBe result.id
                saved.channel shouldBe TransactionChannel.PORTAL
                saved.baseAcquirer.id shouldBe account.acquirer.id
                saved.type shouldBe TransactionType.WITHDRAWAL
                saved.purpose shouldBe TransactionPurpose.WITHDRAWAL
                saved.currency shouldBe Currency.USD
                saved.amount shouldBe BigDecimal(100)
                saved.beforeBalance shouldBe BigDecimal(100)
                saved.afterBalance shouldBe BigDecimal(0)
                saved.exchangeRate shouldBe BigDecimal(1000)
                saved.transferDate shouldBe transaction.transferDate
                saved.status shouldBe COMPLETED
            }
        }
    }
    
    given("'MTO 펀딩 계좌' 송금 목적 출금 대기 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence(), acquirerType = MTO_FUNDING, acquirerName = "MTO 펀딩 계좌")
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

        `when`("요청 'amount' 금액보다 'baseAcquirer' 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.pending(transaction) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 0 < (pendingAmount: 0 + amount: 100)"
            }
        }

        // 외화 계좌 정보 잔액 변경
        saveAccount(account, balance = BigDecimal(100), averageExchangeRate = BigDecimal(1300.00))

        `when`("출금 대기 거래 요청 처리 결과 성공인 경우") {
            val result = v1TransactionWithdrawalService.pending(transaction)

            then("출금 대기 거래 결과 상태 'PENDING' 정상 확인한다") {
                result.status shouldBe TransactionStatus.PENDING
            }

            then("출금 대기 거래 내역 정보 생성 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val saved = v1TransactionFindService.findByPredicate(predicate)

                saved!! shouldNotBe null
                saved.id shouldBe result.id
                saved.channel shouldBe ORS
                saved.baseAcquirer.id shouldBe account.acquirer.id
                saved.type shouldBe TransactionType.WITHDRAWAL
                saved.purpose shouldBe TransactionPurpose.REMITTANCE
                saved.currency shouldBe Currency.USD
                saved.amount shouldBe BigDecimal(100)
                saved.beforeBalance shouldBe BigDecimal(0)
                saved.afterBalance shouldBe BigDecimal(0)
                saved.exchangeRate shouldBe BigDecimal(1000)
                saved.transferDate shouldBe transaction.transferDate
                saved.status shouldBe TransactionStatus.PENDING
            }

            then("출금 대기 거래 Cache 정보 저장 정상 확인한다") {
                val trReferenceId = transaction.trReferenceId
                val channel = transaction.channel.name
                val key = WITHDRAWAL_TRANSACTION_CACHE.getKey(trReferenceId, channel)

                numberRedisTemplate.opsForValue().get(key) shouldBe result.id
            }

            then("출금 대기 거래 금액 Cache 정보 변경 정상 확인한다") {
                val acquirer = transaction.baseAcquirer
                val key = WITHDRAWAL_TRANSACTION_PENDING_AMOUNT_CACHE.getKey(acquirer.id, acquirer.type.name)

                numberRedisTemplate.opsForValue().get(key) shouldBe transaction.amount.toLong()
            }

        }
    }

    given("'MTO 펀딩 계좌' 송금 목적 출금 완료 거래 요청되어") {
        val account = v1AccountFixture.make(id = generateSequence(), acquirerType = MTO_FUNDING, acquirerName = "MTO 펀딩 계좌")
        val baseAcquirer = account.acquirer
        val trReferenceId = generateUUID()
        val channel = ORS

        `when`("요청 'trReferenceId' 기준 출금 대기 거래 정보 없는 경우") {
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

        `when`("요청 'amount' 금액보다 'baseAcquirer' 계좌 잔액 부족인 경우") {
            val exception = shouldThrow<InternalServiceException> { v1TransactionWithdrawalService.complete(trReferenceId, channel) }

            then("'ACCOUNT_BALANCE_IS_INSUFFICIENT' 예외 발생 정상 확인한다") {
                exception.errorCode shouldBe ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT
                exception.detailMessage shouldBe "balance: 100 < (pendingAmount: 200 + amount: 0)"
            }
        }

        // 외화 계좌 잔액 변경
        saveAccount(account, balance = BigDecimal(1000), averageExchangeRate = BigDecimal(1300.00))

        `when`("출금 완료 거래 처리 결과 성공인 경우") {
            val result = v1TransactionWithdrawalService.complete(trReferenceId, channel)

            then("출금 완료 거래 결과 상태 'COMPLETED' 정상 확인한다") {
                result.status shouldBe COMPLETED
            }

            then("출금 완료 거래 내역 정보 변경 정상 확인한다") {
                val predicate = V1TransactionPredicate(id = result.id)
                val saved = v1TransactionFindService.findByPredicate(predicate)

                saved!! shouldNotBe null
                saved.id shouldBe result.id
                saved.channel shouldBe ORS
                saved.baseAcquirer.id shouldBe account.acquirer.id
                saved.type shouldBe TransactionType.WITHDRAWAL
                saved.purpose shouldBe TransactionPurpose.REMITTANCE
                saved.currency shouldBe Currency.USD
                saved.amount shouldBe BigDecimal(100)
                saved.beforeBalance shouldBe BigDecimal(1000)
                saved.afterBalance shouldBe BigDecimal(900)
                saved.exchangeRate shouldBe BigDecimal(1000)
                saved.transferDate shouldBe transaction.transferDate
                saved.status shouldBe COMPLETED
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
                val saved = v1AccountFindService.findByPredicate(V1AccountPredicate(account.id))!!
                saved.balance shouldBe BigDecimal(1000)
            }

            then("출금 완료 거래 'CANCELED' 상태 변경 정상 확인한다") {
                val saved = v1TransactionFindService.findByPredicate(V1TransactionPredicate(trReferenceId = orgTrReferenceId))!!
                saved.status shouldBe CANCELED
                saved.beforeBalance shouldBe BigDecimal(1000)
                saved.afterBalance shouldBe BigDecimal(900)
                saved.cancelDate shouldNotBe null
            }

            then("출금 취소 거래 내역 생성 정상 확인한다") {
                val saved = v1TransactionFindService.findByPredicate(V1TransactionPredicate(trReferenceId = trReferenceId))!!
                saved.status shouldBe COMPLETED
                saved.beforeBalance shouldBe BigDecimal(900)
                saved.afterBalance shouldBe BigDecimal(1000)
                saved.orgTrReferenceId shouldBe orgTrReferenceId
            }
        }
    }

})