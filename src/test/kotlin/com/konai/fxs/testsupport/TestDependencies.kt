package com.konai.fxs.testsupport

import com.konai.fxs.v1.account.repository.FakeV1AccountRepositoryImpl
import com.konai.fxs.v1.account.repository.entity.V1AccountEntityFixture
import com.konai.fxs.v1.account.service.V1AccountManagementService
import com.konai.fxs.v1.account.service.domain.V1AccountMapper

object TestDependencies {

    val v1AccountMapper = V1AccountMapper()

    val fakeV1AccountRepository = FakeV1AccountRepositoryImpl(v1AccountMapper)

    val v1AccountManagementService = V1AccountManagementService(fakeV1AccountRepository)

    val v1AccountEntityFixture = V1AccountEntityFixture()

}