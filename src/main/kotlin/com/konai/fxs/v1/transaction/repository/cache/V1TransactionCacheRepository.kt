package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionChannel

interface V1TransactionCacheRepository {

    fun saveWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel, transactionId: Long)

    fun findWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Long?

    fun hasWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean

    fun deleteWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel)

    fun findWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearWithdrawalTransactionPendingAmountCache(acquirerId: String, acquirerType: AcquirerType)

}