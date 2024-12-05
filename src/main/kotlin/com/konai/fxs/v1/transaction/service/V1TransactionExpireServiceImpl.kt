package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.error
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.transaction.service.cache.V1TransactionCacheService
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class V1TransactionExpireServiceImpl(
    private val v1TransactionFindService: V1TransactionFindService,
    private val v1TransactionSaveService: V1TransactionSaveService,
    private val v1TransactionCacheService: V1TransactionCacheService
) : V1TransactionExpireService {
    // logger
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun expireTransaction(transactionId: Long, amount: Long) {
        /**
         * 외화 계좌 출금 거래 만료 처리
         * 1. 출금 거래 내역 조회
         * 2. 출금 거래 내역 상태 'EXPIRED` 변경 처리
         * 3. 출금 거램 금액 합계 Cache 정보 감액 처리
         */
        try {
            // 출금 중비 거래 내역 조회
            v1TransactionFindService.findByPredicate(V1TransactionPredicate(id = transactionId))
                ?.checkCanBeExpired()
                // 출금 거래 내역 상태 'EXPIRED` 변경 처리
                ?.let { v1TransactionSaveService.save(it.changeStatusToExpired()) }
                // 출금 거램 금액 합계 Cache 정보 감액 처리
                ?.let { v1TransactionCacheService.decrementWithdrawalTransactionPendingAmountCache(it.acquirer, it.amount) }
                ?: throw ResourceNotFoundException(ErrorCode.WITHDRAWAL_TRANSACTION_NOT_FOUND)
        } catch (e: Exception) {
            logger.error(e)
        }
    }

}