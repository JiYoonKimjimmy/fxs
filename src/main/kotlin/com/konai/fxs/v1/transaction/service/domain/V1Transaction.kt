package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1Transaction(
    val id: Long? = null,
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
    val status: TransactionStatus
) {

    fun checkAcquirerStatus(block: (V1Acquirer, String) -> V1Account): V1Account {
        return block(acquirer, currency)
            // `fromAcquirer` 정보 잇는 경우, 검증 처리만 진행
            .also { fromAcquirer?.let { block(it, currency) } }
    }

    fun checkAcquirerLimit(block: (V1Acquirer, String, BigDecimal) -> V1Account): V1Account {
        return block(acquirer, currency, amount)
    }

    fun changeStatusToCompleted(transactionId: Long): V1Transaction {
        return copy(
            id = transactionId,
            status = COMPLETED
        )
    }

}