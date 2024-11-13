package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.DistributedLockType.ACCOUNT_LOCK
import com.konai.fxs.common.lock.DistributedLockManager
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service

@Service
class V1TransactionDepositServiceImpl(
    private val v1AccountValidationService: V1AccountValidationService,
    private val v1AccountSaveService: V1AccountSaveService,
    private val distributedLockManager: DistributedLockManager
) : V1TransactionDepositService {

    override fun manualDeposit(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 수기 입금 처리
         * 1. 외화 계좌 상태 확인
         * 2. 외화 계좌 잔액/평균 환율 변경 처리 (with. DistributedLock)
         * 3. 외화 계좌 입금 거래 내역 생성 Event 발행
         */
        // 외화 계좌 상태 확인
        val account = v1AccountValidationService.checkStatus(transaction.acquirer, transaction.currency)
        val fromAccount = transaction.fromAcquirer?.let { v1AccountValidationService.checkStatus(it, transaction.currency) }

        // 외화 계좌 잔액/평균 환율 변경 처리
        distributedLockManager.accountLock(ACCOUNT_LOCK, account) {
            account.deposit(transaction.amount, transaction.depositQuantity, transaction.exchangeRate).let(v1AccountSaveService::save)
        }

        return transaction.completed()
    }

}