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
    override fun prepareWithdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 준비 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 출금 준비 거래 Cache 저장
         * 3. 외화 계좌 출금 준비 거래 합계 Cache 증액 업데이트
         * 4. 외화 계좌 출금 준비 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .changeStatusToPrepared()
            .preparedTransactionCacheProc()
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
        return findPreparedTransactionCache(trReferenceId, channel)
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .withdrawalTransaction()
            .changeStatusToCompleted()
            .completedTransactionCacheProc()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.preparedTransactionCacheProc(): V1Transaction {
        return this.also {
            runBlocking {
                // 출금 준비 거래 Cache 생성
                launch(Dispatchers.IO) { it.savePreparedTransactionCache() }
                // 출금 준비 거래 금액 합계 Cache 증액 업데이트
                launch(Dispatchers.IO) { it.incrementPreparedTransactionAmountCache() }
            }
        }
    }

    private suspend fun V1Transaction.savePreparedTransactionCache(): V1Transaction {
        return v1TransactionCacheService.savePreparedWithdrawalTransactionCache(this)
    }

    private suspend fun V1Transaction.incrementPreparedTransactionAmountCache(): V1Transaction {
        v1TransactionCacheService.incrementPreparedWithdrawalTotalAmountCache(this.acquirer, this.amount)
        return this
    }

    private fun V1Transaction.completedTransactionCacheProc(): V1Transaction {
        return this.also {
            runBlocking {
                // 출금 준비 거래 Cache 삭제
                launch(Dispatchers.IO) { it.deletePreparedTransactionCache() }
                // 출금 준비 거래 금액 합계 Cache 감액 업데이트
                launch(Dispatchers.IO) { it.decrementPreparedTransactionAmountCache() }
            }
        }
    }

    private suspend fun V1Transaction.deletePreparedTransactionCache(): V1Transaction {
        v1TransactionCacheService.deletePreparedWithdrawalTransactionCache(this.trReferenceId, this.channel)
        return this
    }

    private suspend fun V1Transaction.decrementPreparedTransactionAmountCache(): V1Transaction {
        v1TransactionCacheService.decrementPreparedWithdrawalTotalAmountCache(this.acquirer, this.amount)
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

    private fun findPreparedTransactionCache(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        // 출금 준비 거래 Cache 정보 조회
        return v1TransactionCacheService.findPreparedWithdrawalTransactionCache(trReferenceId, channel)
            // 출금 준비 거래 Cache 정보 기준 외화 계좌 거래 내역 조회
            ?.let { v1TransactionFindService.findByPredicate(V1TransactionPredicate(id = it)) }
            ?: throw InternalServiceException(ErrorCode.WITHDRAWAL_PREPARED_TRANSACTION_NOT_FOUND)
    }

}