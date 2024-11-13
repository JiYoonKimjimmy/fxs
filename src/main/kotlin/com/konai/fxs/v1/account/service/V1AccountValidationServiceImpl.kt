package com.konai.fxs.v1.account.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1Account.V1Acquirer
import com.konai.fxs.v1.transaction.service.cache.TransactionCacheService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class V1AccountValidationServiceImpl(
    private val v1AccountFindService: V1AccountFindService,
    private val transactionCacheService: TransactionCacheService
) : V1AccountValidationService {

    override fun checkStatus(acquirer: V1Acquirer, currency: String): V1Account {
        /**
         * 외화 계좌 상태 확인
         * 1. 외화 계좌 정보 조회
         * 2. 외화 계좌 상태 ACTIVE 여부 확인
         */
        return v1AccountFindService.findByAcquirer(acquirer, currency)
            ?.checkStatusIsActive()
            ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND, "Account for acquirerId '${acquirer.id}' could not found")
    }

    override fun checkLimit(acquirer: V1Acquirer, currency: String, amount: BigDecimal): V1Account {
        /**
         * 외화 계좌 한도 확인
         * 1. 외화 계좌 조회 & 상태 확인
         * 2. 출금 준비 금액 합계 Cache 정보 조회
         * 3. 외화 계좌 한도 계산
         *  - 계좌 잔액 < (요청 거래 금액 + 출금 준비 금액 합계)
         */
        return with(checkStatus(acquirer, currency)) {
            transactionCacheService.findWithdrawalReadyTotalAmountCache(this.acquirer)
                .let { this.checkSufficientBalance(readyAmount = it, amount = amount) }
        }
    }

}