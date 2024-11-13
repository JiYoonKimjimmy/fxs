package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.DistributedLockType.ACCOUNT_LOCK
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.event.V1TransactionEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1TransactionDepositServiceImpl(
    private val v1AccountValidationService: V1AccountValidationService,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1TransactionEventPublisher: V1TransactionEventPublisher,
    private val distributedLockManager: DistributedLockManager,
) : V1TransactionDepositService {

    @Transactional
    override fun manualDeposit(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 수기 입금 처리
         * 1. 외화 계좌 상태 확인
         * 2. 외화 계좌 잔액/평균 환율 변경 처리 (with. DistributedLock)
         * 3. 외화 계좌 입금 거래 내역 생성 Event 발행
         */
        // 외화 계좌 상태 확인
        val account = transaction.checkAcquirers(v1AccountValidationService::checkStatus)
        return transaction
            .depositProc(account)
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    private fun V1Transaction.depositProc(account: V1Account): V1Transaction {
        // 외화 계좌 잔액/평균 환율 변경 처리
        distributedLockManager.accountLock(ACCOUNT_LOCK, account) {
            account.deposit(this.amount, this.exchangeRate).let(v1AccountSaveService::save)
        }
        return this
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        return this.also {
            v1TransactionEventPublisher.saveTransaction(it)
        }
    }

}