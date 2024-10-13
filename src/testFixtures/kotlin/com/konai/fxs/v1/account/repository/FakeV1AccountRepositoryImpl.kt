package com.konai.fxs.v1.account.repository

import com.konai.fxs.common.FakeBaseRepository
import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import com.konai.fxs.v1.account.service.domain.V1Account
import com.konai.fxs.v1.account.service.domain.V1AccountMapper

class FakeV1AccountRepositoryImpl(
    private val v1AccountMapper: V1AccountMapper
) : V1AccountRepository, FakeBaseRepository<V1AccountEntity>() {

    override fun save(domain: V1Account): V1Account {
        return v1AccountMapper.domainToEntity(domain)
            .let { super.save(it) }
            .let {v1AccountMapper.entityToDomain(it) }
    }

    override fun findOne(id: Long): V1Account? {
        return super.findById(id)
            ?.let { v1AccountMapper.entityToDomain(it) }
    }

}