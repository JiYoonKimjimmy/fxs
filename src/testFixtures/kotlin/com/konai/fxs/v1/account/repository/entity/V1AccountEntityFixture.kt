package com.konai.fxs.v1.account.repository.entity

import java.math.BigDecimal

class V1AccountEntityFixture {

    fun make(
        id: Long? = null,
        acquirer: V1AcquirerEntity
    ): V1AccountEntity {
        return V1AccountEntity(
            id = id,
            acquirer = acquirer,
            currency = "USD",
            balance = BigDecimal.ZERO,
            minRequiredBalance = BigDecimal.ZERO,
            averageExchangeRate = BigDecimal.ZERO
        )
    }

}