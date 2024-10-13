package com.konai.fxs.v1.account.repository

import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper
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

    override fun findOne(id: Long): V1Account? {
        return v1AccountJpaRepository.findById(id).orElse(null)
            .let { v1AccountMapper.entityToDomain(it) }
    }
}