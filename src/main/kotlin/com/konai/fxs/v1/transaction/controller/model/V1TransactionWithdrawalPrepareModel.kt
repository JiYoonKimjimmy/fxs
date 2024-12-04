package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

data class V1TransactionWithdrawalPrepareRequest(
    val acquirer: V1AcquirerPredicate,
    val trReferenceId: String,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal
)

data class V1TransactionWithdrawalPrepareResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalPrepareResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalPrepareResponse> {
        return ResponseEntity(this, httpStatus)
    }
}