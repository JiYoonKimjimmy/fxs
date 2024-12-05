package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.sequence.service.V1SequenceGeneratorService
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventPublisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1TransactionWithdrawalServiceImpl(
    private val v1AccountValidationService: V1AccountValidationService,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1TransactionFindService: V1TransactionFindService,
    private val v1TransactionCacheService: V1TransactionCacheService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
    private val v1SequenceGeneratorService: V1SequenceGeneratorService,
    private val distributedLockManager: DistributedLockManager
) : V1TransactionWithdrawalService {

    @Transactional
    override fun manualWithdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 수기 출금 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 잔액 변경 처리
         * 3. 외화 계좌 출금 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .withdrawalTransaction()
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun withdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 출금 거래 Cache 저장
         * 3. 외화 계좌 출금 거래 합계 Cache 증액 업데이트
         * 4. 외화 계좌 출금 거래 `PENDING` 상태 변경
         * 4. 외화 계좌 출금 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .changeStatusToPending()
            .withdrawalTransactionCacheProc()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun completeWithdrawal(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        /**
         * 외화 계좌 출금 완료 처리
         * 1. 외화 계좌 출금 준비 거래 Cache 정보 조회
         * 2. 외화 계촤 출금 한도 확인
         * 3. 외화 계좌 잔액 변경 처리
         * 4. 외화 계좌 출금 준비 거래 Cache 정보 삭제
         * 5. 외화 계좌 출금 준비 거래 합계 Cache 감액 업데이트
         * 6. 외화 계좌 출금 거래 내역 생성 Event 발행
         */
        return findWithdrawalTransaction(trReferenceId, channel)
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .withdrawalTransaction()
            .changeStatusToCompleted()
            .completedWithdrawalTransactionCacheProc()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.withdrawalTransactionCacheProc(): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 생성
            launch(Dispatchers.IO) { this@withdrawalTransactionCacheProc.saveWithdrawalTransactionCache() }
            // 출금 거래 대기 금액 Cache 증액 업데이트
            launch(Dispatchers.IO) { this@withdrawalTransactionCacheProc.incrementWithdrawalTransactionAmountCache() }
        }
        return this
    }

    private suspend fun V1Transaction.saveWithdrawalTransactionCache(): V1Transaction {
        return v1TransactionCacheService.saveWithdrawalTransactionCache(this)
    }

    private suspend fun V1Transaction.incrementWithdrawalTransactionAmountCache(): V1Transaction {
        distributedLockManager.prepareWithdrawalTransactionLick(this.account) {
            v1TransactionCacheService.incrementWithdrawalTransactionPendingAmountCache(this.acquirer, this.amount)
        }
        return this
    }

    private fun V1Transaction.completedWithdrawalTransactionCacheProc(): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 삭제
            launch(Dispatchers.IO) { this@completedWithdrawalTransactionCacheProc.deleteWithdrawalTransactionCache() }
            // 출금 거래 대기 금액 Cache 감액 업데이트
            launch(Dispatchers.IO) { this@completedWithdrawalTransactionCacheProc.decrementWithdrawalTransactionAmountCache() }
        }
        return this
    }

    private suspend fun V1Transaction.deleteWithdrawalTransactionCache(): V1Transaction {
        v1TransactionCacheService.deleteWithdrawalTransactionCache(this.trReferenceId, this.channel)
        return this
    }

    private suspend fun V1Transaction.decrementWithdrawalTransactionAmountCache(): V1Transaction {
        distributedLockManager.prepareWithdrawalTransactionLick(this.account) {
            v1TransactionCacheService.decrementWithdrawalTransactionPendingAmountCache(this.acquirer, this.amount)
        }
        return this
    }

    private fun V1Transaction.withdrawalTransaction(): V1Transaction {
        // 외화 계좌 잔액 변경 처리
        distributedLockManager.accountLock(this.account) {
            this.account.withdrawal(this.amount).let(v1AccountSaveService::save)
        }
        return this
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        v1TransactionEventPublisher.saveTransaction(this)
        return this
    }

    private fun findWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        // 출금 거래 Cache 정보 조회
        return v1TransactionCacheService.findWithdrawalTransactionCache(trReferenceId, channel)
            // 외화 계좌 거래 내역 DB 조회
            ?.let { v1TransactionFindService.findByPredicate(V1TransactionPredicate(id = it)) }
            ?: throw InternalServiceException(ErrorCode.WITHDRAWAL_TRANSACTION_NOT_FOUND)
    }

}