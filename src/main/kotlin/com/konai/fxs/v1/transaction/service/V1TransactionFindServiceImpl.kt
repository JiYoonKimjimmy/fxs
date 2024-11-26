package com.konai.fxs.v1.transaction.service

import com.konai.fxs.infra.error.ErrorCode
import com.konai.fxs.infra.error.exception.ResourceNotFoundException
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class V1TransactionFindServiceImpl(
    private val v1TransactionRepository: V1TransactionRepository
) : V1TransactionFindService {

    override fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction {
        return v1TransactionRepository.findByPredicate(predicate)
            ?: throw ResourceNotFoundException(ErrorCode.ACCOUNT_TRANSACTION_NOT_FOUND)
    }

}