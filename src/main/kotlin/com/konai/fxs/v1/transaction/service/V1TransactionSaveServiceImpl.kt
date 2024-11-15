package com.konai.fxs.v1.transaction.service

import com.konai.fxs.common.retry.RetryableManager
import com.konai.fxs.v1.transaction.repository.V1TransactionRepository
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class V1TransactionSaveServiceImpl(
    private val v1TransactionRepository: V1TransactionRepository,
    private val retryableManager: RetryableManager
) : V1TransactionSaveService {

    @Transactional
    override fun save(transaction: V1Transaction): V1Transaction {
        return retryableManager.retrySaveTransaction(transaction, v1TransactionRepository::save)
    }

}