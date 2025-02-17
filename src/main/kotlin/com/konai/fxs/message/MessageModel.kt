package com.konai.fxs.message

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

open class BaseMessage

data class V1SaveTransactionMessage(
    val transaction: V1Transaction
) : BaseMessage()

data class V1ExpireTransactionMessage(
    val transactionId: Long,
    val amount: Long
) : BaseMessage()

data class V1ExchangeRateCollectorTimerMessage(
    val index: Int,
    val date: String
) : BaseMessage()