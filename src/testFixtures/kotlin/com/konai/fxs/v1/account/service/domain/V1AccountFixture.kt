package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.enumerate.AcquirerType.FX_DEPOSIT
import com.konai.fxs.testsupport.TestExtensionFunctions.generateUUID
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import java.math.BigDecimal

class V1AccountFixture {

    fun make(
        id: Long? = null,
        acquirerId: String = generateUUID(),
        acquirerType: AcquirerType = FX_DEPOSIT,
        acquirerName: String = "외화 예치금 계좌"
    ): V1Account {
        return V1Account(
            id = id,
            acquirer = V1Acquirer(acquirerId, acquirerType, acquirerName),
            currency = "USD",
            balance = BigDecimal.ZERO,
            minRequiredBalance = BigDecimal.ZERO,
            averageExchangeRate = BigDecimal.ZERO
        )
    }

}