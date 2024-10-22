package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1CreateAccountRequest(
    val acquirerId: String,
    val acquirerType: AcquirerType,
    val acquirerName: String,
    val currency: String,
    val minRequiredBalance: Long
)

data class V1CreateAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1CreateAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1CreateAccountResponse> {
        return ResponseEntity(this, httpStatus)
    }
}