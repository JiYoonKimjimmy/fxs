package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.repository.cache.TransactionCacheRepository
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TransactionCacheServiceImpl(
    private val transactionCacheRepository: TransactionCacheRepository
) : TransactionCacheService {

    override fun findPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer): BigDecimal {
        return transactionCacheRepository.findPreparedWithdrawalTotalAmountCache(acquirer.id, acquirer.type)
            ?.let { BigDecimal(it.toLong()) }
            ?: BigDecimal.ZERO
    }

    override fun savePreparedWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction {
        transactionCacheRepository.savePreparedWithdrawalTransactionCache(
            acquirerId = transaction.acquirer.id,
            acquirerType = transaction.acquirer.type,
            trReferenceId = transaction.trReferenceId,
            transactionId = transaction.id!!
        )
        return transaction
    }

    override fun hasPreparedWithdrawalTransactionCache(transaction: V1Transaction): Boolean {
        return transactionCacheRepository.hasPreparedWithdrawalTransactionCache(
            acquirerId = transaction.acquirer.id,
            acquirerType = transaction.acquirer.type,
            trReferenceId = transaction.trReferenceId
        )
    }

    override fun deletePreparedWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction {
        transactionCacheRepository.deletePreparedWithdrawalTransactionCache(
            acquirerId = transaction.acquirer.id,
            acquirerType = transaction.acquirer.type,
            trReferenceId = transaction.trReferenceId
        )
        return transaction
    }

}