package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1TransactionWithdrawalCompleteRequest(
    val acquirer: V1AcquirerPredicate,
    val trReferenceId: String
)

data class V1TransactionWithdrawalCompleteResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalCompleteResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalCompleteResponse> {
        return ResponseEntity(this, httpStatus)
    }
}