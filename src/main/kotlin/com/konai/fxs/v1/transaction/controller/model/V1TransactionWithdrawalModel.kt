package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.controller.model.V1AcquirerModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

data class V1TransactionWithdrawalRequest(
    val acquirer: V1AcquirerModel,
    val trReferenceId: String,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal
)

data class V1TransactionWithdrawalResponse(
    val trReferenceId: String
) : BaseResponse<V1TransactionWithdrawalResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionWithdrawalResponse> {
        return ResponseEntity(this, httpStatus)
    }
}