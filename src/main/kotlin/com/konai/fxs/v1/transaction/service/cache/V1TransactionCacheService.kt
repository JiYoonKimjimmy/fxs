package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import java.math.BigDecimal

interface V1TransactionCacheService {

    fun saveWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction

    fun findWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Long?

    fun hasWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel): Boolean

    fun deleteWithdrawalTransactionCache(trReferenceId: String, channel: TransactionChannel)

    fun findWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer): BigDecimal

    fun incrementWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal

    fun decrementWithdrawalTransactionPendingAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal

}