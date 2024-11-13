package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.DEFAULT_REQUEST_BY
import com.konai.fxs.common.enumerate.*
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionStatus.CREATED
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1Transaction(
    val id: Long? = null,
    val acquirer: V1Acquirer,
    val fromAcquirer: V1Acquirer? = null,
    val type: TransactionType,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal,
    val transferDate: String,
    val requestBy: String = DEFAULT_REQUEST_BY,
    val requestNote: String? = null,
    val status: TransactionStatus = CREATED
) {

    fun checkAcquirers(checkAcquirerStatus: (V1Acquirer, String) -> V1Account): V1Account {
        return checkAcquirerStatus(acquirer, currency)
            .also {
                // `fromAcquirer` 정보 잇는 경우, 검증 처리만 진행
                fromAcquirer?.let { checkAcquirerStatus(it, currency) }
            }
    }

    fun changeStatusToCompleted(): V1Transaction {
        return copy(
            status = COMPLETED
        )
    }

}