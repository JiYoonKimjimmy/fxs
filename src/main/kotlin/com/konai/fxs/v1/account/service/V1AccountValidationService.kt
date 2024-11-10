package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.*
import java.math.BigDecimal

interface V1AccountValidationService {

    /**
     * 외화 계좌 상태 확인
     */
    fun checkStatus(acquirer: V1Acquirer, currency: String): V1Account

    /**
     * 외화 계좌 한도 확인
     */
    fun checkLimit(acquirer: V1Acquirer, currency: String, amount: BigDecimal): V1Account

}