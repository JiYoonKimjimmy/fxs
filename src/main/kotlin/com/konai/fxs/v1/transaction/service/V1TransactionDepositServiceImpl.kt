package com.konai.fxs.v1.transaction.service

import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1TransactionDepositServiceImpl(
    private val v1AccountValidationService: V1AccountValidationService,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1TransactionAfterService: V1TransactionAfterService,
) : V1TransactionDepositService {

    @Transactional
    override fun deposit(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 수기 입금 처리
         * 1. 외화 계좌 상태 확인
         * 2. 외화 계좌 잔액/평균 환율 변경 처리
         * 3. 외화 계좌 입금 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountStatus(v1AccountValidationService::checkStatus)
            .changeBalances()
            .depositTransaction()
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.depositTransaction(): V1Transaction {
        // 외화 계좌 잔액/평균 환율 변경 처리
        this.account
            .deposit(this.amount, this.exchangeRate)
            .let(v1AccountSaveService::saveAndFlush)
        return this
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        return v1TransactionAfterService.publishSaveTransaction(this)
    }

}