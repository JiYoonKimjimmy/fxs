package com.konai.fxs.v1.account.controller.model

import com.konai.fxs.common.EMPTY
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.InternalServiceException
import com.konai.fxs.v1.account.service.domain.V1Acquirer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1FindOneAccountRequest(
    val accountId: Long?,
    val acquirerId: String?,
    val acquirerType: AcquirerType?
) {

    val acquirer: V1Acquirer? by lazy {
        takeIf {
            it.acquirerId != null && it.acquirerType != null
        }?.let {
            V1Acquirer(id = this.acquirerId!!, type = this.acquirerType!!, name = EMPTY)
        }
    }

    init {
        if (accountId == null && (acquirerId == null || acquirerType == null)) {
            throw InternalServiceException(ErrorCode.ARGUMENT_NOT_VALID_ERROR)
        }
    }

}

data class V1FindOneAccountResponse(
    val data: V1AccountModel
) : BaseResponse<V1FindOneAccountResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1FindOneAccountResponse> {
        return ResponseEntity(this, httpStatus)
    }
}