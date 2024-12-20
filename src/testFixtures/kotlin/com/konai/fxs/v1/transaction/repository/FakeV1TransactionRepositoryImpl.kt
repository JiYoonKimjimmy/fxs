package com.konai.fxs.v1.transaction.repository

import com.konai.fxs.testsupport.FakeSequenceBaseRepository
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
        return predicate.id                       .ifNotNullEquals(entity.id)
                && predicate.baseAcquirer?.id     .ifNotNullEquals(entity.baseAcquirer.id)
                && predicate.baseAcquirer?.type   .ifNotNullEquals(entity.baseAcquirer.type)
                && predicate.baseAcquirer?.name   .ifNotNullEquals(entity.baseAcquirer.name)
                && predicate.targetAcquirer?.id   .ifNotNullEquals(entity.targetAcquirer?.id)
                && predicate.targetAcquirer?.type .ifNotNullEquals(entity.targetAcquirer?.type)
                && predicate.targetAcquirer?.name .ifNotNullEquals(entity.targetAcquirer?.name)
                && predicate.trReferenceId        .ifNotNullEquals(entity.trReferenceId)
                && predicate.type                 .ifNotNullEquals(entity.type)
                && predicate.purpose              .ifNotNullEquals(entity.purpose)
                && predicate.channel              .ifNotNullEquals(entity.channel)
                && predicate.currency             .ifNotNullEquals(entity.currency)
                && predicate.amount               .ifNotNullEquals(entity.amount)
                && predicate.exchangeRate         .ifNotNullEquals(entity.exchangeRate)
                && predicate.transferDate         .ifNotNullEquals(entity.transferDate)
                && predicate.requestBy            .ifNotNullEquals(entity.requestBy)
                && predicate.status               .ifNotNullEquals(entity.status)
                && predicate.orgTrReferenceId     .ifNotNullEquals(entity.orgTrReferenceId)
    }

}