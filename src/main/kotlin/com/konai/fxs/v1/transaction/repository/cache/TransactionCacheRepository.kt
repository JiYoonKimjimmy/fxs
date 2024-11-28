package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType

interface TransactionCacheRepository {

    fun findWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearWithdrawalPreparedTotalAmountCache(acquirerId: String, acquirerType: AcquirerType)

}