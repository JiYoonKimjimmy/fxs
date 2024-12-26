package com.konai.fxs.v1.transaction.service

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionAfterService {

    fun cachingPendingTransaction(transaction: V1Transaction): V1Transaction

    fun cachingCompletedTransaction(transaction: V1Transaction): V1Transaction

    fun publishSaveTransaction(transaction: V1Transaction): V1Transaction

}