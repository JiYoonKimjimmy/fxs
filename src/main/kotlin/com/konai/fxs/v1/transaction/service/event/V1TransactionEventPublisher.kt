package com.konai.fxs.v1.transaction.service.event

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionEventPublisher {

    fun saveTransaction(transaction: V1Transaction)

    fun expireTransaction(transaction: V1Transaction)

}