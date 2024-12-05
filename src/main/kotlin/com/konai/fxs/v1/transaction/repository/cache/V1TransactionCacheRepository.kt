package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionChannel

interface V1TransactionCacheRepository {

    fun savePendingTransactionCache(trReferenceId: String, channel: TransactionChannel, transactionId: Long)

    fun findPendingTransactionCache(trReferenceId: String, channel: TransactionChannel): Long?

    fun hasPendingTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean

    fun deletePendingTransactionCache(trReferenceId: String, channel: TransactionChannel)

    fun findPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearPendingTransactionAmountCache(acquirerId: String, acquirerType: AcquirerType)

}