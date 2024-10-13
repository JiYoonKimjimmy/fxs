package com.konai.fxs.infra.error.exception

import com.konai.fxs.infra.error.ErrorCode

class InternalServiceException(errorCode: ErrorCode) : BaseException(errorCode)