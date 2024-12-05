package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.common.enumerate.TransactionChannel
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

    override fun saveWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction {
        v1TransactionCacheRepository.saveWithdrawalTransactionCache(
            trReferenceId = transaction.trReferenceId,
            channel = transaction.channel,
            transactionId = transaction.id!!
        )
        return transaction
    }

    override fun findWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Long? {
        return v1TransactionCacheRepository.findWithdrawalTransactionCache(trReferenceId, channel)
    }

    override fun deleteWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel) {
        v1TransactionCacheRepository.deleteWithdrawalTransactionCache(trReferenceId, channel)
    }

    override fun findWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer): BigDecimal {
        return v1TransactionCacheRepository.findWithdrawalTransactionPendingAmountCache(acquirer.id, acquirer.type)
            ?.let { BigDecimal(it.toLong()) }
            ?: BigDecimal.ZERO
    }

    override fun incrementWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal {
        return v1TransactionCacheRepository.incrementWithdrawalTransactionPendingAmountCache(acquirer.id, acquirer.type, amount.toLong())
            ?.let { BigDecimal(it.toLong()) }
            ?: throw InternalServiceException(ErrorCode.CACHE_SERVICE_ERROR)
    }

    override fun decrementWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal {
        return v1TransactionCacheRepository.decrementWithdrawalTransactionPendingAmountCache(acquirer.id, acquirer.type, amount.toLong())
            ?.let { BigDecimal(it.toLong()) }
            ?: throw InternalServiceException(ErrorCode.CACHE_SERVICE_ERROR)
    }

}