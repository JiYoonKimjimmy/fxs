package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionWithdrawalService {

    fun withdrawal(transaction: V1Transaction): V1Transaction

    fun pending(transaction: V1Transaction): V1Transaction

    fun complete(trReferenceId: String, channel: TransactionChannel): V1Transaction

    fun cancel(
        trReferenceId: String,
        orgTrReferenceId: String,
        channel: TransactionChannel,
        canceledTransactionId: () -> Long
    ): V1Transaction

}