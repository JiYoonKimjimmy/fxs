package com.konai.fxs.v1.account.service.domain

import java.math.BigDecimal

data class V1Account(
    val id: Long? = null,
    val acquirer: V1Acquirer,
    val currency: String,
    var balance: BigDecimal = BigDecimal.ZERO,
    val minRequiredBalance: BigDecimal,
    val averageExchangeRate: BigDecimal
)