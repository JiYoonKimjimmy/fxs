package com.konai.fxs.v1.transaction.service.event

interface V1TransactionEventListener {

    fun saveTransactionEventHandler(event: V1SaveTransactionEvent)

    fun expireTransactionEventHandler(event: V1ExpireTransactionEvent)

    fun reverseTransactionEventHandler(event: V1ReverseTransactionEvent)

}