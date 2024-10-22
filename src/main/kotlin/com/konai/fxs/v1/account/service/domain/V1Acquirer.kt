package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.AcquirerType

data class V1Acquirer(
    val id: String,
    val type: AcquirerType,
    val name: String = EMPTY
)