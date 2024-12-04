package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.controller.model.V1AcquirerModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1TransactionWithdrawalCompleteRequest(
    val acquirer: V1AcquirerModel,
    val trReferenceId: String
)

data class V1TransactionWithdrawalCompleteResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalCompleteResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalCompleteResponse> {
        return ResponseEntity(this, httpStatus)
    }
}