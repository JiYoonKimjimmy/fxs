package com.konai.fxs.v1.transaction.service.domain

import com.konai.fxs.common.DEFAULT_REQUEST_BY
import com.konai.fxs.common.enumerate.*
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.common.enumerate.TransactionStatus.CREATED
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1Transaction(
    val acquirer: V1Acquirer,
    val fromAcquirer: V1Acquirer? = null,
    val type: TransactionType,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal,
    val depositQuantity: BigDecimal = BigDecimal.ZERO,
    val transferDate: String,
    val requestBy: String = DEFAULT_REQUEST_BY,
    val requestNote: String? = null,
    val status: TransactionStatus = CREATED
) {

    fun completed(): V1Transaction {
        return copy(
            status = COMPLETED
        )
    }

}