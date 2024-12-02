package com.konai.fxs.v1.transaction.service

interface V1TransactionExpireService {

    fun expirePreparedTransaction(transactionId: Long, amount: Long)

}