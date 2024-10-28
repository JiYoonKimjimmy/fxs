package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.jdsl.findOne
import com.konai.fxs.v1.account.repository.entity.V1AcquirerEntity
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
import com.konai.fxs.v1.account.service.domain.V1AccountPredicate
import com.konai.fxs.v1.account.service.domain.V1Acquirer
import org.springframework.stereotype.Repository

@Repository
class V1AccountRepositoryImpl(
    private val v1AccountMapper: V1AccountMapper,
    private val v1AccountJpaRepository: V1AccountJpaRepository
) : V1AccountRepository {

    override fun save(domain: V1Account): V1Account {
        return v1AccountMapper.domainToEntity(domain)
            .let { v1AccountJpaRepository.save(it) }
            .let { v1AccountMapper.entityToDomain(it) }
    }

    override fun findByPredicate(predicate: V1AccountPredicate): V1Account? {
        return v1AccountJpaRepository.findOne(predicate.selectQuery())
            ?.let { v1AccountMapper.entityToDomain(it) }
    }

    override fun existsByAcquirer(acquirer: V1Acquirer): Boolean {
        return V1AcquirerEntity(id = acquirer.id, type = acquirer.type, name = acquirer.name)
            .let { v1AccountJpaRepository.existsByAcquirer(it) }
    }

}