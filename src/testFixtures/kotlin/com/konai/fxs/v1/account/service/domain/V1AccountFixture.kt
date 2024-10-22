package com.konai.fxs.v1.account.service.domain

import java.math.BigDecimal

class V1AccountFixture {

    fun make(
        id: Long? = null,
        acquirer: V1Acquirer
    ): V1Account {
        return V1Account(
            id = id,
            acquirer = acquirer,
            currency = "USD",
            balance = BigDecimal.ZERO,
            minRequiredBalance = BigDecimal.ZERO,
            averageExchangeRate = BigDecimal.ZERO
        )
    }

}