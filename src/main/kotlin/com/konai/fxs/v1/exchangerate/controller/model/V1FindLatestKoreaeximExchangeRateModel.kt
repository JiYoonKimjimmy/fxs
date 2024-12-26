package com.konai.fxs.v1.exchangerate.controller.model

import com.konai.fxs.common.model.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class V1FindLatestKoreaeximExchangeRateResponse(
    val data: V1KoreaeximExchangeRateModel
) : BaseResponse<V1FindLatestKoreaeximExchangeRateResponse>() {
    override fun success(httpStatus: HttpStatus): ResponseEntity<V1FindLatestKoreaeximExchangeRateResponse> {
        return ResponseEntity(this, httpStatus)
    }
}