package com.konai.fxs.v1.transaction.controller.model

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionPurpose
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

data class V1TransactionManualWithdrawalRequest(
    val fromAcquirer: V1AcquirerPredicate,
    val toAcquirer: V1AcquirerPredicate?,
    val purpose: TransactionPurpose,
    val channel: TransactionChannel,
    val currency: String,
    val amount: BigDecimal,
    val exchangeRate: BigDecimal,
    val transferDate: String,
    val requestBy: String,
    val requestNote: String?
)

data class V1TransactionManualWithdrawalResponse(
    val transactionId: Long
) : BaseResponse<V1TransactionManualWithdrawalResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1TransactionManualWithdrawalResponse> {
        return ResponseEntity(this, httpStatus)
    }
}