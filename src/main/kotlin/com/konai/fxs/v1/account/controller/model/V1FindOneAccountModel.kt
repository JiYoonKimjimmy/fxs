package com.konai.fxs.v1.account.controller.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1FindOneAccountRequest(
    val accountId: Long?,
    val acquirerId: String?,
    val acquirerType: AcquirerType?
) {

    @get:JsonIgnore
    val acquirer: V1AcquirerPredicate? by lazy {
        if (acquirerId != null && acquirerType != null) {
            V1AcquirerPredicate(id = this.acquirerId, type = this.acquirerType)
        } else {
            null
        }
    }

    fun validation(): V1FindOneAccountRequest {
        if (accountId == null && (acquirerId == null || acquirerType == null)) {
            throw InternalServiceException(ErrorCode.ARGUMENT_NOT_VALID_ERROR)
        }
        return this
    }

}

data class V1FindOneAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1FindOneAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1FindOneAccountResponse> {
        return ResponseEntity(this, httpStatus)
    }
}