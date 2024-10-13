package com.konai.fxs.common.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class VoidResponse : BaseResponse<VoidResponse>() {

    override fun success(httpStatus: HttpStatus): ResponseEntity<VoidResponse> {
        return ResponseEntity(this, httpStatus)
    }

}

fun success(httpStatus: HttpStatus): ResponseEntity<VoidResponse> {
    return VoidResponse().success(httpStatus)
}