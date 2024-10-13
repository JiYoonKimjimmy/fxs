package com.konai.fxs.infra.error.exception

import com.konai.fxs.infra.error.ErrorCode

class RestClientServiceException(errorCode: ErrorCode) : BaseException(errorCode) {

    constructor(errorCode: ErrorCode, message: String?): this(errorCode) {
        this.detailMessage = message
    }

}