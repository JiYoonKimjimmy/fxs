package com.konai.fxs.v1.account.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import org.springframework.stereotype.Service

@Service
class V1AccountValidationServiceImpl(
    private val v1AccountFindService: V1AccountFindService
) : V1AccountValidationService {

    override fun checkStatus(acquirer: V1Acquirer, currency: String): V1Account {
        /**
         * [외화 계좌 상태 확인]
         * 1. 외화 계좌 정보 조회
         * 2. 외화 계좌 상태 ACTIVE 여부 확인
         */
        return v1AccountFindService.findByAcquirer(acquirer, currency)
            ?.checkStatusIsActive()
            ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND)
    }

}