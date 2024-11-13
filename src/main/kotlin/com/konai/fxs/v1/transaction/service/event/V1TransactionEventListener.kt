package com.konai.fxs.v1.transaction.service.event

interface V1TransactionEventListener {

    fun saveTransactionEventHandler(event: V1SaveTransactionEvent)

}