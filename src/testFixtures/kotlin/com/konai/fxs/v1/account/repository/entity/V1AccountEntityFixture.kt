package com.konai.fxs.v1.account.repository.entity

import com.konai.fxs.testsupport.TestExtensionFunctions

class V1AccountEntityFixture {

    fun make(
        id: Long? = null,
        accountNumber: String = TestExtensionFunctions.generateUUID()
    ): V1AccountEntity {
        return V1AccountEntity(
            id = id,
            accountNumber = accountNumber
        )
    }

}