package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1TransactionWithdrawalCancelRequest(
    val trReferenceId: String,
    val orgTrReferenceId: String,
    val channel: TransactionChannel
)

data class V1TransactionWithdrawalCancelResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalCancelResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalCancelResponse> {
        return ResponseEntity(this, httpStatus)
    }
}