package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1AccountTransactionService {

    fun manualDeposit(transaction: V1Transaction): V1Transaction

    fun manualWithdrawal(transaction: V1Transaction): V1Transaction

    fun withdrawalPending(transaction: V1Transaction): V1Transaction

    fun withdrawalComplete(trReferenceId: String, channel: TransactionChannel): V1Transaction

    fun withdrawalCancel(trReferenceId: String, orgTrReferenceId: String, channel: TransactionChannel): V1Transaction

}