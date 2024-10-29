package com.konai.fxs.v1.account.controller.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1CreateAccountRequest(
    val acquirerId: String,
    val acquirerType: AcquirerType,
    val acquirerName: String,
    val currency: String,
    val minRequiredBalance: Long
) {
    @get:JsonIgnore
    val acquirer: V1Acquirer by lazy { V1Acquirer(id = acquirerId, type = acquirerType, name = acquirerName) }
}

data class V1CreateAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1CreateAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1CreateAccountResponse> {
        return ResponseEntity(this, httpStatus)
    }
}