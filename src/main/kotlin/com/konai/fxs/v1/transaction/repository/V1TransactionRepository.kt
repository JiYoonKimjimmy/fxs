package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate

interface V1TransactionRepository {

    fun save(transaction: V1Transaction): V1Transaction

    fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction?

}