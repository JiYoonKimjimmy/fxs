package com.konai.fxs.v1.account.controller.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.AccountStatus
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1UpdateAccountRequest(
    val accountId: Long,
    val acquirerId: String?,
    val acquirerType: AcquirerType?,
    val acquirerName: String?,
    val currency: String?,
    val minRequiredBalance: Long?,
    val status: AccountStatus?
) {

    @get:JsonIgnore
    val acquirer: V1AcquirerPredicate? by lazy {
        if (acquirerId != null && acquirerType != null && acquirerName != null) {
            V1AcquirerPredicate(acquirerId, acquirerType, acquirerName)
        } else {
            null
        }
    }

    fun validation(): V1UpdateAccountRequest {
        val acquirerFields = listOf(acquirerId, acquirerType, acquirerName)
        if (acquirerFields.any { it != null } && acquirerFields.any { it == null }) {
            // `acquirer` 요청 필드가 하나라도 `null` 아니지만, 그 중 하나라도 `null` 인 경우, 예외 발생
            throw InternalServiceException(ErrorCode.ARGUMENT_NOT_VALID_ERROR)
        }
        return this
    }

}

data class V1UpdateAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1UpdateAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1UpdateAccountResponse> {
        return ResponseEntity(this, HttpStatus.OK)
    }
}