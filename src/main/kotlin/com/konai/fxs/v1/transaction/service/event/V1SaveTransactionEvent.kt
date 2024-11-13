package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.enumerate.TransactionStatus
import com.konai.fxs.common.enumerate.TransactionType
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

data class V1SaveTransactionEvent(
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
)