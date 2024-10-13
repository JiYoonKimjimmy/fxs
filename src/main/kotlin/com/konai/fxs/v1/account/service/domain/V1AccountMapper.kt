package com.konai.fxs.v1.account.service.domain

import com.konai.fxs.v1.account.repository.entity.V1AccountEntity
import org.springframework.stereotype.Component

@Component
class V1AccountMapper {

    fun domainToEntity(domain: V1Account): V1AccountEntity {
        return V1AccountEntity(
            id = domain.id,
            accountNumber = domain.accountNumber
        )
    }

    fun entityToDomain(entity: V1AccountEntity): V1Account {
        return V1Account(
            id = entity.id,
            accountNumber = entity.accountNumber
        )
    }

}