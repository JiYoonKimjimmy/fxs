package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.enumerate.AcquirerType
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

    data class V1Acquirer(
        val id: String,
        val type: AcquirerType,
        val name: String
    )

    fun checkDuplicatedAcquirer(function: (V1Acquirer, Long?) -> Boolean): V1Account {
        return if (function(this.acquirer, this.id)) {
            throw InternalServiceException(ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED)
        } else {
            this
        }
    }

    fun update(predicate: V1AccountPredicate): V1Account {
        return this.copy(
            acquirer = predicate.acquirer ?: this.acquirer,
            currency = predicate.currency ?: this.currency,
            balance = predicate.balance ?: this.balance,
            minRequiredBalance = predicate.minRequiredBalance ?: this.minRequiredBalance
        )
    }

}