package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionWithdrawalService {

    fun manualWithdrawal(transaction: V1Transaction): V1Transaction

    fun withdrawal(transaction: V1Transaction): V1Transaction

    fun completeWithdrawal(trReferenceId: String, channel: TransactionChannel): V1Transaction

}