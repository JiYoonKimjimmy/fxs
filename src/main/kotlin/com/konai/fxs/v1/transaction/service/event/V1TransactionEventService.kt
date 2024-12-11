package com.konai.fxs.v1.transaction.service.event

interface V1TransactionEventService {

    fun saveTransaction(event: V1SaveTransactionEvent)

    fun expireTransaction(event: V1ExpireTransactionEvent)

    fun reverseTransaction(event: V1ReverseTransactionEvent)

}