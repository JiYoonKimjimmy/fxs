package com.konai.fxs.v1.transaction.service

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface V1TransactionDepositService {

    fun deposit(transaction: V1Transaction): V1Transaction

}