package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
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
    private val distributedLockManager: DistributedLockManager
) : V1TransactionWithdrawalService {

    @Transactional
    override fun withdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 잔액 출금 처리
         * 3. 외화 계좌 출금 완료 거래 내역 생성
         * 4. 외화 계좌 출금 완료 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .withdrawalTransaction()
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun pending(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 대기 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 출금 거래 `PENDING` 상태 변경
         * 3. 외화 계좌 출금 대기 거래 Cache 저장
         * 4. 외화 계좌 출금 대기 거래 합계 Cache 증액 업데이트
         * 5. 외화 계좌 출금 거래 대기 내역 저장 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .changeStatusToPending()
            .cachingProcessPending()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun complete(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        /**
         * 외화 계좌 출금 완료 처리
         * 1. 외화 계좌 출금 거래 정보 조회
         * 2. 외화 계좌 출금 처리
         * 3. 외화 계좌 출금 대기 거래 Cache 정보 삭제
         * 4. 외화 계좌 출금 대기 거래 금액 Cache 감액 업데이트
         */
        return findWithdrawalTransaction(trReferenceId, channel)
            .let(this::withdrawal)
            .cachingProcessCompleted()
    }

    @Transactional
    override fun cancel(
        trReferenceId: String,
        orgTrReferenceId: String,
        channel: TransactionChannel,
        canceledTransactionId: () -> Long
    ): V1Transaction {
        /**
         * 외화 계좌 출금 취소 처리
         * 1. 외화 계좌 출금 완료 거래 정보 조회
         * 2. 외화 계좌 상태 확인
         * 3. 외화 계좌 잔액 입금 처리
         * 4. 외화 계좌 출금 완료 거래 `CANCELED` 상태 변경
         * 5. 외화 계좌 출금 완료 거래 내역 저장 Event 발행
         * 6. 외화 계좌 출금 취소 거래 생성
         * 7. 외화 계좌 출금 취소 거래 내역 저장 Event 발행
         */
        return findCompletedWithdrawalTransaction(orgTrReferenceId, channel)
            .checkAccountStatus(v1AccountValidationService::checkStatus)
            .withdrawalCancelTransaction()
            .changeStatusToCanceled()
            .publishSaveTransactionEvent()
            .generateCanceledTransaction(trReferenceId, canceledTransactionId)
    }

    private fun V1Transaction.generateCanceledTransaction(trReferenceId: String, canceledTransactionId: () -> Long): V1Transaction {
        return this
            .toCanceled(trReferenceId)
            .applyTransactionId(canceledTransactionId)
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.withdrawalTransaction(): V1Transaction {
        // 외화 계좌 잔액 출금 처리
        distributedLockManager.accountLock(this.account) {
            this.account.withdrawal(this.amount).let(v1AccountSaveService::save)
        }
        return this
    }

    private fun V1Transaction.withdrawalCancelTransaction(): V1Transaction {
        // 외화 계좌 잔액 입금 처리
        distributedLockManager.accountLock(this.account) {
            this.account.deposit(this.amount, this.account.averageExchangeRate).let(v1AccountSaveService::save)
        }
        return this
    }

    private fun V1Transaction.cachingProcessPending(): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 생성
            launch(Dispatchers.IO) { this@cachingProcessPending.saveWithdrawalTransaction() }
            // 출금 거래 대기 금액 Cache 증액 업데이트
            launch(Dispatchers.IO) { this@cachingProcessPending.incrementWithdrawalTransactionPendingAmount() }
        }
        return this
    }

    private suspend fun V1Transaction.saveWithdrawalTransaction() {
        v1TransactionCacheService.saveWithdrawalTransactionCache(this)
    }

    private suspend fun V1Transaction.incrementWithdrawalTransactionPendingAmount() {
        distributedLockManager.withdrawalTransactionAmountLick(this.account) {
            v1TransactionCacheService.incrementWithdrawalTransactionPendingAmountCache(this.baseAcquirer, this.amount)
        }
    }

    private fun V1Transaction.cachingProcessCompleted(): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 삭제
            launch(Dispatchers.IO) { this@cachingProcessCompleted.deleteWithdrawalTransaction() }
            // 출금 거래 대기 금액 Cache 감액 업데이트
            launch(Dispatchers.IO) { this@cachingProcessCompleted.decrementWithdrawalTransactionPendingAmount() }
        }
        return this
    }

    private suspend fun V1Transaction.deleteWithdrawalTransaction() {
        v1TransactionCacheService.deleteWithdrawalTransactionCache(this.trReferenceId, this.channel)
    }

    private suspend fun V1Transaction.decrementWithdrawalTransactionPendingAmount() {
        distributedLockManager.withdrawalTransactionAmountLick(this.account) {
            v1TransactionCacheService.decrementWithdrawalTransactionPendingAmountCache(this.baseAcquirer, this.amount)
        }
    }

    private fun findWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        // 출금 거래 Cache 정보 조회
        return v1TransactionCacheService.findWithdrawalTransactionCache(trReferenceId, channel)
            // 외화 계좌 거래 내역 DB 조회
            ?.let { v1TransactionFindService.findByPredicate(V1TransactionPredicate(id = it)) }
            ?: throw ResourceNotFoundException(ErrorCode.WITHDRAWAL_TRANSACTION_NOT_FOUND)
    }

    private fun findCompletedWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        return V1TransactionPredicate(trReferenceId = trReferenceId, channel = channel, status = COMPLETED)
            .let { v1TransactionFindService.findByPredicate(it) }
            ?: throw ResourceNotFoundException(ErrorCode.WITHDRAWAL_COMPLETED_TRANSACTION_NOT_FOUND)
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        return this.also(v1TransactionEventPublisher::saveTransaction)
    }

}