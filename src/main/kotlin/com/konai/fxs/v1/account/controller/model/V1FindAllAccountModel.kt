package com.konai.fxs.v1.account.controller.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.konai.fxs.common.enumerate.AcquirerType
import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.BaseResponse
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate.V1AcquirerPredicate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1FindAllAccountRequest(
    val accountId: Long?,
    val acquirerId: String?,
    val acquirerType: AcquirerType?,
    val acquirerName: String?,
    val currency: String?,
    val pageable: PageableRequest = PageableRequest()
) {

    @get:JsonIgnore
    val acquirer: V1AcquirerPredicate? by lazy {
        V1AcquirerPredicate(acquirerId, acquirerType, acquirerName)
    }

}

data class V1FindAllAccountResponse(
    val pageable: BasePageable.Pageable,
    val content: List<V1AccountModel>
) : BaseResponse<V1FindAllAccountResponse>() {

    constructor(pageable: BasePageable<V1Account>, mapper: (V1Account) -> V1AccountModel): this(
        pageable = pageable.pageable,
        content = pageable.content.map(mapper)
    )

    override fun success(httpStatus: HttpStatus): ResponseEntity<V1FindAllAccountResponse> {
        return ResponseEntity(this, httpStatus)
    }

}