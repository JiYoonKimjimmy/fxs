package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1CheckLimitAccountRequest(
    val acquirerId: String,
    val acquirerType: AcquirerType,
    val currency: String,
    val amount: Long
)

class V1CheckLimitAccountResponse : BaseResponse<V1CheckLimitAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1CheckLimitAccountResponse> {
        return ResponseEntity(this, httpStatus
        )
    }
}