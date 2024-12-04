package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.repository.cache.V1TransactionCacheRepository
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class V1TransactionCacheServiceImpl(
    private val v1TransactionCacheRepository: V1TransactionCacheRepository
) : V1TransactionCacheService {

    override fun findPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer): BigDecimal {
        return v1TransactionCacheRepository.findPreparedWithdrawalTotalAmountCache(acquirer.id, acquirer.type)
            ?.let { BigDecimal(it.toLong()) }
            ?: BigDecimal.ZERO
    }

    override fun incrementPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal {
        return v1TransactionCacheRepository.incrementPreparedWithdrawalTotalAmountCache(acquirer.id, acquirer.type, amount.toLong())
            ?.let { BigDecimal(it.toLong()) }
            ?: throw InternalServiceException(ErrorCode.CACHE_SERVICE_ERROR)
    }

    override fun decrementPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal {
        return v1TransactionCacheRepository.decrementPreparedWithdrawalTotalAmountCache(acquirer.id, acquirer.type, amount.toLong())
            ?.let { BigDecimal(it.toLong()) }
            ?: throw InternalServiceException(ErrorCode.CACHE_SERVICE_ERROR)
    }

    override fun savePreparedWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction {
        v1TransactionCacheRepository.savePreparedWithdrawalTransactionCache(
            acquirerId = transaction.acquirer.id,
            acquirerType = transaction.acquirer.type,
            trReferenceId = transaction.trReferenceId,
            transactionId = transaction.id!!
        )
        return transaction
    }

    override fun hasPreparedWithdrawalTransactionCache(acquirer: V1Acquirer, trReferenceId: String): Boolean {
        return v1TransactionCacheRepository.hasPreparedWithdrawalTransactionCache(acquirer.id, acquirer.type, trReferenceId)
    }

    override fun deletePreparedWithdrawalTransactionCache(acquirer: V1Acquirer, trReferenceId: String) {
        v1TransactionCacheRepository.deletePreparedWithdrawalTransactionCache(acquirer.id, acquirer.type, trReferenceId)
    }

}