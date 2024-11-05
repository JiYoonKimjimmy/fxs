package com.konai.fxs.v1.account.service

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.*

interface V1AccountValidationService {

    /**
     * 외화 계좌 상태 확인
     * - 외화 계좌 정보 존재 여부 확인
     * - 외화 계좌 정보 상태 확인
     */
    fun checkStatus(acquirer: V1Acquirer, currency: String): V1Account

}