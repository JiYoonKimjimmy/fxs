package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1UpdateAccountRequest(
    val accountId: Long,
    val acquirerId: String?,
    val acquirerType: AcquirerType?,
    val acquirerName: String?,
    val currency: String?,
    val minimumBalance: Long?
)

data class V1UpdateAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1UpdateAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1UpdateAccountResponse> {
        return ResponseEntity(this, HttpStatus.OK)
    }
}