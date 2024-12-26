package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.v1.account.service.V1AccountSaveService
import com.konai.fxs.v1.account.service.V1AccountValidationService
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1TransactionWithdrawalServiceImpl(
    private val v1AccountValidationService: V1AccountValidationService,
    private val v1AccountSaveService: V1AccountSaveService,
    private val v1TransactionFindService: V1TransactionFindService,
    private val v1TransactionAfterService: V1TransactionAfterService,
) : V1TransactionWithdrawalService {

    @Transactional
    override fun withdrawal(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 잔액 출금 처리
         * 3. 외화 계좌 출금 완료 거래 내역 생성
         * 4. 외화 계좌 출금 완료 거래 내역 생성 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .changeBalances()
            .withdrawalProcessed()
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun pending(transaction: V1Transaction): V1Transaction {
        /**
         * 외화 계좌 출금 대기 처리
         * 1. 외화 계좌 출금 한도 확인
         * 2. 외화 계좌 출금 거래 `PENDING` 상태 변경
         * 3. 외화 계좌 출금 대기 거래 Cache 저장
         * 4. 외화 계좌 출금 대기 거래 합계 Cache 증액 업데이트
         * 5. 외화 계좌 출금 거래 대기 내역 저장 Event 발행
         */
        return transaction
            .checkAccountLimit(v1AccountValidationService::checkLimit)
            .changeStatusToPending()
            .afterPendingTransaction()
            .publishSaveTransactionEvent()
    }

    @Transactional
    override fun complete(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        /**
         * 외화 계좌 출금 완료 처리
         * 1. 외화 계좌 출금 거래 정보 조회
         * 2. 외화 계좌 출금 처리
         * 3. 외화 계좌 출금 대기 거래 Cache 정보 삭제
         * 4. 외화 계좌 출금 대기 거래 금액 Cache 감액 업데이트
         */
        return findWithdrawalTransaction(trReferenceId, channel)
            .let(this::withdrawal)
            .afterCompletedTransaction()
    }

    @Transactional
    override fun cancel(trReferenceId: String, orgTrReferenceId: String, channel: TransactionChannel, canceledTransactionId: () -> Long): V1Transaction {
        /**
         * 외화 계좌 출금 취소 처리
         * 1. 외화 계좌 출금 완료 거래 정보 조회
         * 2. 외화 계좌 상태 확인
         * 3. 외화 계좌 잔액 입금 처리
         * 4. 외화 계좌 출금 완료 거래 `CANCELED` 상태 변경
         * 5. 외화 계좌 출금 완료 거래 내역 저장 Event 발행
         * 6. 외화 계좌 출금 취소 거래 생성
         * 7. 외화 계좌 출금 취소 거래 내역 저장 Event 발행
         */
        return findWithdrawalCompletedTransaction(orgTrReferenceId, channel)
            .checkAccountStatus(v1AccountValidationService::checkStatus)
            .withdrawalCancelProcessed()
            .changeStatusToCanceled()
            .publishSaveTransactionEvent()
            .changeBalances(true)
            .generateCanceledTransaction(trReferenceId, canceledTransactionId)
    }

    private fun findWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        return v1TransactionFindService.findWithdrawalTransaction(trReferenceId, channel)
    }

    private fun findWithdrawalCompletedTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        return v1TransactionFindService.findWithdrawalCompletedTransaction(trReferenceId, channel)
    }

    private fun V1Transaction.withdrawalProcessed(): V1Transaction {
        // 외화 계좌 잔액 출금 처리
        this.account
            .withdrawal(this.amount)
            .let(v1AccountSaveService::save)
        return this
    }

    private fun V1Transaction.withdrawalCancelProcessed(): V1Transaction {
        // 외화 계좌 잔액 입금 처리
        this.account
            .withdrawalCanceled(this.amount)
            .let(v1AccountSaveService::save)
        return this
    }

    private fun V1Transaction.afterPendingTransaction(): V1Transaction {
        return v1TransactionAfterService.cachingPendingTransaction(this)
    }

    private fun V1Transaction.afterCompletedTransaction(): V1Transaction {
        return v1TransactionAfterService.cachingCompletedTransaction(this)
    }

    private fun V1Transaction.publishSaveTransactionEvent(): V1Transaction {
        return v1TransactionAfterService.publishSaveTransaction(this)
    }

    private fun V1Transaction.generateCanceledTransaction(trReferenceId: String, canceledTransactionId: () -> Long): V1Transaction {
        return this.toCanceled(trReferenceId)
            .applyTransactionId(canceledTransactionId)
            .changeStatusToCompleted()
            .publishSaveTransactionEvent()
    }

}