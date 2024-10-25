package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import java.math.BigDecimal

data class V1Account(
    val id: Long? = null,
    val acquirer: V1Acquirer,
    val currency: String,
    val balance: BigDecimal = BigDecimal.ZERO,
    val minRequiredBalance: BigDecimal,
    val averageExchangeRate: BigDecimal
) {

    fun checkDuplicatedAcquirer(function: (V1Acquirer) -> Boolean): V1Account {
        return if (function(this.acquirer)) {
            throw InternalServiceException(ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED)
        } else {
            this
        }
    }
}