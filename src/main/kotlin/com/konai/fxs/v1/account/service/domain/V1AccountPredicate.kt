package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

class V1AccountPredicate(
    val id: Long? = null,
    val acquirer: V1Acquirer? = null,
    val currency: String? = null,
    val balance: BigDecimal? = null,
    val minRequiredBalance: BigDecimal? = null,
    val averageExchangeRate: BigDecimal? = null
)