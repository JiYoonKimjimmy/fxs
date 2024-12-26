package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.enumerate.TransactionChannel
import com.konai.fxs.common.enumerate.TransactionStatus.COMPLETED
import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.repository.cache.V1TransactionCacheRepository
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class V1TransactionFindServiceImpl(
    private val v1TransactionRepository: V1TransactionRepository,
    private val v1TransactionCacheRepository: V1TransactionCacheRepository
) : V1TransactionFindService {

    override fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction? {
        return v1TransactionRepository.findByPredicate(predicate)
    }

    override fun findWithdrawalTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        // 출금 거래 Cache 정보 조회
        return v1TransactionCacheRepository.findWithdrawalTransactionCache(trReferenceId, channel)
            // 외화 계좌 거래 내역 DB 조회
            ?.let { findByPredicate(predicate = V1TransactionPredicate(id = it)) }
            ?: throw ResourceNotFoundException(ErrorCode.WITHDRAWAL_TRANSACTION_NOT_FOUND)
    }

    override fun findWithdrawalCompletedTransaction(trReferenceId: String, channel: TransactionChannel): V1Transaction {
        return findByPredicate(
                predicate = V1TransactionPredicate(trReferenceId = trReferenceId, channel = channel, status = COMPLETED)
            )
            ?: throw ResourceNotFoundException(ErrorCode.WITHDRAWAL_COMPLETED_TRANSACTION_NOT_FOUND)
    }

}