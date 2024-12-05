package com.konai.fxs.v1.transaction.service

interface V1TransactionExpireService {

    fun expireTransaction(transactionId: Long, amount: Long)

}