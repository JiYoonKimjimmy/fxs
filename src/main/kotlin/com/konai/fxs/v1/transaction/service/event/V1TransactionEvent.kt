package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

data class V1SaveTransactionEvent(
    val transaction: V1Transaction
)

data class V1ExpirePreparedTransactionEvent(
    val transaction: V1Transaction
)