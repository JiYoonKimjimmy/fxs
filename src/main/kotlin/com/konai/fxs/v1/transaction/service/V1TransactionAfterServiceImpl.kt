package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventPublisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class V1TransactionAfterServiceImpl(
    private val v1TransactionCacheService: V1TransactionCacheService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
    private val distributedLockManager: DistributedLockManager,
) : V1TransactionAfterService {

    override fun cachingPendingTransaction(transaction: V1Transaction): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 생성
            launch(Dispatchers.IO) { saveWithdrawalTransaction(transaction) }
            // 출금 거래 대기 금액 Cache 증액 업데이트
            launch(Dispatchers.IO) { incrementWithdrawalTransactionPendingAmount(transaction) }
        }
        return transaction
    }

    override fun cachingCompletedTransaction(transaction: V1Transaction): V1Transaction {
        runBlocking {
            // 출금 거래 Cache 삭제
            launch(Dispatchers.IO) { deleteWithdrawalTransaction(transaction) }
            // 출금 거래 대기 금액 Cache 감액 업데이트
            launch(Dispatchers.IO) { decrementWithdrawalTransactionPendingAmount(transaction) }
        }
        return transaction
    }

    override fun publishSaveTransaction(transaction: V1Transaction): V1Transaction {
        return transaction.also(v1TransactionEventPublisher::saveTransaction)
    }

    private suspend fun saveWithdrawalTransaction(transaction: V1Transaction) {
        v1TransactionCacheService.saveWithdrawalTransactionCache(transaction)
    }

    private suspend fun incrementWithdrawalTransactionPendingAmount(transaction: V1Transaction) {
        distributedLockManager.withdrawalTransactionAmountLock(transaction.account) {
            v1TransactionCacheService.incrementWithdrawalTransactionPendingAmountCache(transaction.baseAcquirer, transaction.amount)
        }
    }

    private suspend fun deleteWithdrawalTransaction(transaction: V1Transaction) {
        v1TransactionCacheService.deleteWithdrawalTransactionCache(transaction.trReferenceId, transaction.channel)
    }

    private suspend fun decrementWithdrawalTransactionPendingAmount(transaction: V1Transaction) {
        distributedLockManager.withdrawalTransactionAmountLock(transaction.account) {
            v1TransactionCacheService.decrementWithdrawalTransactionPendingAmountCache(transaction.baseAcquirer, transaction.amount)
        }
    }

}