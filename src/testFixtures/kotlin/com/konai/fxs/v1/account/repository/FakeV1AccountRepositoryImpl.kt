package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.FakeSequenceBaseRepository
import com.konai.fxs.common.ifNotNullEquals
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate

class FakeV1AccountRepositoryImpl(
    private val v1AccountMapper: V1AccountMapper
) : V1AccountRepository, FakeSequenceBaseRepository<V1AccountEntity>() {

    override fun save(domain: V1Account): V1Account {
        return v1AccountMapper.domainToEntity(domain)
            .let { super.save(it) }
            .let {v1AccountMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account? {
        return super.entities.values.find {
            ifNotNullEquals(predicate.id, it.id)
                && ifNotNullEquals(predicate.acquirer?.id, it.acquirer.id)
                && ifNotNullEquals(predicate.acquirer?.type, it.acquirer.type)
                && ifNotNullEquals(predicate.acquirer?.name, it.acquirer.name)
                && ifNotNullEquals(predicate.currency, it.currency)
                && ifNotNullEquals(predicate.balance, it.balance)
                && ifNotNullEquals(predicate.minRequiredBalance, it.minRequiredBalance)
                && ifNotNullEquals(predicate.averageExchangeRate, it.averageExchangeRate)
            }
            ?.let { v1AccountMapper.entityToDomain(it) }
    }

}