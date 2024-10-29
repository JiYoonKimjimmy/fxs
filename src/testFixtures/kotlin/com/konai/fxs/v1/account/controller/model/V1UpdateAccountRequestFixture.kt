package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.testsupport.TestExtensionFunctions.generateSequence

class V1UpdateAccountRequestFixture {

    fun make(
        accountId: Long = generateSequence(),
        acquirerId: String? = null,
        acquirerType: AcquirerType? = null,
        acquirerName: String? = null,
        currency: String? = null,
        minRequiredBalance: Long? = null
    ): V1UpdateAccountRequest {
        return V1UpdateAccountRequest(
            accountId = accountId,
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName,
            currency = currency,
            minRequiredBalance = minRequiredBalance
        )
    }

}