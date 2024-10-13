package com.konai.fxs.v1.account.repository.entity

import java.security.SecureRandom
import java.util.UUID

class V1AccountEntityFixture {

    fun make(
        id: Long? = SecureRandom().nextLong(),
        accountNumber: String = UUID.randomUUID().toString()
    ): V1AccountEntity {
        return V1AccountEntity(
            id = id,
            accountNumber = accountNumber
        )
    }

}