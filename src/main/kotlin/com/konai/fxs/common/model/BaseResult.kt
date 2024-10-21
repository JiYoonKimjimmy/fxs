package com.konai.fxs.common.model

import com.konai.fxs.common.COMPONENT_CODE
import com.konai.fxs.common.enumerate.ResultStatus
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.FeatureCode

data class BaseResult(
    val status: ResultStatus = ResultStatus.SUCCESS,
    val code: String? = null,
    val message: String? = null,
    val detailMessage: String? = null,
) {

    constructor(featureCode: FeatureCode, errorCode: ErrorCode, detailMessage: String? = null): this(
        status = ResultStatus.FAILED,
        code = "${COMPONENT_CODE}_${featureCode.code}_${errorCode.code}",
        message = "${featureCode.message} Failed. ${errorCode.message}.",
        detailMessage = detailMessage
    )

}