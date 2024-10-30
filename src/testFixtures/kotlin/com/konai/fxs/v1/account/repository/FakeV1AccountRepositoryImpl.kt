package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.FakeSequenceBaseRepository
import com.konai.fxs.common.ifNotNullEquals
import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
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
        return super.entities.values.find { checkPredicate(predicate, it) }
            ?.let { v1AccountMapper.entityToDomain(it) }
    }

    override fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account> {
        val (totalSize, content) = super.findPage(pageable) { checkPredicate(predicate, it) }
        return BasePageable(
            pageable = BasePageable.Pageable(
                numberOfElements = content.size,
                totalElements = totalSize.toLong(),
            ),
            content = content.map(v1AccountMapper::entityToDomain)
        )
    }

    private fun checkPredicate(predicate: V1AccountPredicate, entity: V1AccountEntity): Boolean {
        return predicate.id.ifNotNullEquals(entity.id)
                && predicate.acquirer?.id.ifNotNullEquals(entity.acquirer.id)
                && predicate.acquirer?.type.ifNotNullEquals(entity.acquirer.type)
                && predicate.acquirer?.name.ifNotNullEquals(entity.acquirer.name)
                && predicate.currency.ifNotNullEquals(entity.currency)
                && predicate.balance.ifNotNullEquals(entity.balance)
                && predicate.minRequiredBalance.ifNotNullEquals(entity.minRequiredBalance)
                && predicate.averageExchangeRate.ifNotNullEquals(entity.averageExchangeRate)
    }

}