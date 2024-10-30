package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.PageableRequest

class V1FindAllAccountRequestFixture {

    fun make(
        accountId: Long? = null,
        acquirerId: String? = null,
        acquirerType: AcquirerType? = null,
        acquirerName: String? = null,
        currency: String? = null,
        pageable: PageableRequest = PageableRequest()
    ): V1FindAllAccountRequest {
        return V1FindAllAccountRequest(
            accountId = accountId,
            acquirerId = acquirerId,
            acquirerType = acquirerType,
            acquirerName = acquirerName,
            currency = currency,
            pageable = pageable,
        )
    }

}