package com.konai.fxs.v1.transaction.service.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionStatus.*
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1Transaction(
    var id: Long? = null,
    val trReferenceId: String,
    val acquirer: V1Acquirer,
    val fromAcquirer: V1Acquirer?,
    val type: TransactionType,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal,
    val transferDate: String,
    val requestBy: String,
    val requestNote: String?,
    var status: TransactionStatus
) {
    @get:JsonIgnore
    lateinit var account: V1Account

    fun checkAccountStatus(block: (V1Acquirer, String) -> V1Account): V1Transaction {
        return apply {
            account = block(acquirer, currency)
                .also {
                    // `fromAcquirer` 정보 잇는 경우, 검증 처리만 진행
                    fromAcquirer?.let { block(it, currency) }
                }
        }
    }

    fun checkAccountLimit(block: (V1Acquirer, String, BigDecimal) -> V1Account): V1Transaction {
        return apply {
            account = block(acquirer, currency, if (status == PREPARED) BigDecimal.ZERO else amount)
        }
    }

    fun checkCanBeExpired(): V1Transaction {
        if (status == COMPLETED) {
            throw InternalServiceException(ErrorCode.WITHDRAWAL_PREPARED_TRANSACTION_IS_COMPLETED)
        }
        return this
    }

    fun applyTransactionId(block: () -> Long): V1Transaction {
        return apply { id = block() }
    }

    fun changeStatusToPrepared(): V1Transaction {
        return apply { status = PREPARED }
    }

    fun changeStatusToCompleted(): V1Transaction {
        return apply { status = COMPLETED }
    }

    fun changeStatusToExpired(): V1Transaction {
        return apply { status = EXPIRED }
    }

}