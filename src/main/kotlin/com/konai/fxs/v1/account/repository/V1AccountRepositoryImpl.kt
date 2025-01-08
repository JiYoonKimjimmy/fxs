package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.jdsl.findAll
import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.common.model.BasePageable
import com.konai.fxs.common.model.PageableRequest
import com.konai.fxs.common.util.PageRequestUtil.toBasePageable
import com.konai.fxs.v1.account.repository.jdsl.V1AccountJdslExecutor
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import org.springframework.stereotype.Repository

@Repository
class V1AccountRepositoryImpl(
    private val v1AccountMapper: V1AccountMapper,
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : V1AccountRepository {

    override fun save(account: V1Account): V1Account {
        return v1AccountMapper.domainToEntity(account)
            .let { v1AccountJpaRepository.save(it) }
            .let { v1AccountMapper.entityToDomain(it) }
    }

    override fun saveAndFlush(account: V1Account): V1Account {
        return v1AccountMapper.domainToEntity(account)
            .let { v1AccountJpaRepository.saveAndFlush(it) }
            .let { v1AccountMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account? {
        return V1AccountJdslExecutor(predicate)
            .let { v1AccountJpaRepository.findOne(it.selectQuery()) }
            ?.let { v1AccountMapper.entityToDomain(it) }
    }

    override fun findAllByPredicate(predicate: V1AccountPredicate, pageable: PageableRequest): BasePageable<V1Account> {
        return V1AccountJdslExecutor(predicate)
            .let { v1AccountJpaRepository.findAll(pageable, it.selectQuery()) }
            .toBasePageable(v1AccountMapper::entityToDomain)
    }

}