package com.konai.fxs.common.retry

import com.konai.fxs.v1.transaction.service.domain.V1Transaction

interface RetryableManager {

    fun retrySaveTransaction(transaction: V1Transaction, block: (V1Transaction) -> V1Transaction): V1Transaction

}