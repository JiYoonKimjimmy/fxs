package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.TransactionChannel

interface V1TransactionCacheRepository {

    fun savePreparedWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel, transactionId: Long)

    fun findPreparedWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Long?

    fun hasPreparedWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean

    fun deletePreparedWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel)

    fun findPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearPreparedWithdrawalTotalAmountCache(acquirerId: String, acquirerType: AcquirerType)

}