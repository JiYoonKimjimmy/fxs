package com.konai.fxs.v1.transaction.repository.cache

import com.konai.fxs.common.enumerate.AcquirerType

interface TransactionCacheRepository {

    fun findWithdrawalReadyTotalAmountCache(acquirerId: String, acquirerType: AcquirerType): Number?

    fun incrementWithdrawalReadyTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun decrementWithdrawalReadyTotalAmountCache(acquirerId: String, acquirerType: AcquirerType, amount: Long): Number?

    fun clearWithdrawalReadyTotalAmountCache(acquirerId: String, acquirerType: AcquirerType)

}