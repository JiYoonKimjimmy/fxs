package com.konai.fxs.v1.transaction.service.cache

import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

interface TransactionCacheService {

    fun findPreparedWithdrawalTotalAmountCache(acquirer: V1Acquirer): BigDecimal

}