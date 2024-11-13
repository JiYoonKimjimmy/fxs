package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.common.FakeSequenceBaseRepository
import com.konai.fxs.common.ifNotNullEquals
import com.konai.fxs.v1.transaction.repository.entity.V1TransactionEntity
import com.konai.fxs.v1.transaction.service.domain.V1Transaction
import com.konai.fxs.v1.transaction.service.domain.V1TransactionMapper
import com.konai.fxs.v1.transaction.service.domain.V1TransactionPredicate

class FakeV1TransactionRepositoryImpl(
    private val v1TransactionMapper: V1TransactionMapper,
) : V1TransactionRepository, FakeSequenceBaseRepository<V1TransactionEntity>() {

    override fun save(transaction: V1Transaction): V1Transaction {
        return v1TransactionMapper.domainToEntity(transaction)
            .let { super.save(it) }
            .let { v1TransactionMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1TransactionPredicate): V1Transaction? {
        return super.entities.values.find { checkPredicate(predicate, it) }
            ?.let { v1TransactionMapper.entityToDomain(it) }
    }

    private fun checkPredicate(predicate: V1TransactionPredicate, entity: V1TransactionEntity): Boolean {
        return predicate.id.ifNotNullEquals(entity.id)
                && predicate.acquirer?.id.ifNotNullEquals(entity.acquirer.id)
                && predicate.acquirer?.type.ifNotNullEquals(entity.acquirer.type)
                && predicate.acquirer?.name.ifNotNullEquals(entity.acquirer.name)
                && predicate.fromAcquirer?.id.ifNotNullEquals(entity.fromAcquirer?.id)
                && predicate.fromAcquirer?.type.ifNotNullEquals(entity.fromAcquirer?.type)
                && predicate.fromAcquirer?.name.ifNotNullEquals(entity.fromAcquirer?.name)
                && predicate.type.ifNotNullEquals(entity.type)
                && predicate.purpose.ifNotNullEquals(entity.purpose)
                && predicate.channel.ifNotNullEquals(entity.channel)
                && predicate.currency.ifNotNullEquals(entity.currency)
                && predicate.amount.ifNotNullEquals(entity.amount)
                && predicate.exchangeRate.ifNotNullEquals(entity.exchangeRate)
                && predicate.transferDate.ifNotNullEquals(entity.transferDate)
                && predicate.requestBy.ifNotNullEquals(entity.requestBy)
                && predicate.status.ifNotNullEquals(entity.status)
    }

}