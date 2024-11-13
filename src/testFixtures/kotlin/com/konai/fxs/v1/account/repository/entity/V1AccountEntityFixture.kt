package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.Currency
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.testsupport.TestExtensionFunctions.generateAcquirerEntity
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity.V1AcquirerEntity
import java.math.BigDecimal

class V1AccountEntityFixture {

    fun make(
        id: Long? = null,
        acquirer: V1AcquirerEntity = generateAcquirerEntity(),
        currency: String = Currency.USD,
        balance: Long = 0,
        minRequiredBalance: BigDecimal = BigDecimal.ZERO,
        averageExchangeRate: BigDecimal = BigDecimal.ZERO,
        quantity: BigDecimal = BigDecimal.ZERO,
        status: AccountStatus = AccountStatus.ACTIVE,
    ): V1AccountEntity {
        return V1AccountEntity(
            id = id,
            acquirer = acquirer,
            currency = currency,
            balance = BigDecimal(balance),
            minRequiredBalance = minRequiredBalance,
            averageExchangeRate = averageExchangeRate,
            depositAmount = quantity,
            status = status
        )
    }

}