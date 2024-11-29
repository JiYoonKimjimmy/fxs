package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import java.math.BigDecimal

interface V1TransactionCacheService {

    fun findPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer): BigDecimal

    fun incrementPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer, amount: BigDecimal): BigDecimal

    fun savePreparedWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction

    fun hasPreparedWithdrawalTransactionCache(transaction: V1Transaction): Boolean

    fun deletePreparedWithdrawalTransactionCache(transaction: V1Transaction): V1Transaction

}