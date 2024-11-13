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
    val depositQuantity: BigDecimal = BigDecimal.ZERO,
    val status: AccountStatus = ACTIVE
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
        if (status == DELETED) {
            throw InternalServiceException(ErrorCode.ACCOUNT_STATUS_IS_DELETED)
        }
        return this
    }

    fun checkStatusIsActive(): V1Account {
        if (status != ACTIVE) {
            throw InternalServiceException(ErrorCode.ACCOUNT_STATUS_IS_INVALID, "Account for acquirerId '${acquirer.id}' status is invalid")
        }
        return this
    }

    fun checkSufficientBalance(readyAmount: BigDecimal, amount: BigDecimal): V1Account {
        if (balance < (readyAmount + amount)) {
            throw InternalServiceException(
                errorCode =ErrorCode.ACCOUNT_BALANCE_IS_INSUFFICIENT,
                detailMessage = "balance: $balance < (readyAmount: $readyAmount + amount: $amount)"
            )
        }
        return this
    }

    fun update(predicate: V1AccountPredicate): V1Account {
        return copy(
            acquirer           = predicate.acquirer?.toDomain().ifNull(acquirer),
            currency           = predicate.currency.ifNull(currency),
            balance            = predicate.balance.ifNull(balance),
            minRequiredBalance = predicate.minRequiredBalance.ifNull(minRequiredBalance),
            status             = predicate.status.ifNull(status)
        )
    }

    fun deposit(amount: BigDecimal, quantity: BigDecimal, exchangeRate: BigDecimal): V1Account {
        /**
         * 외화 계좌 입금
         * - 외화 계좌 잔액 증액 처리
         * - 평단가 계산
         *   - 평단가 = ((이전 매입 환율 * 수량) + (현재 매입 환율 * 수량)) / (총 매입 수량)
         */
        val average = ((averageExchangeRate * depositQuantity) + (exchangeRate * quantity)) / (depositQuantity + quantity)
        return copy(
            balance = balance + amount,
            averageExchangeRate = average,
            depositQuantity = depositQuantity + quantity
        )
    }

}