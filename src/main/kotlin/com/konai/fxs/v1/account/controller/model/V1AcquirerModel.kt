package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer

data class V1AcquirerModel(
    val acquirerId: String,
    val acquirerType: AcquirerType,
    val acquirerName: String,
) {
    fun toDomain(): V1Acquirer {
        return V1Acquirer(id = acquirerId, type = acquirerType, name = acquirerName)
    }
}