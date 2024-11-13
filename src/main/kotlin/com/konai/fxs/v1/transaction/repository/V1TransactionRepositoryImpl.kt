package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.v1.transaction.repository.jdsl.V1TransactionJdslExecutor
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate
import org.springframework.stereotype.Repository

@Repository
class V1TransactionRepositoryImpl(
    private val v1TransactionMapper: V1TransactionMapper,
    private val v1TransactionJpaRepository: V1TransactionJpaRepository
) : V1TransactionRepository {

    override fun save(transaction: V1Transaction): V1Transaction {
        return v1TransactionMapper.domainToEntity(transaction)
            .let { v1TransactionJpaRepository.save(it) }
            .let { v1TransactionMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction? {
        return V1TransactionJdslExecutor(predicate)
            .let { v1TransactionJpaRepository.findOne(it.selectQuery()) }
            ?.let { v1TransactionMapper.entityToDomain(it) }
    }
}