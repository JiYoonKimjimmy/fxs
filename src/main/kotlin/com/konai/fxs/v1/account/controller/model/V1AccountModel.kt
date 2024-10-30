package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType

data class V1AccountModel(
    val accountId: Long?,
    val acquirerId: String,
    val acquirerType: AcquirerType,
    val acquirerName: String,
    val currency: String,
    val balance: Long,
    val minRequiredBalance: Long,
    val averageExchangeRate: Double,
    val status: AccountStatus
)