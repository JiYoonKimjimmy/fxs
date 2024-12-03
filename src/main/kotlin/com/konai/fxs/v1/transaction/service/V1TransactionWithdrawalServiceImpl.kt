package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.sequence.service.V1SequenceGeneratorService
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
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
    private val v1TransactionCacheService: V1TransactionCacheService,
    private val v1SequenceGeneratorService: V1SequenceGeneratorService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
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
        // 외화 계좌 출금 한도 확인
        val account = transaction.checkAccountLimit(v1AccountValidationService::checkLimit)
        return transaction
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .withdrawal(account)
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun prepareWithdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 실시간 출금 준비 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 출금 준비 거래 내역 생성 Event 발행
         * 3. 외화 계좌 출금 준비 거래 Cache 저장
         * 3. 외화 계좌 출금 준비 거래 합계 Cache 업데이트
         */
        // 외화 계좌 출금 한도 확인
        transaction.checkAccountLimit(v1AccountValidationService::checkLimit)
        return transaction
            .applyTransactionId(v1SequenceGeneratorService::nextTransactionSequence)
            .changeStatusToPrepared()
            .preparedTransactionCacheProc()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.preparedTransactionCacheProc(): V1Transaction {
        return this.also {
            runBlocking {
                launch(Dispatchers.IO) { it.savePreparedTransactionCache() }
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

    private fun V1Transaction.withdrawal(account: V1Account): V1Transaction {
        // 외화 계좌 잔액 변경 처리
        distributedLockManager.accountLock(account) {
            account.withdrawal(this.amount).let(v1AccountSaveService::save)
        }
        return this
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        v1TransactionEventPublisher.saveTransaction(this)
        return this
    }

}