package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate

interface V1TransactionFindService {

    fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction?

    fun findWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction

    fun findWithdrawalCompletedTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction

}