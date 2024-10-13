package com.konai.fxs.infra.error.exception

import com.konai.fxs.infra.error.ErrorCode

class ResourceNotFoundException(errorCode: ErrorCode) : BaseException(errorCode)