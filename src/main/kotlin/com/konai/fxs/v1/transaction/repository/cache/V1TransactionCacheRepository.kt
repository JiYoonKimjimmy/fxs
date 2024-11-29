package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType

interface V1TransactionCacheRepository {

    fun findPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType)

    fun savePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String, transactionId: Long)

    fun hasPreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String): Boolean

    fun deletePreparedWithdrawalTransactionCache(acquirerId: String, acquirerType: AcquirerType, trReferenceId: String)

}