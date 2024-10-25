package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.testsupport.TestExtensionFunctions
import java.math.BigDecimal

class V1AccountEntityFixture {

    private val defaultAcquirer: V1AcquirerEntity = V1AcquirerEntity(
        id = TestExtensionFunctions.generateUUID(),
        type = AcquirerType.FX_DEPOSIT,
        name = "외화 예치금 계좌"
    )

    fun make(
        id: Long? = null,
        acquirer: V1AcquirerEntity = defaultAcquirer,
        currency: String = "USD",
        balance: BigDecimal = BigDecimal.ZERO,
        minRequiredBalance: BigDecimal = BigDecimal.ZERO,
        averageExchangeRate: BigDecimal = BigDecimal.ZERO,
    ): V1AccountEntity {
        return V1AccountEntity(
            id = id,
            acquirer = acquirer,
            currency = currency,
            balance = balance,
            minRequiredBalance = minRequiredBalance,
            averageExchangeRate = averageExchangeRate
        )
    }

}