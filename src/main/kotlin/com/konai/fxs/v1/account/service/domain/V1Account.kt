package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AccountStatus.ACTIVE
import com.konai.fxs.common.enumerate.AccountStatus.DELETED
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.ifNull
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import java.math.BigDecimal

data class V1Account(
    val id: Long? = null,
    val acquirer: V1Acquirer,
    val currency: String,
    val balance: BigDecimal = BigDecimal.ZERO,
    val minRequiredBalance: BigDecimal,
    val averageExchangeRate: BigDecimal,
    val status: AccountStatus = AccountStatus.ACTIVE
) {

    data class V1Acquirer(
        val id: String,
        val type: AcquirerType,
        val name: String = EMPTY
    )

    fun checkDuplicatedAcquirer(isExistsByAcquirer: (V1AcquirerPredicate, Long?) -> Boolean): V1Account {
        return if (status != DELETED && isExistsByAcquirer(V1AcquirerPredicate(acquirer), id)) {
            // `status != DELETED` 이면서, `acquirer` & `id` 기준 동일한 정보가 이미 있는 경우, 예외 처리
            throw InternalServiceException(ErrorCode.ACCOUNT_ACQUIRER_IS_DUPLICATED)
        } else {
            this
        }
    }

    fun checkCanBeUpdated(): V1Account {
        if (this.status == DELETED) {
            throw InternalServiceException(ErrorCode.ACCOUNT_STATUS_IS_DELETED)
        }
        return this
    }

    fun checkStatusIsActive(): V1Account {
        if (this.status != ACTIVE) {
            throw InternalServiceException(ErrorCode.ACCOUNT_STATUS_IS_INVALID)
        }
        return this
    }

    fun update(predicate: V1AccountPredicate): V1Account {
        return this.copy(
            acquirer           = predicate.acquirer?.toDomain().ifNull(acquirer),
            currency           = predicate.currency.ifNull(currency),
            balance            = predicate.balance.ifNull(balance),
            minRequiredBalance = predicate.minRequiredBalance.ifNull(minRequiredBalance),
            status             = predicate.status.ifNull(status)
        )
    }

}