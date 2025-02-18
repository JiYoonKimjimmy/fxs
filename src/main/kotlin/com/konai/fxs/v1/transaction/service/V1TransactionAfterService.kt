package com.konai.fxs.v1.transaction.service

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionAfterService {

    fun publishSaveTransaction(transaction: V1Transaction): V1Transaction

    suspend fun cachingPendingTransaction(transaction: V1Transaction)

    suspend fun cachingCompletedTransaction(transaction: V1Transaction)

}