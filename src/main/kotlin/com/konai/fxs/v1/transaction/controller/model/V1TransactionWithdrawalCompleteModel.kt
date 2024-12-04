package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1TransactionWithdrawalCompleteRequest(
    val trReferenceId: String,
    val channel: TransactionChannel
)

data class V1TransactionWithdrawalCompleteResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalCompleteResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalCompleteResponse> {
        return ResponseEntity(this, httpStatus)
    }
}